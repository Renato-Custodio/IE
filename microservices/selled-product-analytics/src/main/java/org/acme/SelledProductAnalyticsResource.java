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

import org.acme.api.ApiSelledProductAnalyticsRequest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import java.net.URI;

@Path("SelledProductAnalytics")
public class SelledProductAnalyticsResource {

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
        client.query("DROP TABLE IF EXISTS SelledProductAnalytics").execute()
        .flatMap(r -> client.query("CREATE TABLE SelledProductAnalytics (id SERIAL PRIMARY KEY, id_purchase BIGINT UNSIGNED NOT NULL)").execute())
        .flatMap(r -> client.query("INSERT INTO SelledProductAnalytics (id_purchase) VALUES (1)").execute())
        .flatMap(r -> client.query("INSERT INTO SelledProductAnalytics (id_purchase) VALUES (2)").execute())
        .flatMap(r -> client.query("INSERT INTO SelledProductAnalytics (id_purchase) VALUES (2)").execute())
        .flatMap(r -> client.query("INSERT INTO SelledProductAnalytics (id_purchase) VALUES (3)").execute())
            .await().indefinitely();
    }

    @POST
    public Uni<Response> createAndProduce(@RequestBody ApiSelledProductAnalyticsRequest request) {
        //Produce event to topics
        Thread worker = new StaticTopicProducer(kafkaServers , client, request);
        worker.start();

        return SelledProductAnalytics
            .save(
                client,
                request.idPurchase())
            .onItem().transform(id -> URI.create("/SelledProductAnalytics/" + id))
            .onItem().transform(uri -> Response.created(uri).build());
    }

    @GET
    public Multi<SelledProductAnalytics> get() {
        return SelledProductAnalytics.findAll(client);
    }

    @GET
    @Path("{id}")
    public Uni<Response> getSingle(Long id) {
        return SelledProductAnalytics.findById(client, id)
                .onItem().transform(selledProductAnalytics -> selledProductAnalytics != null ? Response.ok(selledProductAnalytics) : Response.status(Response.Status.NOT_FOUND))
                .onItem().transform(ResponseBuilder::build); 
    }

    @GET
    @Path("/purchase/{id}")
    public Uni<Response> getSingleByPurchase(Long id) {
        return SelledProductAnalytics.findByPurchaseId(client, id)
            .onItem().transform(selledProductAnalytics -> selledProductAnalytics != null ? Response.ok(selledProductAnalytics) : Response.status(Response.Status.NOT_FOUND))
            .onItem().transform(ResponseBuilder::build);
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(Long id) {
        return SelledProductAnalytics.delete(client, id)
            .onItem()
            .transform(deleted -> deleted ? Response.Status.NO_CONTENT : Response.Status.NOT_FOUND)
            .onItem().transform(status -> Response.status(status).build());
    }
}


