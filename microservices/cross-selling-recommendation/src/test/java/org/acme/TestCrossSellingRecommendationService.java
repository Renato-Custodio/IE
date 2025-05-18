package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class TestCrossSellingRecommendationService {

    private static final String BASE_ENDPOINT_PATH = "/CrossSellingRecommendation";
    private static final String ID_PLACEHOLDER = "{id}";
    private static final String GET_ENDPOINT_PATH = BASE_ENDPOINT_PATH + "/" + ID_PLACEHOLDER;
    private static final String PUT_ENDPOINT_PATH = BASE_ENDPOINT_PATH + "/" + ID_PLACEHOLDER;
    private static final String DELETE_ENDPOINT_PATH = BASE_ENDPOINT_PATH + "/" + ID_PLACEHOLDER;



    @Inject
    io.vertx.mutiny.mysqlclient.MySQLPool client;

    @BeforeEach
    void resetDatabase() {
        client.query("DELETE FROM CrossSellingRecommendations").execute().await().indefinitely();
        client.query("""
            INSERT INTO CrossSellingRecommendations (id, id_loyalty_card, id_shops) VALUES
            (1, 1, '[1, 2, 3]'),
            (2, 2, '[4, 5, 6]'),
            (3, 2, '[1, 5, 6]'),
            (4, 3, '[3, 4, 6]')
        """).execute().await().indefinitely();
    }

    @Test
    void testGetMultipleCrossSellingRecommendations() {
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
    void testGetSingleCrossSellingRecommendation() {
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
    void testCreateAndProduceCrossSellingRecommendation() {
        final String payload = """
            {
            	"idLoyaltyCard": 5,
                "idShops": [4, 7, 8]
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
    void testUpdateCrossSellingRecommendation() {
        final Long id = 1L;
        final String payload = """
            {
            	"idLoyaltyCard": 5,
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
    void testDeleteCrossSellingRecommendation() {
        final Long id = 1L;

        given()
            .when()
            .expect()
            .statusCode(204)
            .when()
            .delete(DELETE_ENDPOINT_PATH.replace(ID_PLACEHOLDER, id.toString()));
    }
}
