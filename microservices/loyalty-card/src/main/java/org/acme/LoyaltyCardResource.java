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
import org.acme.api.ApiLoyaltyCardRequest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import java.net.URI;

@Path("LoyaltyCard")
public class LoyaltyCardResource {

    @Inject
    io.vertx.mutiny.mysqlclient.MySQLPool client;
    
    @Inject
    @ConfigProperty(name = "myapp.schema.create", defaultValue = "true") 
    boolean schemaCreate ;

    void config(@Observes StartupEvent ev) {
        if (schemaCreate) {
            initdb();
        }
    }
    
    private void initdb() {
        // In a production environment this configuration SHOULD NOT be used
        client.query("DROP TABLE IF EXISTS LoyaltyCards").execute()
        .flatMap(r -> client.query("CREATE TABLE LoyaltyCards (id SERIAL PRIMARY KEY, id_customer BIGINT UNSIGNED, id_shop BIGINT UNSIGNED, CONSTRAINT UC_Loyal UNIQUE (id_customer,id_shop))").execute())
        .flatMap(r -> client.query(" INSERT INTO LoyaltyCards (id_customer,id_shop) VALUES (1,1)").execute())
        .flatMap(r -> client.query(" INSERT INTO LoyaltyCards (id_customer,id_shop) VALUES (2,1)").execute())
        .flatMap(r -> client.query(" INSERT INTO LoyaltyCards (id_customer,id_shop) VALUES (1,3)").execute())
        .flatMap(r -> client.query(" INSERT INTO LoyaltyCards (id_customer,id_shop) VALUES (4,2)").execute())
        .await().indefinitely();
    }
    
    @GET
    public Multi<LoyaltyCard> get() {
        return LoyaltyCard.findAll(client);
    }
    
    @GET
    @Path("{id}")
    public Uni<Response> getSingle(Long id) {
        return LoyaltyCard.findById(client, id)
                .onItem().transform(loyaltyCard -> loyaltyCard != null ? Response.ok(loyaltyCard) : Response.status(Response.Status.NOT_FOUND))
                .onItem().transform(ResponseBuilder::build); 
    }

    @GET
    @Path("{idCustomer}/{idShop}")
    public Uni<Response> getDual(Long idCustomer, Long idShop) {
        return LoyaltyCard.findById2(client, idCustomer, idShop)
                .onItem().transform(loyaltyCard -> loyaltyCard != null ? Response.ok(loyaltyCard) : Response.status(Response.Status.NOT_FOUND))
                .onItem().transform(ResponseBuilder::build); 
    }

    @POST
    public Uni<Response> create(@RequestBody ApiLoyaltyCardRequest request) {
        return LoyaltyCard.save(
            client,
            request.idCustomer(),
            request.idShop())
        .onItem().transform(id -> URI.create("/LoyaltyCard/" + id))
        .onItem().transform(uri -> Response.created(uri).build());
    }
    
    @DELETE
    @Path("{id}")
    public Uni<Response> delete(Long id) {
        return LoyaltyCard.delete(client, id)
                .onItem().transform(deleted -> deleted ? Response.Status.NO_CONTENT : Response.Status.NOT_FOUND)
                .onItem().transform(status -> Response.status(status).build());
    }

    @PUT
    @Path("{id}")
    public Uni<Response> update(Long id, @RequestBody ApiLoyaltyCardRequest request) {
        return LoyaltyCard.update(
            client,
            id,
            request.idCustomer(),
            request.idShop())
        .onItem().transform(updated -> updated ? Response.Status.NO_CONTENT : Response.Status.NOT_FOUND)
        .onItem().transform(status -> Response.status(status).build());
    }
    
}
