package org.acme.model;

import java.time.LocalDateTime;
import java.util.List;

public class Message {

    private String asText;
    private String seqKey;

    private List<Long> idShops;
    private String discount;
    private java.time.LocalDateTime expiryDate;
    private Long idLoyaltyCard;

    public List<Long> getIdShops() {
        return idShops;
    }

    public void setIdShops(List<Long> idShops) {
        this.idShops = idShops;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

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

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}
