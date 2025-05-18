package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class TestCustomerService {

    private static final String BASE_ENDPOINT_PATH = "/Customer";
    private static final String ID_PLACEHOLDER = "{id}";
    private static final String GET_ENDPOINT_PATH = BASE_ENDPOINT_PATH + "/" + ID_PLACEHOLDER;
    private static final String PUT_ENDPOINT_PATH = BASE_ENDPOINT_PATH + "/" + ID_PLACEHOLDER;
    private static final String DELETE_ENDPOINT_PATH = BASE_ENDPOINT_PATH + "/" + ID_PLACEHOLDER;

    @Inject
    io.vertx.mutiny.mysqlclient.MySQLPool client;

    @BeforeEach
    void resetDatabase() {
        client.query("DELETE FROM Customers").execute().await().indefinitely();
        client.query("""
            INSERT INTO Customers (id, name, fiscal_number, location) VALUES
            (1, 'client1','123456','Lisbon'),
            (2, 'client2','987654','Setúbal'),
            (3, 'client3','123987','OPorto'),
            (4, 'client4','987123','Faro')
        """).execute().await().indefinitely();
    }

    @Test
    void testGetMultipleCustomers() {
        given()
            .when()
            .expect()
            .statusCode(200)
            .when()
            .get(BASE_ENDPOINT_PATH)
            .then()
            .assertThat()
            .body("size()", is(4));
    }

    @Test
    void testGetSingleCustomer() {
        final Long id = 1L;

        given()
            .when()
            .expect()
            .statusCode(200)
            .when()
            .get(GET_ENDPOINT_PATH.replace(ID_PLACEHOLDER, id.toString()))
            .then()
            .assertThat()
            .body("id", is(id.intValue()));
    }

    @Test
    void testCreateCustomer() {
        final String payload = """
            {
            	"fiscalNumber": 245370285,
                "location": "Lisboa",
                "name": "João"
            }
            """;

        given()
            .when()
            .contentType("application/json")
            .body(payload)
            .expect()
            .statusCode(201)
            .when()
            .post(BASE_ENDPOINT_PATH);
    }

    @Test
    void testUpdateCustomer() {
        final Long id = 1L;
        final String payload = """
            {
            	"fiscalNumber": 245370285,
                "location": "Lisboa",
                "name": "João"
            }
            """;

        given()
            .when()
            .contentType("application/json")
            .body(payload)
            .expect()
            .statusCode(204)
            .when()
            .put(PUT_ENDPOINT_PATH.replace(ID_PLACEHOLDER, id.toString()));
    }

    @Test
    void testDeleteCustomer() {
        final Long id = 1L;

        given()
            .when()
            .expect()
            .statusCode(204)
            .when()
            .delete(DELETE_ENDPOINT_PATH.replace(ID_PLACEHOLDER, id.toString()));
    }
}
