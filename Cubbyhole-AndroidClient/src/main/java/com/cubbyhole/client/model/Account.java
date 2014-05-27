package com.cubbyhole.client.model;

public class Account {
    private String username;
    private int plan;
    private long home;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getHome() {
        return home;
    }

    public void setHome(long home) {
        this.home = home;
    }

    public int getPlan() {
        return plan;
    }

    public void setPlan(int plan) {
        this.plan = plan;
    }
}
