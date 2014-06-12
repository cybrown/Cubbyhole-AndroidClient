package com.cubbyhole.android.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.cubbyhole.android.R;
import com.cubbyhole.android.util.CellWrapper;
import com.cubbyhole.android.util.GenericListAdapter;
import com.cubbyhole.client.model.File;

import java.util.List;


public class FileListAdapter extends GenericListAdapter<File> {

    public FileListAdapter(Context context, List<CellWrapper<File>> objects) {
        super(context, objects, R.layout.cell_file);
    }

    @Override
    public long getId(File object) {
        return object.getId();
    }

    @Override
    protected void getView(CellWrapper<File> object, View view) {
        ((TextView) view.findViewById(R.id.tvTitle)).setText(object.get().getName());
        ((TextView) view.findViewById(R.id.tvSubtitle)).setText(String.valueOf(object.get().getMdate()));
    }
}
