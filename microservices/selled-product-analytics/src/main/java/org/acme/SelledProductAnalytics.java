package org.acme;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.List;

public class SelledProductAnalytics {
    public Long id;
    public Long idPurchase;

    public SelledProductAnalytics(
        final Long id,
        final Long idPurchase)
    {
        this.id = id;
        this.idPurchase = idPurchase;
    }
    
    public SelledProductAnalytics() {
    }
    
    public static Multi<SelledProductAnalytics> findAll(MySQLPool client) {
        return client.query("SELECT id, id_purchase FROM SelledProductAnalytics ORDER BY id ASC").execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(SelledProductAnalytics::from);
    }
    
    public static Uni<SelledProductAnalytics> findById(MySQLPool client, Long id) {
        return client.preparedQuery("SELECT id, id_purchase FROM SelledProductAnalytics WHERE id = ?").execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator) 
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null); 
    }

    public static Uni<SelledProductAnalytics> findByPurchaseId(MySQLPool client, Long id) {
        return client.preparedQuery("SELECT id, id_purchase FROM SelledProductAnalytics WHERE id_purchase = ?").execute(Tuple.of(id))
            .onItem().transform(RowSet::iterator)
            .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);
    }

    public static Uni<Boolean> save(MySQLPool client, Long idPurchase) {
        return client
            .preparedQuery(
                "INSERT INTO SelledProductAnalytics(id_purchase) VALUES (?)")
            .execute(Tuple.of(idPurchase))
            .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    public static Uni<Boolean> delete(MySQLPool client, Long id) {
        return client.preparedQuery("DELETE FROM SelledProductAnalytics WHERE id = ?").execute(Tuple.of(id))
            .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    @Override
    public String toString() {
        return "CrossSellingRecommendation{" +
            "id=" + id +
            ", purchaseId=" + idPurchase +
            '}';
    }

    private static SelledProductAnalytics from(Row row) {
        return new SelledProductAnalytics(
            row.getLong("id"),
            row.getLong("id_purchase"));
    }
}
