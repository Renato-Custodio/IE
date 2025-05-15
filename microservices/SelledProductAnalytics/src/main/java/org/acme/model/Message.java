package org.acme.model;

public class Message {

    private String asText;
    private String seqKey;
    private Long id;
    private Long idCustomer;
    private Long idShop;
    private Long idPurchase;
    private Long idLoyaltyCard;
    private Long idCoupon;
    private String location;

    public Message() {
    }

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

    public Long getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(Long idCustomer) {
        this.idCustomer = idCustomer;
    }

    public Long getIdShop() {
        return idShop;
    }

    public void setIdShop(Long idShop) {
        this.idShop = idShop;
    }

    public Long getIdPurchase() {
        return idPurchase;
    }

    public void setIdPurchase(Long idPurchase) {
        this.idPurchase = idPurchase;
    }

    public Long getIdLoyaltyCard() {
        return idLoyaltyCard;
    }

    public void setIdLoyaltyCard(Long idLoyaltyCard) {
        this.idLoyaltyCard = idLoyaltyCard;
    }

    public Long getIdCoupon() {
        return idCoupon;
    }

    public void setIdCoupon(Long idCoupon) {
        this.idCoupon = idCoupon;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
