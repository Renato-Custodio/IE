package org.acme;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

import java.time.LocalDateTime;

public class Purchase 
{
    public Long id;
    public java.time.LocalDateTime timestamp;
    public Float price;
    public String product;
    public String supplier;
    public String shopName;
    public Long loyaltyCardId;
    public Long discountCouponId;

    public Purchase(Long id, LocalDateTime timestamp, Float price, String product, String supplier, String shopName, Long loyaltyCardId, Long discountCouponId) {
        this.id = id;
        this.timestamp = timestamp;
        this.price = price;
        this.product = product;
        this.supplier = supplier;
        this.shopName = shopName;
        this.loyaltyCardId = loyaltyCardId;
        this.discountCouponId = discountCouponId;
    }
    
    public Purchase() {
    }
    
    public static Multi<Purchase> findAll(MySQLPool client) {
        return client.query("SELECT id, date_time, price, product , supplier, shop_name, loyalty_card_id, discount_coupon_id FROM Purchases ORDER BY id ASC").execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(Purchase::from);
    }
    
    public static Uni<Purchase> findById(MySQLPool client, Long id) {
        return client.preparedQuery("SELECT id, date_time, price, product , supplier, shop_name, loyalty_card_id, discount_coupon_id FROM Purchases WHERE id = ?").execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator) 
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null); 
    }

    public static Multi<Purchase> findByLoyaltyCardId(MySQLPool client, Long loyaltyCardId) {
        return client.preparedQuery("SELECT id, date_time, price, product, supplier, shop_name, loyalty_card_id, discount_coupon_id FROM Purchases WHERE loyalty_card_id = ?").execute(Tuple.of(loyaltyCardId))
            .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
            .onItem().transform(Purchase::from);
    }

    public static Multi<Purchase> findByLoyaltyCardIdAndCoupon(MySQLPool client, Long loyaltyCardId) {
    return client
        .preparedQuery("SELECT id, date_time, price, product, supplier, shop_name, loyalty_card_id, discount_coupon_id FROM Purchases WHERE loyalty_card_id = ? AND discount_coupon_id IS NOT NULL")
        .execute(Tuple.of(loyaltyCardId))
        .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
        .onItem().transform(Purchase::from);
    }

    @Override
    public String toString() {
        return "{id=" + id + ", timestamp=" + timestamp.toString() + ", price=" + price + ", product=" + product + ", supplier="
            + supplier + ", shopName=" + shopName + ", loyaltyCardId=" + loyaltyCardId + ", discountCouponId=" + discountCouponId +  "}";
    }

    private static Purchase from(Row row) {
        return new Purchase(
            row.getLong("id"),
            row.getLocalDateTime("date_time"),
            row.getFloat("price") ,
            row.getString("product"),
            row.getString("supplier"),
            row.getString("shop_name"),
            row.getLong("loyalty_card_id"),
            row.getLong("discount_coupon_id"));
    }
}
