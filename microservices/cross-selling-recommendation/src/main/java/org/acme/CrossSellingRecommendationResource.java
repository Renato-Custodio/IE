package org.acme;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;

import org.acme.api.ApiCrossSellingRecommendationRequest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import java.net.URI;

@Path("CrossSellingRecommendation")
public class CrossSellingRecommendationResource {

    @Inject
    io.vertx.mutiny.mysqlclient.MySQLPool client;
    
    @Inject
    @ConfigProperty(name = "myapp.schema.create", defaultValue = "true") 
    boolean schemaCreate;

    @ConfigProperty(name = "kafka.bootstrap.servers") 
    String kafkaServers;
    
    void config(@Observes StartupEvent ev) {
        if (schemaCreate) {
            initdb();
        }
    }
    
    private void initdb() {
        // In a production environment this configuration SHOULD NOT be used
        client.query("DROP TABLE IF EXISTS CrossSellingRecommendations").execute()
        .flatMap(r -> client.query("CREATE TABLE CrossSellingRecommendations (id SERIAL PRIMARY KEY, id_loyalty_card BIGINT UNSIGNED, id_shops TEXT NOT NULL, recommendation TEXT NOT NULL)").execute())
        .flatMap(r -> client.query("INSERT INTO CrossSellingRecommendations (id_loyalty_card,id_shops,recommendation) VALUES (1, '[1, 2, 3]', 'Pencil')").execute())
        .flatMap(r -> client.query("INSERT INTO CrossSellingRecommendations (id_loyalty_card,id_shops,recommendation) VALUES (2, '[4, 5, 6]', 'Apple')").execute())
        .flatMap(r -> client.query("INSERT INTO CrossSellingRecommendations (id_loyalty_card,id_shops,recommendation) VALUES (2, '[1, 5, 6]', 'Grapes')").execute())
        .flatMap(r -> client.query("INSERT INTO CrossSellingRecommendations (id_loyalty_card,id_shops,recommendation) VALUES (3, '[3, 4, 6]', 'Cookies')").execute())
            .await().indefinitely();
    }

    @POST
    public Uni<Response> createAndProduce(@RequestBody ApiCrossSellingRecommendationRequest request) {
        //Produce event to topic
        Thread worker = new StaticTopicProducer(kafkaServers , client, request);
        worker.start();

        return CrossSellingRecommendation
            .save(
                client,
                request.idLoyaltyCard(),
                request.idShops(),
                request.recommendation())
            .onItem().transform(id -> URI.create("/CrossSellingRecommendation/" + id))
            .onItem().transform(uri -> Response.created(uri).build());
    }

    @GET
    public Multi<CrossSellingRecommendation> get() {
        return CrossSellingRecommendation.findAll(client);
    }

    @GET
    @Path("{id}")
    public Uni<Response> getSingle(Long id) {
        return CrossSellingRecommendation.findById(client, id)
                .onItem().transform(crossSellingRecommendation -> crossSellingRecommendation != null ? Response.ok(crossSellingRecommendation) : Response.status(Response.Status.NOT_FOUND))
                .onItem().transform(ResponseBuilder::build); 
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(Long id) {
        return CrossSellingRecommendation.delete(client, id)
            .onItem()
            .transform(deleted -> deleted ? Response.Status.NO_CONTENT : Response.Status.NOT_FOUND)
            .onItem().transform(status -> Response.status(status).build());
    }

    @PUT
    @Path("{id}")
    public Uni<Response> update(Long id, @RequestBody ApiCrossSellingRecommendationRequest request) {
        return CrossSellingRecommendation.update(
                client,
                id,
                request.idLoyaltyCard(),
                request.idShops(),
                request.recommendation())
            .onItem()
            .transform(updated -> updated ? Response.Status.NO_CONTENT : Response.Status.NOT_FOUND)
            .onItem().transform(status -> Response.status(status).build());
    }
}


