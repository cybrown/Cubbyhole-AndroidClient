package com.cubbyhole.android.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;


public abstract class GenericListAdapter<T> extends BaseAdapter {

    protected List<CellWrapper<T>> objects = null;
    protected LayoutInflater inflater;
    protected int viewId;

    abstract protected long getId(T object);
    abstract protected void getView(T object, View view);

    public GenericListAdapter(Context context, List<CellWrapper<T>> objects, int viewId) {
        this.objects = objects;
        this.viewId = viewId;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.objects.size();
    }

    @Override
    public Object getItem(int i) {
        return this.objects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return getId(this.objects.get(i).get());
    }

    @Override
    public View getView(final int i, View arg1, ViewGroup viewGroup) {
        View view = inflater.inflate(viewId, null);
        getView(this.objects.get(i).get(), view);
        return view;
    }
}
