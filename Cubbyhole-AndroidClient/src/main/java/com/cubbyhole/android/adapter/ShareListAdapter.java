package com.cubbyhole.android.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.cubbyhole.android.R;
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
    protected void getView(Share object, View view) {
        ((TextView) view.findViewById(R.id.tvPermission)).setText(object.getPermission());
        ((TextView) view.findViewById(R.id.tvAccount)).setText(String.valueOf(object.getAccount()));
    }

}
