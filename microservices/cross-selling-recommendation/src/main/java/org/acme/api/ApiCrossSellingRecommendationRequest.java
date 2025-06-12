package org.acme.api;

import java.util.List;

public record ApiCrossSellingRecommendationRequest(Long idLoyaltyCard, List<Long> idShops, String recommendation) {}
