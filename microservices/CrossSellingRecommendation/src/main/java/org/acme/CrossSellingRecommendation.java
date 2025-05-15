package org.acme;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

import java.util.List;

public class CrossSellingRecommendation
{
    public Long id;
    public Long loyaltyCardId;
    public List<Long> shopIds;


    public CrossSellingRecommendation(
        final Long id,
        final Long loyaltyCardId,
        final List<Long> shopIds)
    {
        this.id = id;
        this.loyaltyCardId = loyaltyCardId;
        this.shopIds = shopIds;
    }
    
    public CrossSellingRecommendation() {
    }

    public Long getId() {
        return id;
    }

    public Long getLoyaltyCardId() {
        return loyaltyCardId;
    }

    public List<Long> getShopIds() {
        return shopIds;
    }
    
    public static Multi<CrossSellingRecommendation> findAll(MySQLPool client) {
        return client.query("SELECT id, loyaltyCardId, shopIds FROM CrossSellingRecommendations ORDER BY id ASC").execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(CrossSellingRecommendation::from);
    }
    
    public static Uni<CrossSellingRecommendation> findById(MySQLPool client, Long id) {
        return client.preparedQuery("SELECT id, loyaltyCardId, shopIds FROM CrossSellingRecommendations WHERE id = ?").execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator) 
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null); 
    }

    private static CrossSellingRecommendation from(Row row) {
        return new CrossSellingRecommendation(
            row.getLong("id"),
            row.getLong("loyaltyCardId"),
            List.of(row.getArrayOfLongs("shopIds")));
    }

    @Override
    public String toString() {
        return "CrossSellingRecommendation{" +
            "id=" + id +
            ", loyaltyCardId=" + loyaltyCardId +
            ", shopIds=" + shopIds +
            '}';
    }
}
