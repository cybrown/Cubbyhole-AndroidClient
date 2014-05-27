package com.cubbyhole.client;

import com.cubbyhole.client.model.Account;

public class CurrentAccountService {

    private Account account;

    public Account get() {
        return account;
    }

    public void set(Account account) {
        this.account = account;
    }
}
