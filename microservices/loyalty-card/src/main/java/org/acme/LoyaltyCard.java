package org.acme;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

public class LoyaltyCard {
	
	public Long id;
	public Long idCustomer;
	public Long idShop;

	public LoyaltyCard() {
	}

	public LoyaltyCard(Long id, Long idCustomer, Long idShop) {
		this.id = id;
		this.idCustomer = idCustomer;
		this.idShop = idShop;
	}

	public static Multi<LoyaltyCard> findAll(MySQLPool client) {
		return client.query("SELECT id, id_customer, id_shop  FROM LoyaltyCards ORDER BY id ASC").execute()
				.onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
				.onItem().transform(LoyaltyCard::from);
	}

	public static Uni<LoyaltyCard> findById(MySQLPool client, Long id) {
		return client.preparedQuery("SELECT id, id_customer, id_shop  FROM LoyaltyCards WHERE id = ?").execute(Tuple.of(id))
				.onItem().transform(RowSet::iterator)
				.onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);
	}

	public static Uni<LoyaltyCard> findById2(MySQLPool client, Long idCustomer, Long idShop) {
		return client.preparedQuery("SELECT id, id_customer, id_shop FROM LoyaltyCards WHERE id_customer = ? AND id_shop = ?").execute(Tuple.of(idCustomer, idShop))
				.onItem().transform(RowSet::iterator)
				.onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);

	}

	public static Uni<Boolean> save(MySQLPool client, Long idCustomer_R, Long idShop_R)
	{
		return client.preparedQuery("INSERT INTO LoyaltyCards(id_customer,id_shop) VALUES (?,?)").execute(Tuple.of(idCustomer_R , idShop_R))
				.onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1 );
	}

	public static Uni<Boolean> delete(MySQLPool client, Long id_R) {
		return client.preparedQuery("DELETE FROM LoyaltyCards WHERE id = ?").execute(Tuple.of(id_R))
				.onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
	}

	public static Uni<Boolean> update(MySQLPool client, Long id_R, Long idCustomer_R , Long idShop_R ) {
		return client.preparedQuery("UPDATE LoyaltyCards SET id_customer = ? , id_shop = ? WHERE id = ?").execute(Tuple.of(idCustomer_R, idShop_R, id_R))
			.onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
	}

	@Override
	public String toString() {
		return "{id:" + id + ", idCustomer:" + idCustomer + ", idShop:" + idShop + "}\n";
	}

	private static LoyaltyCard from(Row row) {
		return new LoyaltyCard(row.getLong("id"), row.getLong("id_customer") , row.getLong("id_shop"));
	}
}
