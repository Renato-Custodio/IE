package org.acme.api;

import java.time.LocalDateTime;
import java.util.List;

public record ApiDiscountCouponRequest(
    Long idLoyaltyCard,
    String discount,
    LocalDateTime expiryDate)
{}
