package org.acme;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CrossSellingRecommendation {
    public Long id;
    public Long idLoyaltyCard;
    public List<Long> idShops;
    public String recommendation;

    public CrossSellingRecommendation(
        final Long id,
        final Long idLoyaltyCard,
        final List<Long> idShops,
        final String recommendation)
    {
        this.id = id;
        this.idLoyaltyCard = idLoyaltyCard;
        this.idShops = idShops;
        this.recommendation = recommendation;
    }
    
    public CrossSellingRecommendation() {
    }
    
    public static Multi<CrossSellingRecommendation> findAll(MySQLPool client) {
        return client.query("SELECT id, id_loyalty_card, id_shops, recommendation FROM CrossSellingRecommendations ORDER BY id ASC").execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(CrossSellingRecommendation::from);
    }
    
    public static Uni<CrossSellingRecommendation> findById(MySQLPool client, Long id) {
        return client.preparedQuery("SELECT id, id_loyalty_card, id_shops, recommendation FROM CrossSellingRecommendations WHERE id = ?").execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator) 
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null); 
    }

    public static Uni<Boolean> save(MySQLPool client, Long idLoyaltyCard, List<Long> idShops, String recommendation) {
        final String shopIdsStr = idShops.toString();
        return client
            .preparedQuery(
                "INSERT INTO CrossSellingRecommendations(id_loyalty_card, id_shops, recommendation) VALUES (?,?,?)")
            .execute(Tuple.of(idLoyaltyCard, shopIdsStr, recommendation))
            .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    public static Uni<Boolean> delete(MySQLPool client, Long id) {
        return client.preparedQuery("DELETE FROM CrossSellingRecommendations WHERE id = ?").execute(Tuple.of(id))
            .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    public static Uni<Boolean> update(MySQLPool client, Long id, Long idLoyaltyCard, List<Long> idShops, String recommendation) {
        final String shopIdsStr = idShops.toString();
        return client.preparedQuery(
                "UPDATE CrossSellingRecommendations SET id_loyalty_card = ? , id_shops = ?, recommendation = ? WHERE id = ?")
            .execute(Tuple.of(idLoyaltyCard, shopIdsStr, recommendation, id))
            .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    @Override
    public String toString() {
        return "CrossSellingRecommendation{" +
            "id=" + id +
            ", loyaltyCardId=" + idLoyaltyCard +
            ", shopIds=" + idShops +
            ", recommendation=" + recommendation +
            '}';
    }

    private static CrossSellingRecommendation from(Row row) {
        return new CrossSellingRecommendation(
            row.getLong("id"),
            row.getLong("id_loyalty_card"),
            parseShopIds(row.getString("id_shops")),
            row.getString("recommendation"));
    }

    private static List<Long> parseShopIds(String shopIds) {
        final String shopIdsWithoutBrackets = shopIds.substring(1, shopIds.length() - 1);
        final String[] parts = shopIdsWithoutBrackets.split(", ");

        List<Long> shopIdList = new ArrayList<>();
        for (final String part : parts) {
            shopIdList.add(Long.parseLong(part));
        }
        return shopIdList;
    }
}
