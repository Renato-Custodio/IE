package org.acme;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;

import org.acme.api.ApiTopicRequest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

@Path("Purchase")
public class PurchaseResource {

    @Inject
    io.vertx.mutiny.mysqlclient.MySQLPool client;
    
    @Inject
    @ConfigProperty(name = "myapp.schema.create", defaultValue = "true") 
    boolean schemaCreate ;

    @ConfigProperty(name = "kafka.bootstrap.servers") 
    String kafka_servers;
    
    void config(@Observes StartupEvent ev) {
        if (schemaCreate) {
            initdb();
        }
    }
    
    private void initdb() {
        // In a production environment this configuration SHOULD NOT be used
        client.query("DROP TABLE IF EXISTS Purchases").execute()
        .flatMap(r -> client.query("CREATE TABLE Purchases (id SERIAL PRIMARY KEY,date_time DATETIME, price FLOAT, product TEXT NOT NULL, supplier TEXT NOT NULL, shop_name TEXT NOT NULL, loyalty_card_id BIGINT UNSIGNED, discount_coupon_id BIGINT UNSIGNED)").execute())
        .flatMap(r -> client.query("INSERT INTO Purchases (date_time,price,product,supplier,shop_name,loyalty_card_id,discount_coupon_id) VALUES ('2038-01-19 03:14:07','12.34','Boots','supplier','ArcoCego',1,NULL)").execute())
        .flatMap(r -> client.query("INSERT INTO Purchases (date_time,price,product,supplier,shop_name,loyalty_card_id,discount_coupon_id) VALUES ('2038-01-19 03:14:07','12.34','Leather','supplier','Lisboa',1,2)").execute())
        .flatMap(r -> client.query("INSERT INTO Purchases (date_time,price,product,supplier,shop_name,loyalty_card_id,discount_coupon_id) VALUES ('2038-01-19 03:14:07','12.34','Bat','supplier','Sintra',1,NULL)").execute())
        .flatMap(r -> client.query("INSERT INTO Purchases (date_time,price,product,supplier,shop_name,loyalty_card_id,discount_coupon_id) VALUES ('2038-01-19 03:14:07','12.34','Ball','supplier','RioDeMouro',1,3)").execute())
            .await().indefinitely();
    }

    @POST
    @Path("Consume")
    public String ProvisioningConsumer(@RequestBody ApiTopicRequest request) {
        Thread worker = new DynamicTopicConsumer(request.topicName(), kafka_servers , client);
        worker.start();
        return "New worker started";
    }

    @GET
    public Multi<Purchase> get() {
        return Purchase.findAll(client);
    }

    @GET
    @Path("/loyaltyCardId/{id}")
    public Multi<Purchase> getByLoyaltyCardId(Long id) {
        return Purchase.findByLoyaltyCardId(client, id);
    }

    @GET
    @Path("/loyaltyCardId/{id}/usedCoupons")
    public Multi<Purchase> getByLoyaltyCardIdByUsedCoupons(Long id) {
        return Purchase.findByLoyaltyCardIdAndCoupon(client, id);
    }

    @GET
    @Path("{id}")
    public Uni<Response> getSingle(Long id) {
        return Purchase.findById(client, id)
                .onItem().transform(purchase -> purchase != null ? Response.ok(purchase) : Response.status(Response.Status.NOT_FOUND)) 
                .onItem().transform(ResponseBuilder::build); 
    }
}


