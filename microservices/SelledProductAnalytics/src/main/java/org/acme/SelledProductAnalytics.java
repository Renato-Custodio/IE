package org.acme;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

public class SelledProductAnalytics {

	public Long id;
	public Long idCustomer;
	public Long idShop;
	public Long idPurchase;
	public Long idLoyaltyCard;
	public Long idCoupon;
	public String location;

	public SelledProductAnalytics() {
	}

	public SelledProductAnalytics(Long id, Long idCustomer, Long idShop, Long idPurchase, Long idLoyaltyCard,
			Long idCoupon, String location) {
		this.id = id;
		this.idCustomer = idCustomer;
		this.idShop = idShop;
		this.idPurchase = idPurchase;
		this.idLoyaltyCard = idLoyaltyCard;
		this.idCoupon = idCoupon;
		this.location = location;
	}

	@Override
	public String toString() {
		return "{id:" + id + ", idCustomer:" + idCustomer + ", idShop:" + idShop + ", idPurchase:" + idPurchase
				+ ", idLoyaltyCard:" + idLoyaltyCard
				+ ", idCoupon:" + idCoupon + ", location:" + location + "}\n";
	}

	private static SelledProductAnalytics from(Row row) {
		return new SelledProductAnalytics(row.getLong("id"), row.getLong("idCustomer"), row.getLong("idShop"),
				row.getLong("idPurchase"), row.getLong("idLoyaltyCard"), row.getLong("idCoupon"),
				row.getString("location"));
	}

	public static Multi<SelledProductAnalytics> findAll(MySQLPool client) {
		return client.query(
				"SELECT id, idCustomer, idShop, idPurchase, idLoyaltyCard, idCoupon, location FROM SelledProductAnalytics ORDER BY id ASC")
				.execute()
				.onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
				.onItem().transform(SelledProductAnalytics::from);
	}

	public static Uni<SelledProductAnalytics> findById(MySQLPool client, Long id) {
		return client.preparedQuery(
				"SELECT id, idCustomer, idShop, idPurchase, idLoyaltyCard, idCoupon, location FROM SelledProductAnalytics WHERE id = ?")
				.execute(Tuple.of(id))
				.onItem().transform(RowSet::iterator)
				.onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);
	}

	public static Uni<SelledProductAnalytics> findById2(MySQLPool client, Long idCustomer, Long idShop) {
		return client
				.preparedQuery(
						"SELECT id, idCustomer, idShop, idPurchase, idLoyaltyCard, idCoupon, location FROM SelledProductAnalytics WHERE idCustomer = ? AND idShop = ?")
				.execute(Tuple.of(idCustomer, idShop))
				.onItem().transform(RowSet::iterator)
				.onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);

	}

	public Uni<Boolean> save(MySQLPool client, Long idCustomer_R, Long idShop_R, Long idPurchase_R,
			Long idLoyaltyCard_R, Long idCoupon, String location) {
		return client.preparedQuery(
				"INSERT INTO SelledProductAnalytics(idCustomer, idShop, idPurchase, idLoyaltyCard, idCoupon, location) VALUES (?,?,?,?,?,?)")
				.execute(Tuple.of(idCustomer_R, idShop_R, idPurchase_R, idLoyaltyCard_R, idCoupon, location))
				.onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
	}

	public static Uni<Boolean> delete(MySQLPool client, Long id_R) {
		return client.preparedQuery("DELETE FROM SelledProductAnalytics WHERE id = ?").execute(Tuple.of(id_R))
				.onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
	}

	public static Uni<Boolean> update(MySQLPool client, Long id_R, Long idCustomer_R, Long idShop_R, Long idPurchase_R,
			Long idLoyaltyCard_R, Long idCoupon, String location) {

		Tuple tuple = Tuple.tuple()
				.addValue(idCustomer_R)
				.addValue(idShop_R)
				.addValue(idPurchase_R)
				.addValue(idLoyaltyCard_R)
				.addValue(idCoupon)
				.addValue(location)
				.addValue(id_R);

		return client.preparedQuery(
				"UPDATE SelledProductAnalytics SET idCustomer = ?, idShop = ?, idPurchase = ?, idLoyaltyCard = ?, idCoupon = ?, location = ? WHERE id = ?")
				.execute(tuple)
				.onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
	}
}
