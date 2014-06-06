package com.cubbyhole.android.fragment;

import com.cubbyhole.client.model.File;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

public class SharedFileListFragment extends FileListFragment {
    public SharedFileListFragment(FileListFragmentListener listener) {
        super(listener);
    }

    protected void refreshFileList() {
        final Observer<List<File>> listCurrentFileObserver = new Observer<List<File>>() {
            @Override public void onCompleted() { }
            @Override public void onError(Throwable throwable) { }
            @Override
            public void onNext(final List<File> files) {
                setFilesToShow(files);
            }
        };
        fileService.listShared()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listCurrentFileObserver);
    }
}
