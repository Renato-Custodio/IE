package org.acme.model;

public class Message {

    private String asText;
    private String seqKey;

    private String name;

    public Message() {}

    public String getAsText() {
        return asText;
    }

    public String getSeqkey() {
        return seqKey;
    }

    public String getName() {
        return name;
    }


    public void setAsText(String asText) {
        this.asText = asText;
    }

    public void setSeqkey(String seqKey) {
        this.seqKey = seqKey;
    }

    public void setName(String name) {
        this.name = name;
    }
}
