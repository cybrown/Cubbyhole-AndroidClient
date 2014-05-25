package com.cubbyhole.android.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.cubbyhole.android.CubbyholeAndroidClientApp;
import com.cubbyhole.android.R;
import com.cubbyhole.android.activity.MainActivity;
import com.cubbyhole.android.adapter.FileListAdapter;
import com.cubbyhole.android.cell.FileCell;
import com.cubbyhole.android.parcelable.ParcelableFile;
import com.cubbyhole.client.http.FileRestWebService;
import com.cubbyhole.client.model.File;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

public class FileListFragment extends Fragment {

    @Inject FileRestWebService fileService;
    private List<FileCell> fileCells = new LinkedList<FileCell>();
    private File currentFile;
    private FileListFragmentListener listener;
    private File fileForMenu;

    public FileListFragment(FileListFragmentListener listener) {
        this.listener = listener;
    }

    private void refreshFileList() {
        (currentFile == null ? fileService.listRoot() : fileService.list(this.currentFile.getId()))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<List<File>>() {
                @Override public void onCompleted() { }
                @Override public void onError(Throwable throwable) { }
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
        Bundle bundle = getArguments();
        if (bundle != null) {
            currentFile = (File)bundle.getParcelable("file");
        }
        this.refreshFileList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_file_list, null);
        ListView lstFiles = (ListView) view.findViewById(R.id.lstFiles);
        lstFiles.setAdapter(new FileListAdapter(getActivity(), fileCells));
        lstFiles.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        lstFiles.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                fileForMenu = fileCells.get(i).getFile();
                return false;
            }
        });
        this.registerForContextMenu(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.file_list_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ContextMenu.ContextMenuInfo menuInfo = item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteFile(fileForMenu);
                break;
            case R.id.action_browse:
                openFile(fileForMenu);
                break;
        }
        return super.onContextItemSelected(item);
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
                    file.setParent(currentFile != null ? currentFile.getId() : 0);
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
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void openFile(File file) {
        this.listener.onOpen(new ParcelableFile(file));
    }

    private void createFile(File file) {
        fileService.create(file).subscribe(new Observer<Void>() {
            @Override public void onError(Throwable throwable) { }
            @Override public void onNext(Void aVoid) { }
            @Override
            public void onCompleted() {
                FileListFragment.this.refreshFileList();
            }
        });
    }

    private void deleteFile(File file) {
        this.fileService.delete(file.getId())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<Void>() {
                @Override public void onError(Throwable throwable) { }
                @Override public void onNext(Void aVoid) { }
                @Override
                public void onCompleted() {
                    FileListFragment.this.refreshFileList();
                }
            });
    }
}
