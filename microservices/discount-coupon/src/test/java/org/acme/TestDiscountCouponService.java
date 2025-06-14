package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class TestDiscountCouponService {

    private static final String BASE_ENDPOINT_PATH = "/DiscountCoupon";
    private static final String ID_PLACEHOLDER = "{id}";
    private static final String GET_ENDPOINT_PATH = BASE_ENDPOINT_PATH + "/" + ID_PLACEHOLDER;
    private static final String PUT_ENDPOINT_PATH = BASE_ENDPOINT_PATH + "/" + ID_PLACEHOLDER;
    private static final String DELETE_ENDPOINT_PATH = BASE_ENDPOINT_PATH + "/" + ID_PLACEHOLDER;

    @Inject
    io.vertx.mutiny.mysqlclient.MySQLPool client;

    @BeforeEach
    void resetDatabase() {
        client.query("DELETE FROM DiscountCoupons").execute().await().indefinitely();
        client.query("""
            INSERT INTO DiscountCoupons (id, id_loyalty_card, discount, expiry_date) VALUES
            (1, 1,'discount1','2038-01-19 03:14:07'),
            (2, 2,'discount2','2038-01-19 03:14:07'),
            (3, 1,'discount3','2038-01-19 03:14:07'),
            (4, 4,'discount4','2038-01-19 03:14:07')
        """).execute().await().indefinitely();
    }

    @Test
    void testGetMultipleDiscountCoupons() {
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
    void testGetSingleDiscountCoupon() {
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
    void testCreateAndProduceDiscountCoupon() {
        final String payload = """
            {
            	"idLoyaltyCard": 5,
                "discount": "Percentage",
                "expiryDate": "2038-01-19T03:14:07"
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
    void testUpdateDiscountCoupon() {
        final Long id = 1L;
        final String payload = """
            {
            	"idLoyaltyCard": 5,
                "discount": "Percentage",
                "expiryDate": "2038-01-19T03:14:07"
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
    void testDeleteDiscountCoupon() {
        final Long id = 1L;

        given()
            .when()
            .expect()
            .statusCode(204)
            .when()
            .delete(DELETE_ENDPOINT_PATH.replace(ID_PLACEHOLDER, id.toString()));
    }
}
