package org.acme.model;

import java.time.LocalDateTime;

public class Message {

    private String asText;
    private String seqKey;

    private Long idLoyaltyCard;
    private Long idShop;
    private String discountType;
    private java.time.LocalDateTime expiryDate;

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

    public Long getIdLoyaltyCard() {
        return idLoyaltyCard;
    }

    public void setIdLoyaltyCard(Long idLoyaltyCard) {
        this.idLoyaltyCard = idLoyaltyCard;
    }

    public Long getIdShop() {
        return idShop;
    }

    public void setIdShop(Long idShop) {
        this.idShop = idShop;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}
