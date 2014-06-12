package com.cubbyhole.android.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.cubbyhole.android.R;
import com.cubbyhole.android.cell.ShareCell;
import com.cubbyhole.android.util.CellWrapper;
import com.cubbyhole.android.util.GenericListAdapter;
import com.cubbyhole.client.model.Share;

import java.util.List;


public class ShareListAdapter extends GenericListAdapter<Share> {

    public ShareListAdapter(Context context, List<CellWrapper<Share>> objects) {
        super(context, objects, R.layout.cell_share);
    }

    @Override
    protected long getId(Share object) {
        return object.getId();
    }

    @Override
    protected void getView(CellWrapper<Share> object, View view) {
        ShareCell shareCell = (ShareCell) object;
        ((TextView) view.findViewById(R.id.tvPermission)).setText(shareCell.get().getPermission());
        ((TextView) view.findViewById(R.id.tvAccount)).setText(shareCell.getAccount().getUsername());
    }

}
