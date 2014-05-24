package com.cubbyhole.android.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.cubbyhole.android.CubbyholeAndroidClientApp;
import com.cubbyhole.android.R;
import com.cubbyhole.android.adapter.FileListAdapter;
import com.cubbyhole.client.model.File;
import com.cubbyhole.android.service.FileService;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

public class FileListFragment extends Fragment {

    @Inject FileService fileService;

    private List<File> files = new LinkedList<File>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((CubbyholeAndroidClientApp)getActivity().getApplication()).getObjectGraph().inject(this);
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
                    FileListFragment.this.files.clear();
                    FileListFragment.this.files.addAll(files);
                    ((FileListAdapter) lstFiles.getAdapter()).notifyDataSetChanged();
                }
            });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_file_list, null);
        ((ListView) view.findViewById(R.id.lstFiles))
            .setAdapter(new FileListAdapter(this.getActivity(), this.files));
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
                        fileService.create(file).subscribe(new Observer<Void>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable throwable) {

                            }

                            @Override
                            public void onNext(Void aVoid) {

                            }
                        });
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
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
