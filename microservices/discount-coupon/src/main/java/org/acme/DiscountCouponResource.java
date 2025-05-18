package org.acme;

import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import org.acme.api.ApiDiscountCouponRequest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import java.net.URI;

@Path("DiscountCoupon")
public class DiscountCouponResource {

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
                client.query("DROP TABLE IF EXISTS DiscountCoupons")
                    .execute()
                .flatMap(r -> client.query(
                        "CREATE TABLE DiscountCoupons (id SERIAL PRIMARY KEY, id_loyalty_card BIGINT UNSIGNED, id_shops TEXT NOT NULL, discount TEXT NOT NULL, expiry_date DATETIME)")
                        .execute())
                .flatMap(r -> client.query(
                        "INSERT INTO DiscountCoupons (id_loyalty_card,id_shops,discount,expiry_date) VALUES (1,'[1, 2, 3]','discount1','2038-01-19 03:14:07')")
                        .execute())
                .flatMap(r -> client.query(
                        "INSERT INTO DiscountCoupons (id_loyalty_card,id_shops,discount,expiry_date) VALUES (2,'[1, 2, 3]','discount2','2038-01-19 03:14:07')")
                        .execute())
                .flatMap(r -> client.query(
                        "INSERT INTO DiscountCoupons (id_loyalty_card,id_shops,discount,expiry_date) VALUES (1,'[1, 2, 3]','discount3','2038-01-19 03:14:07')")
                        .execute())
                .flatMap(r -> client.query(
                        "INSERT INTO DiscountCoupons (id_loyalty_card,id_shops,discount,expiry_date) VALUES (4,'[1, 2, 3]','discount4','2038-01-19 03:14:07')")
                        .execute())
                .await().indefinitely();
        }

        @POST
        public Uni<Response> createAndProduce(@RequestBody ApiDiscountCouponRequest request) {
                //Produce event to topic
                Thread worker = new StaticTopicProducer(kafkaServers, client, request);
                worker.start();

                return DiscountCoupon
                    .save(
                        client,
                        request.idLoyaltyCard(),
                        request.idShops(),
                        request.discount(),
                        request.expiryDate())
                    .onItem().transform(id -> URI.create("/DiscountCoupon/" + id))
                    .onItem().transform(uri -> Response.created(uri).build());
        }

        @GET
        public Multi<DiscountCoupon> get() {
                return DiscountCoupon.findAll(client);
        }

        @GET
        @Path("{id}")
        public Uni<Response> getSingle(Long id) {
                return DiscountCoupon.findById(client, id)
                        .onItem()
                        .transform(discountCoupon -> discountCoupon != null ?
                            Response.ok(discountCoupon)
                            : Response.status(Response.Status.NOT_FOUND))
                        .onItem().transform(ResponseBuilder::build);
        }

        @DELETE
        @Path("{id}")
        public Uni<Response> delete(Long id) {
                return DiscountCoupon.delete(client, id)
                        .onItem()
                        .transform(deleted -> deleted ? Response.Status.NO_CONTENT : Response.Status.NOT_FOUND)
                        .onItem().transform(status -> Response.status(status).build());
        }

        @PUT
        @Path("{id}")
        public Uni<Response> update(Long id, @RequestBody ApiDiscountCouponRequest request) {
                return DiscountCoupon.update(
                    client,
                    id,
                    request.idLoyaltyCard(),
                    request.idShops(),
                    request.discount(),
                    request.expiryDate())
                .onItem()
                .transform(updated -> updated ? Response.Status.NO_CONTENT : Response.Status.NOT_FOUND)
                .onItem().transform(status -> Response.status(status).build());
        }
}
