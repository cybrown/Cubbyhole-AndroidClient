package com.cubbyhole.android.util;

public class CellWrapper<T> {

    private T object;

    public CellWrapper(T object) {
        this.object = object;
    }

    public T get() {
        return object;
    }
}
