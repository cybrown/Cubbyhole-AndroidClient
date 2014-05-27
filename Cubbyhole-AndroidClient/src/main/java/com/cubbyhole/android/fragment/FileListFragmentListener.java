package com.cubbyhole.android.fragment;

import com.cubbyhole.android.parcelable.ParcelableFile;
import com.cubbyhole.client.model.File;

public interface FileListFragmentListener {

    boolean onOpen(ParcelableFile file);
    void onSelect(FileListFragment fileListFragment, File currentFile);
}
