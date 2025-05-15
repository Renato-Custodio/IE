package org.acme;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

public class CrossSellingRecommendation
{
    public Long id;

    public String name;

    public CrossSellingRecommendation(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public CrossSellingRecommendation() {
    }

    @Override
    public String toString() {
        return "{id=" + id +
                ",name:" + name +"}";
    }
    
    public static Multi<CrossSellingRecommendation> findAll(MySQLPool client) {
        return client.query("SELECT id, name FROM CrossSellingRecommendations ORDER BY id ASC").execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(CrossSellingRecommendation::from);
    }
    
    public static Uni<CrossSellingRecommendation> findById(MySQLPool client, Long id) {
        return client.preparedQuery("SELECT id, name FROM CrossSellingRecommendations WHERE id = ?").execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator) 
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null); 
    }

    private static CrossSellingRecommendation from(Row row) {
        return new CrossSellingRecommendation(
            row.getLong("id"),
            row.getString("name"));
    }
}
