package org.acme.model;

import java.util.List;

public class Message {

    private String asText;
    private String seqKey;
    private Long id;
    private Long loyaltyCardId;
    private List<Long> shopIds;

    public Message() {}

    public String getAsText() {
        return asText;
    }

    public void setAsText(String asText) {
        this.asText = asText;
    }

    public String getSeqKey() {
        return seqKey;
    }

    public void setSeqKey(String seqKey) {
        this.seqKey = seqKey;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLoyaltyCardId() {
        return loyaltyCardId;
    }

    public void setLoyaltyCardId(Long loyaltyCardId) {
        this.loyaltyCardId = loyaltyCardId;
    }

    public List<Long> getShopIds() {
        return shopIds;
    }

    public void setShopIds(List<Long> shopIds) {
        this.shopIds = shopIds;
    }
}
