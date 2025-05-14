package org.acme;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

public class DiscountCoupon {

	public Long id;
	public Long idLoyaltyCard;
	public Long idShop;
	public String discount;
	public java.time.LocalDateTime timestamp;

	public DiscountCoupon() {
	}

	public DiscountCoupon(Long id, Long idLoyaltyCard, Long idShop, String discount,
			java.time.LocalDateTime timestamp) {
		this.id = id;
		this.idLoyaltyCard = idLoyaltyCard;
		this.idShop = idShop;
	}

	@Override
	public String toString() {
		return "{id:" + id + ", idLoyaltyCard:" + idLoyaltyCard + ", idShop:" + idShop + ", discount:" + discount
				+ ", timestamp:" + timestamp.toString() + "}\n";
	}

	private static DiscountCoupon from(Row row) {
		return new DiscountCoupon(row.getLong("id"), row.getLong("idLoyaltyCard"), row.getLong("idShop"),
				row.getString("discount"), row.getLocalDateTime("DateTime"));
	}

	public static Multi<DiscountCoupon> findAll(MySQLPool client) {
		return client
				.query("SELECT id, idLoyaltyCard, idShop, discount, DateTime FROM DiscountCoupons ORDER BY id ASC")
				.execute()
				.onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
				.onItem().transform(DiscountCoupon::from);
	}

	public static Uni<DiscountCoupon> findById(MySQLPool client, Long id) {
		return client
				.preparedQuery(
						"SELECT id, idLoyaltyCard, idShop, discount, DateTime FROM DiscountCoupons WHERE id = ?")
				.execute(Tuple.of(id))
				.onItem().transform(RowSet::iterator)
				.onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);
	}

	public static Uni<DiscountCoupon> findById2(MySQLPool client, Long idLoyaltyCard, Long idShop) {
		return client
				.preparedQuery(
						"SELECT id, idLoyaltyCard, idShop, discount, DateTime FROM DiscountCoupons WHERE idLoyaltyCard = ? AND idShop = ?")
				.execute(Tuple.of(idLoyaltyCard, idShop))
				.onItem().transform(RowSet::iterator)
				.onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);

	}

	public Uni<Boolean> save(MySQLPool client, Long idLoyaltyCard_R, Long idShop_R, String discount,
			java.time.LocalDateTime timestamp) {
		return client
				.preparedQuery(
						"INSERT INTO DiscountCoupons(idLoyaltyCard, idShop, discount, Datetime) VALUES (?,?,?,?)")
				.execute(Tuple.of(idLoyaltyCard_R, idShop_R, discount, timestamp))
				.onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
	}

	public static Uni<Boolean> delete(MySQLPool client, Long id_R) {
		return client.preparedQuery("DELETE FROM DiscountCoupons WHERE id = ?").execute(Tuple.of(id_R))
				.onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
	}

	public static Uni<Boolean> update(MySQLPool client, Long id_R, Long idLoyaltyCard_R, Long idShop_R, String discount,
			java.time.LocalDateTime timestamp) {
		return client.preparedQuery(
				"UPDATE DiscountCoupons SET idLoyaltyCard = ? , idShop = ?, discount = ?, Datetime = ? WHERE id = ?")
				.execute(Tuple.of(idLoyaltyCard_R, idShop_R, discount, timestamp, id_R))
				.onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
	}
}
