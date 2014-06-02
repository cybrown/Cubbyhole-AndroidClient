package com.cubbyhole.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.cubbyhole.android.R;
import com.cubbyhole.android.cell.FileCell;
import com.cubbyhole.android.cell.ShareCell;
import com.cubbyhole.android.fragment.FileListFragment;
import com.cubbyhole.client.model.File;

import java.util.List;


public class ShareListAdapter extends BaseAdapter {

    private final Context context;
    private final List<ShareCell> shares;

    public ShareListAdapter(Context context, List<ShareCell> shares) {
        this.context = context;
        this.shares = shares;
    }

    @Override
    public int getCount() {
        return this.shares.size();
    }

    @Override
    public Object getItem(int i) {
        return this.shares.get(i);
    }

    @Override
    public long getItemId(int i) {
        return this.shares.get(i).getShare().getId();
    }

    @Override
    public View getView(final int i, View arg1, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.cell_share, null);
        TextView tvPermission = (TextView) view.findViewById(R.id.tvPermission);
        TextView tvAccount = (TextView) view.findViewById(R.id.tvAccount);
        tvPermission.setText(this.shares.get(i).getShare().getPermission());
        tvAccount.setText(String.valueOf(this.shares.get(i).getShare().getAccount()));
        return view;
    }
}
