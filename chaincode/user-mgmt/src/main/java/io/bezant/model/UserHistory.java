package io.bezant.model;

public class UserHistory {
    private String txId;
    private User user;

    public UserHistory(String txId, User user) {
        this.txId = txId;
        this.user = user;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
