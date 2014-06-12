package com.cubbyhole.android.cell;

import com.cubbyhole.android.util.CellWrapper;
import com.cubbyhole.client.model.PartialAccount;
import com.cubbyhole.client.model.Share;

public class ShareCell extends CellWrapper<Share> {

    private PartialAccount account;

    public PartialAccount getAccount() {
        return account;
    }

    public void setAccount(PartialAccount account) {
        this.account = account;
    }

    public ShareCell(Share object) {
        super(object);
    }
}
