package org.acme.api;

import java.util.List;

public record ApiLoyaltyCardRequest(Long idCustomer, List<Long> idShops) {}
