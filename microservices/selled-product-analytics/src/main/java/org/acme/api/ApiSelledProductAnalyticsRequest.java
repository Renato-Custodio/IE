package org.acme.api;

public record ApiSelledProductAnalyticsRequest(
    String location,
    Long idCustomer,
    Long idShop,
    Long idLoyaltyCard,
    Long idCoupon,
    Long idPurchase)
{}
