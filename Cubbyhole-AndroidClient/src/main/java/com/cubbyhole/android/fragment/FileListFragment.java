package com.cubbyhole.android.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.cubbyhole.android.CubbyholeAndroidClientApp;
import com.cubbyhole.android.R;
import com.cubbyhole.android.adapter.FileListAdapter;
import com.cubbyhole.android.cell.FileCell;
import com.cubbyhole.client.http.FileRestWebService;
import com.cubbyhole.client.model.File;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

public class FileListFragment extends Fragment {

    @Inject
    FileRestWebService fileService;

    private List<FileCell> fileCells = new LinkedList<FileCell>();

    private void refreshFileList() {
        fileService.findRoot()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<List<File>>() {

                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onNext(final List<File> files) {
                    final ListView lstFiles = (ListView) FileListFragment.this.getView().findViewById(R.id.lstFiles);
                    FileListFragment.this.fileCells.clear();
                    for (File file: files) {
                        FileListFragment.this.fileCells.add(new FileCell(file));
                    }
                    ((FileListAdapter) lstFiles.getAdapter()).notifyDataSetChanged();
                }
            });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((CubbyholeAndroidClientApp)getActivity().getApplication()).getObjectGraph().inject(this);
        this.refreshFileList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_file_list, null);
        ((ListView) view.findViewById(R.id.lstFiles))
            .setAdapter(new FileListAdapter(this.getActivity(), this.fileCells));
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_mkdir:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Create Folder");
                final EditText txtName = new EditText(getActivity());
                builder.setView(txtName);
                builder.setPositiveButton("Create", new Dialog.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    File file = new File();
                    file.setName(txtName.getText().toString());
                    file.setParent(0);
                    file.setFolder(true);
                    createFile(file);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.action_delete:
                new AlertDialog.Builder(getActivity())
                    .setTitle("Delete selection ?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            List<File> filesToDelete = getSelectedFiles();
                            deleteFiles(filesToDelete);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .create()
                    .show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void createFile(File file) {
        fileService.create(file).subscribe(new Observer<Void>() {
            @Override
            public void onCompleted() {
                FileListFragment.this.refreshFileList();
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onNext(Void aVoid) {

            }
        });
    }

    private List<File> getSelectedFiles() {
        List<File> filesToDelete = new LinkedList<File>();
        for (final FileCell fileCell : this.fileCells) {
            if (fileCell.isChecked()) {
                filesToDelete.add(fileCell.getFile());
            }
        }
        return filesToDelete;
    }

    private void deleteFiles(List<File> filesToDelete) {
        List<Observable<Void>> obs = new LinkedList<Observable<Void>>();
        for (final File file: filesToDelete) {
            obs.add(this.fileService.delete(file.getId()));
        }
        Observable.merge(obs)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<Void>() {
                @Override
                public void onCompleted() {
                    FileListFragment.this.refreshFileList();
                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onNext(Void aVoid) {

                }
            });
    }
}
