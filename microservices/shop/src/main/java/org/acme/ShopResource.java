package org.acme;

import java.net.URI;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import org.acme.api.ApiShopRequest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

@Path("Shop")
public class ShopResource {

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
        client.query("DROP TABLE IF EXISTS Shops").execute()
        .flatMap(r -> client.query("CREATE TABLE Shops (id SERIAL PRIMARY KEY, name VARCHAR(255) NOT NULL UNIQUE, location TEXT NOT NULL)").execute())
        .flatMap(r -> client.query("INSERT INTO Shops (name,location) VALUES ('ArcoCegoLisbon','Lisboa')").execute())
        .flatMap(r -> client.query("INSERT INTO Shops (name,location) VALUES ('PracadeBocage','Setubal')").execute())
        .flatMap(r -> client.query("INSERT INTO Shops (name,location) VALUES ('PracadaBoavista','Porto')").execute())
        .flatMap(r -> client.query("INSERT INTO Shops (name,location) VALUES ('PracaDomFranciscoGomes','Faro')").execute())
        .await().indefinitely();
    }
    
    @GET
    public Multi<Shop> get() {
        return Shop.findAll(client);
    }
    
    @GET
    @Path("{id}")
    public Uni<Response> getSingle(Long id) {
        return Shop.findById(client, id)
                .onItem().transform(shop -> shop != null ? Response.ok(shop) : Response.status(Response.Status.NOT_FOUND)) 
                .onItem().transform(ResponseBuilder::build); 
    }

    @GET
    @Path("/name/{ShopName}")
    public Uni<Response> getByShopName(String ShopName) {
        return Shop.findByShopName(client, ShopName)
                .onItem().transform(shop -> shop != null ? Response.ok(shop) : Response.status(Response.Status.NOT_FOUND)) 
                .onItem().transform(ResponseBuilder::build); 
    }
     
    @POST
    public Uni<Response> create(@RequestBody ApiShopRequest request) {
        return Shop.save(
            client,
            request.name(),
            request.location())
        .onItem().transform(id -> URI.create("/shop/" + id))
        .onItem().transform(uri -> Response.created(uri).build());
    }
    
    @DELETE
    @Path("{id}")
    public Uni<Response> delete(Long id) {
        return Shop.delete(client, id)
                .onItem().transform(deleted -> deleted ? Response.Status.NO_CONTENT : Response.Status.NOT_FOUND)
                .onItem().transform(status -> Response.status(status).build());
    }

    @PUT
    @Path("{id}")
    public Uni<Response> update(Long id , @RequestBody ApiShopRequest request) {
        return Shop.update(
            client,
            id,
            request.name(),
            request.location())
        .onItem().transform(updated -> updated ? Response.Status.NO_CONTENT : Response.Status.NOT_FOUND)
        .onItem().transform(status -> Response.status(status).build());
    }
    
}
