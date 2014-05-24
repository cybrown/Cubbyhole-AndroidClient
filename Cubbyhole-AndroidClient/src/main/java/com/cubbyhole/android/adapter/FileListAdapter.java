package com.cubbyhole.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cubbyhole.android.R;
import com.cubbyhole.android.model.File;

import java.util.List;


public class FileListAdapter extends BaseAdapter {

    private final Context context;
    private final List<File> files;

    public FileListAdapter(Context context, List<File> files) {
        this.context = context;
        this.files = files;
    }

    @Override
    public int getCount() {
        return this.files.size();
    }

    @Override
    public Object getItem(int i) {
        return this.files.get(i);
    }

    @Override
    public long getItemId(int i) {
        return this.files.get(i).getId();
    }

    @Override
    public View getView(int i, View arg1, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.cell_file, null);
        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvTitle.setText(this.files.get(i).getName());
        TextView tvSubtitle = (TextView) view.findViewById(R.id.tvSubtitle);
        tvSubtitle.setText(String.valueOf(this.files.get(i).getId()));
        return view;
    }
}
