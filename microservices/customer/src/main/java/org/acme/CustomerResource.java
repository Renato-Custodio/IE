package org.acme;

import java.net.URI;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import org.acme.api.ApiCustomerRequest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

@Path("Customer")
public class CustomerResource {

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
        client.query("DROP TABLE IF EXISTS Customers").execute()
        .flatMap(r -> client.query("CREATE TABLE Customers (id SERIAL PRIMARY KEY, name TEXT NOT NULL, fiscal_number BIGINT UNSIGNED, location TEXT NOT NULL)").execute())
        .flatMap(r -> client.query("INSERT INTO Customers (name,fiscal_number,location) VALUES ('client1','123456','Lisbon')").execute())
        .flatMap(r -> client.query("INSERT INTO Customers (name,fiscal_number,location) VALUES ('client2','987654','Setúbal')").execute())
        .flatMap(r -> client.query("INSERT INTO Customers (name,fiscal_number,location) VALUES ('client3','123987','OPorto')").execute())
        .flatMap(r -> client.query("INSERT INTO Customers (name,fiscal_number,location) VALUES ('client4','987123','Faro')").execute())
        .await().indefinitely();
    }
    
    @GET
    public Multi<Customer> get() {
        return Customer.findAll(client);
    }
    
    @GET
    @Path("{id}")
    public Uni<Response> getSingle(Long id) {
        return Customer.findById(client, id)
                .onItem().transform(customer -> customer != null ? Response.ok(customer) : Response.status(Response.Status.NOT_FOUND)) 
                .onItem().transform(ResponseBuilder::build); 
    }
     
    @POST
    public Uni<Response> create(@RequestBody ApiCustomerRequest request) {
        return Customer.save(
            client,
            request.name(),
            request.fiscalNumber(),
            request.location())
        .onItem().transform(id -> URI.create("/customer/" + id))
        .onItem().transform(uri -> Response.created(uri).build());
    }
    
    @DELETE
    @Path("{id}")
    public Uni<Response> delete(Long id) {
        return Customer.delete(client, id)
                .onItem().transform(deleted -> deleted ? Response.Status.NO_CONTENT : Response.Status.NOT_FOUND)
                .onItem().transform(status -> Response.status(status).build());
    }

    @PUT
    @Path("{id}")
    public Uni<Response> update(Long id, @RequestBody ApiCustomerRequest request) {
        return Customer.update(
            client,
            id,
            request.name(),
            request.fiscalNumber(),
            request.location())
        .onItem().transform(updated -> updated ? Response.Status.NO_CONTENT : Response.Status.NOT_FOUND)
        .onItem().transform(status -> Response.status(status).build());
    }
}
