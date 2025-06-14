package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class TestLoyaltyCardService {


    private static final String BASE_ENDPOINT_PATH = "/LoyaltyCard";
    private static final String ID_PLACEHOLDER = "{id}";
    private static final String GET_ENDPOINT_PATH = BASE_ENDPOINT_PATH + "/" + ID_PLACEHOLDER;
    private static final String GET_BY_CUSTOMER_ID_ENDPOINT_PATH = BASE_ENDPOINT_PATH + "/" + "customerId" + "/" + ID_PLACEHOLDER;
    private static final String PUT_ENDPOINT_PATH = BASE_ENDPOINT_PATH + "/" + ID_PLACEHOLDER;
    private static final String DELETE_ENDPOINT_PATH = BASE_ENDPOINT_PATH + "/" + ID_PLACEHOLDER;

    @Inject
    io.vertx.mutiny.mysqlclient.MySQLPool client;

    @BeforeEach
    void resetDatabase() {
        client.query("DELETE FROM LoyaltyCards").execute().await().indefinitely();
        client.query("""
            INSERT INTO LoyaltyCards (id, id_customer, id_shops) VALUES
            (1, 1, '[1, 2, 3]'),
            (2, 2, '[1, 2, 3]'),
            (3, 3, '[1, 2, 3]'),
            (4, 4, '[1, 2, 3]')
        """).execute().await().indefinitely();
    }

    @Test
    void testGetMultipleLoyaltyCards() {
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
    void testGetSingleLoyaltyCardById() {
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
    void testGetSingleLoyaltyCardByCustomerId() {
        final Long customerId = 1L;

        given()
            .when()
            .expect()
            .statusCode(200)
            .when()
            .get(GET_BY_CUSTOMER_ID_ENDPOINT_PATH.replace(ID_PLACEHOLDER, customerId.toString()))
            .then()
            .assertThat()
            .body("idCustomer", is(customerId.intValue()));
    }

    @Test
    void testCreateLoyaltyCard() {
        final String payload = """
            {
            	"idCustomer": 5,
                "idShop": 4
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
    void testUpdateLoyaltyCard() {
        final Long id = 1L;
        final String payload = """
            {
            	"idCustomer": 1,
                "idShops": [4, 7, 8]
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
    void testDeleteLoyaltyCard() {
        final Long id = 1L;

        given()
            .when()
            .expect()
            .statusCode(204)
            .when()
            .delete(DELETE_ENDPOINT_PATH.replace(ID_PLACEHOLDER, id.toString()));
    }
}
