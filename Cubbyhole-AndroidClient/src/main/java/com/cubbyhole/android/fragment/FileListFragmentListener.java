package com.cubbyhole.android.fragment;

import com.cubbyhole.android.parcelable.ParcelableFile;
import com.cubbyhole.client.model.File;

public interface FileListFragmentListener {

    boolean onOpenFolder(ParcelableFile file);
    boolean onOpenFile(ParcelableFile file);
    void onSelect(FileListFragment fileListFragment, File currentFile);
}
