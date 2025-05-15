package org.acme;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

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
        .flatMap(r -> client.query("CREATE TABLE CrossSellingRecommendations (id SERIAL PRIMARY KEY, loyalty_card_id BIGINT UNSIGNED, shop_ids TEXT NOT NULL)").execute())
        .flatMap(r -> client.query("INSERT INTO CrossSellingRecommendations (loyalty_card_id,shop_ids) VALUES (1, '[1,2,3]')").execute())
        .flatMap(r -> client.query("INSERT INTO CrossSellingRecommendations (loyalty_card_id,shop_ids) VALUES (2, '[4,5,6]')").execute())
        .await().indefinitely();
    }

    @POST
    public String ProvisioningProducer(CrossSellingRecommendation recommendation) {
        Thread worker = new StaticTopicProducer(kafkaServers , client, recommendation);
        worker.start();
        return "New worker started";
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
}


