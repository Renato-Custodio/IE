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

public class DiscountCoupon {

	public Long id;
	public Long idLoyaltyCard;
	public List<Long> idShops;
	public String discount;
	public LocalDateTime expiryDate;

	public DiscountCoupon() {
	}

	public DiscountCoupon(
		final Long id,
		final Long idLoyaltyCard,
		final List<Long> idShops,
		final String discount,
		final LocalDateTime expiryDate)
	{
		this.id = id;
		this.idLoyaltyCard = idLoyaltyCard;
		this.idShops = idShops;
		this.discount = discount;
		this.expiryDate = expiryDate;
	}

	public static Multi<DiscountCoupon> findAll(MySQLPool client) {
		return client
				.query("SELECT id, id_loyalty_card, id_shops, discount, expiry_date FROM DiscountCoupons ORDER BY id ASC")
				.execute()
				.onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
				.onItem().transform(DiscountCoupon::from);
	}

	public static Uni<DiscountCoupon> findById(MySQLPool client, Long id) {
		return client
				.preparedQuery(
						"SELECT id, id_loyalty_card, id_shops, discount, expiry_date FROM DiscountCoupons WHERE id = ?")
				.execute(Tuple.of(id))
				.onItem().transform(RowSet::iterator)
				.onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);
	}

	public static Uni<Boolean> save(MySQLPool client, Long idLoyaltyCard, List<Long> idShops, String discount, LocalDateTime expiryDate) {
		final String shopIdsStr = idShops.toString();
		return client
				.preparedQuery(
						"INSERT INTO DiscountCoupons(id_loyalty_card, id_shops, discount, expiry_date) VALUES (?,?,?,?)")
				.execute(Tuple.of(idLoyaltyCard, shopIdsStr, discount, expiryDate))
				.onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
	}

	public static Uni<Boolean> delete(MySQLPool client, Long id) {
		return client.preparedQuery("DELETE FROM DiscountCoupons WHERE id = ?").execute(Tuple.of(id))
				.onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
	}

	public static Uni<Boolean> update(MySQLPool client, Long id, Long idLoyaltyCard, List<Long> idShops, String discount, LocalDateTime expiryDate) {
		final String shopIdsStr = idShops.toString();
		return client.preparedQuery(
				"UPDATE DiscountCoupons SET id_loyalty_card = ? , id_shops = ?, discount = ?, expiry_date = ? WHERE id = ?")
				.execute(Tuple.of(idLoyaltyCard, shopIdsStr, discount, expiryDate, id))
				.onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
	}

	@Override
	public String toString() {
		return "{id:" + id + ", idLoyaltyCard:" + idLoyaltyCard + ", idShops:" + idShops + ", discount:" + discount
			+ ", expiryDate:" + expiryDate.toString() + "}\n";
	}

	private static DiscountCoupon from(Row row) {
		return new DiscountCoupon(
			row.getLong("id"),
			row.getLong("id_loyalty_card"),
			parseShopIds(row.getString("id_shops")),
			row.getString("discount"),
			row.getLocalDateTime("expiry_date"));
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
