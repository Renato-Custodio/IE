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
        .flatMap(r -> client.query("CREATE TABLE LoyaltyCards (id SERIAL PRIMARY KEY, id_customer BIGINT UNSIGNED NOT NULL UNIQUE, id_shops TEXT NOT NULL)").execute())
        .flatMap(r -> client.query(" INSERT INTO LoyaltyCards (id_customer,id_shops) VALUES (1,'[1, 2, 3]')").execute())
        .flatMap(r -> client.query(" INSERT INTO LoyaltyCards (id_customer,id_shops) VALUES (2,'[4, 5, 6]')").execute())
        .flatMap(r -> client.query(" INSERT INTO LoyaltyCards (id_customer,id_shops) VALUES (3,'[1, 5, 6]')").execute())
        .flatMap(r -> client.query(" INSERT INTO LoyaltyCards (id_customer,id_shops) VALUES (4,'[3, 4, 6]')").execute())
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
    @Path("customerId/{idCustomer}")
    public Uni<Response> getByCustomerId(Long idCustomer) {
        return LoyaltyCard.findByCustomerId(client, idCustomer)
                .onItem().transform(loyaltyCard -> loyaltyCard != null ? Response.ok(loyaltyCard) : Response.status(Response.Status.NOT_FOUND))
                .onItem().transform(ResponseBuilder::build); 
    }

    @POST
    public Uni<Response> create(@RequestBody ApiLoyaltyCardRequest request) {
        return LoyaltyCard.save(
            client,
            request.idCustomer(),
            request.idShops())
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
            request.idShops())
        .onItem().transform(updated -> updated ? Response.Status.NO_CONTENT : Response.Status.NOT_FOUND)
        .onItem().transform(status -> Response.status(status).build());
    }
    
}
