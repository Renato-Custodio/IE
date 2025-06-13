package org.acme.api;

public record ApiSelledProductAnalyticsRequest(
    String location,
    Long idCustomer,
    String shopName,
    Long idLoyaltyCard,
    Long idCoupon,
    Long idPurchase)
{}
