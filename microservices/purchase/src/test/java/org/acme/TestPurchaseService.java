package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class TestPurchaseService {

    private static final String BASE_ENDPOINT_PATH = "/Purchase";
    private static final String ID_PLACEHOLDER = "{id}";
    private static final String GET_ENDPOINT_PATH = BASE_ENDPOINT_PATH + "/" + ID_PLACEHOLDER;
    private static final String GET_BY_LOYALTY_CARD_ID_ENDPOINT_PATH = BASE_ENDPOINT_PATH + "/loyaltyCardId/" + ID_PLACEHOLDER;
    private static final String POST_ENDPOINT_PATH = BASE_ENDPOINT_PATH + "/Consume";


    @Inject
    io.vertx.mutiny.mysqlclient.MySQLPool client;

    @BeforeEach
    void resetDatabase() {
        client.query("DELETE FROM Purchases").execute().await().indefinitely();
        client.query("""
            INSERT INTO Purchases (id, date_time, price, product, supplier, shop_name, loyalty_card_id) VALUES
            (1, '2038-01-19 03:14:07','12.34','one product','supplier','arco cego',1),
            (2, '2038-01-19 03:14:07','12.34','one product','supplier','arco cego',2),
            (3, '2038-01-19 03:14:07','12.34','one product','supplier','arco cego',2),
            (4, '2038-01-19 03:14:07','12.34','one product','supplier','arco cego',3)
        """).execute().await().indefinitely();
    }

    @Test
    void testGetMultiplePurchases() {
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
    void testGetPurchasesByLoyaltyCardId() {
        final Long loyaltyCardId = 2L;

        given()
            .when()
            .expect()
            .statusCode(200)
            .when()
            .get(GET_BY_LOYALTY_CARD_ID_ENDPOINT_PATH.replace(ID_PLACEHOLDER, loyaltyCardId.toString()))
            .then()
            .assertThat()
            .body("size()", is(2));
    }

    @Test
    void testGetSinglePurchase() {
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
    void testKafkaTopicConsumer() {
        final String payload = """
            {
            	"topicName": "TopicName"
            }
            """;

        given()
            .when()
            .contentType("application/json")
            .body(payload)
            .expect()
            .statusCode(200)
            .when()
            .post(POST_ENDPOINT_PATH)
            .then()
            .assertThat()
            .body(is("New worker started"));
    }
}
