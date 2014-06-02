package com.cubbyhole.android.cell;

import com.cubbyhole.client.model.File;
import com.cubbyhole.client.model.Share;

public class ShareCell {
    private Share share;

    public ShareCell(Share share) {
        this.share = share;
    }

    public Share getShare() {
        return share;
    }
}
