package org.acme;

import java.net.URI;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.MediaType;

@Path("DiscountCoupon")
public class DiscountCouponResource {

    @Inject
    io.vertx.mutiny.mysqlclient.MySQLPool client;

    @Inject
    @ConfigProperty(name = "myapp.schema.create", defaultValue = "true")
    boolean schemaCreate;

    void config(@Observes StartupEvent ev) {
        if (schemaCreate) {
            initdb();
        }
    }

    private void initdb() {
        // In a production environment this configuration SHOULD NOT be used
        client.query("DROP TABLE IF EXISTS DiscountCoupons").execute()
                .flatMap(r -> client.query(
                        "CREATE TABLE DiscountCoupons (id SERIAL PRIMARY KEY, idLoyaltyCard BIGINT UNSIGNED, idShop BIGINT UNSIGNED, CONSTRAINT UC_Loyal UNIQUE (idLoyaltyCard,idShop), discount TEXT NOT NULL, DateTime DATETIME)")
                        .execute())
                .flatMap(r -> client.query(
                        " INSERT INTO DiscountCoupons (idLoyaltyCard,idShop,discount,DateTime) VALUES (1,1,'discount1','2038-01-19 03:14:07')")
                        .execute())
                .flatMap(r -> client.query(
                        " INSERT INTO DiscountCoupons (idLoyaltyCard,idShop,discount,DateTime) VALUES (2,1,'discount2','2038-01-20 03:14:07')")
                        .execute())
                .flatMap(r -> client.query(
                        " INSERT INTO DiscountCoupons (idLoyaltyCard,idShop,discount,DateTime) VALUES (1,3,'discount3','2038-01-21 03:14:07')")
                        .execute())
                .flatMap(r -> client.query(
                        " INSERT INTO DiscountCoupons (idLoyaltyCard,idShop,discount,DateTime) VALUES (4,2,'discount4','2038-01-22 03:14:07')")
                        .execute())
                .await().indefinitely();
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
                .transform(discountCoupon -> discountCoupon != null ? Response.ok(discountCoupon)
                        : Response.status(Response.Status.NOT_FOUND))
                .onItem().transform(ResponseBuilder::build);
    }

    @GET
    @Path("{idLoyaltyCard}/{idShop}")
    public Uni<Response> getDual(Long idLoyaltyCard, Long idShop) {
        return DiscountCoupon.findById2(client, idLoyaltyCard, idShop)
                .onItem()
                .transform(discountCoupon -> discountCoupon != null ? Response.ok(discountCoupon)
                        : Response.status(Response.Status.NOT_FOUND))
                .onItem().transform(ResponseBuilder::build);
    }

    @POST
    public Uni<Response> create(DiscountCoupon discountCoupon) {
        return discountCoupon
                .save(client, discountCoupon.idLoyaltyCard, discountCoupon.idShop, discountCoupon.discount,
                        discountCoupon.timestamp)
                .onItem().transform(id -> URI.create("/discountCoupon/" + id))
                .onItem().transform(uri -> Response.created(uri).build());
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(Long id) {
        return DiscountCoupon.delete(client, id)
                .onItem().transform(deleted -> deleted ? Response.Status.NO_CONTENT : Response.Status.NOT_FOUND)
                .onItem().transform(status -> Response.status(status).build());
    }

    @PUT
    @Path("/{id}/{idLoyaltyCard}/{idShop}/{discount}/{Datetime}")
    public Uni<Response> update(Long id, Long idLoyaltyCard, Long idShop, String discount,
            java.time.LocalDateTime Datetime) {
        return DiscountCoupon.update(client, id, idLoyaltyCard, idShop, discount, Datetime)
                .onItem().transform(updated -> updated ? Response.Status.NO_CONTENT : Response.Status.NOT_FOUND)
                .onItem().transform(status -> Response.status(status).build());
    }

}
