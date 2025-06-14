package org.acme;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.List;

public class LoyaltyCard {
	
	public Long id;
	public Long idCustomer;
	public List<Long> idShops;

	public LoyaltyCard() {
	}

	public LoyaltyCard(
		final Long id,
		final Long idCustomer,
		final List<Long> idShops)
	{
		this.id = id;
		this.idCustomer = idCustomer;
		this.idShops = idShops;
	}

	public static Multi<LoyaltyCard> findAll(MySQLPool client) {
		return client.query("SELECT id, id_customer, id_shops  FROM LoyaltyCards ORDER BY id ASC").execute()
				.onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
				.onItem().transform(LoyaltyCard::from);
	}

	public static Uni<LoyaltyCard> findById(MySQLPool client, Long id) {
		return client.preparedQuery("SELECT id, id_customer, id_shops  FROM LoyaltyCards WHERE id = ?").execute(Tuple.of(id))
				.onItem().transform(RowSet::iterator)
				.onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);
	}

	public static Uni<LoyaltyCard> findByCustomerId(MySQLPool client, Long idCustomer) {
		return client.preparedQuery("SELECT id, id_customer, id_shops FROM LoyaltyCards WHERE id_customer = ?").execute(Tuple.of(idCustomer))
				.onItem().transform(RowSet::iterator)
				.onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);

	}

	public static Uni<Boolean> save(MySQLPool client, Long idCustomer_R, Long idShop_R) {
		final String shopIdsStr = List.of(idShop_R).toString();
		return client.preparedQuery("INSERT INTO LoyaltyCards(id_customer,id_shops) VALUES (?,?)").execute(Tuple.of(idCustomer_R , shopIdsStr))
				.onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1 );
	}

	public static Uni<Boolean> delete(MySQLPool client, Long id_R) {
		return client.preparedQuery("DELETE FROM LoyaltyCards WHERE id = ?").execute(Tuple.of(id_R))
				.onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
	}

	public static Uni<Boolean> update(MySQLPool client, Long id_R, Long idShop_R) {
		return findById(client, id_R)
			.onItem().transformToUni(loyaltyCard -> {
				if (loyaltyCard == null) {
					return Uni.createFrom().item(false);
				}
				// Add the new shop ID to the list
				final List<Long> shopIds = new ArrayList<>(loyaltyCard.getIdShops());
				if (!shopIds.contains(idShop_R)) {
					shopIds.add(idShop_R);
				}
				final String shopIdsStr = shopIds.toString();
				// Perform the DB update
				return client.preparedQuery(
						"UPDATE LoyaltyCards SET id_shops = ? WHERE id = ?")
					.execute(Tuple.of(shopIdsStr, id_R))
					.onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
			});
	}

	@Override
	public String toString() {
		return "{id:" + id + ", idCustomer:" + idCustomer + ", idShops:" + idShops + "}\n";
	}

	private static LoyaltyCard from(Row row) {
		return new LoyaltyCard(
			row.getLong("id"),
			row.getLong("id_customer"),
			parseShopIds(row.getString("id_shops")));
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

	private List<Long> getIdShops() {
		return idShops;
	}
}
