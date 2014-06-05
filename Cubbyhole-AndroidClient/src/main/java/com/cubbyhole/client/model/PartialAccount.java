package com.cubbyhole.client.model;

/**
 * Représentation simplifiée de la classe Account,
 * retournée par certain web services.
 */
public class PartialAccount {
    private long id;
    private String username;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
