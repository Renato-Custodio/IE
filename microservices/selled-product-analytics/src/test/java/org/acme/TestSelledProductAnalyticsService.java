package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class TestSelledProductAnalyticsService {

    private static final String BASE_ENDPOINT_PATH = "/SelledProductAnalytics";
    private static final String ID_PLACEHOLDER = "{id}";
    private static final String GET_ENDPOINT_PATH = BASE_ENDPOINT_PATH + "/" + ID_PLACEHOLDER;
    private static final String GET_BY_PURCHASE_ID_ENDPOINT_PATH = BASE_ENDPOINT_PATH + "/purchase/" + ID_PLACEHOLDER;
    private static final String DELETE_ENDPOINT_PATH = BASE_ENDPOINT_PATH + "/" + ID_PLACEHOLDER;


    @Inject
    io.vertx.mutiny.mysqlclient.MySQLPool client;

    @BeforeEach
    void resetDatabase() {
        client.query("DELETE FROM SelledProductAnalytics").execute().await().indefinitely();
        client.query("""
            INSERT INTO SelledProductAnalytics (id, id_purchase) VALUES
            (1, 1),
            (2, 2),
            (3, 3),
            (4, 4)
        """).execute().await().indefinitely();
    }

    @Test
    void testGetMultipleSelledProductAnalytics() {
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
    void testGetSingleSelledProductAnalytics() {
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
    void testGetSingleSelledProductAnalyticsByPurchaseId() {
        final Long id = 1L;

        given()
            .when()
            .expect()
            .statusCode(200)
            .when()
            .get(GET_BY_PURCHASE_ID_ENDPOINT_PATH.replace(ID_PLACEHOLDER, id.toString()))
            .then()
            .assertThat()
            .body("id", is(id.intValue()));
    }

    @Test
    void testCreateAndProduceSelledProductAnalytics() {
        final String payload = """
            {
            	"idCustomer": 10,
                "idShop": 5,
                "idLoyaltyCard": 7,
                "idCoupon": 8,
                "idPurchase": 1,
                "location": "Lisboa"
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
    void testDeleteSelledProductAnalytics() {
        final Long id = 1L;

        given()
            .when()
            .expect()
            .statusCode(204)
            .when()
            .delete(DELETE_ENDPOINT_PATH.replace(ID_PLACEHOLDER, id.toString()));
    }
}
