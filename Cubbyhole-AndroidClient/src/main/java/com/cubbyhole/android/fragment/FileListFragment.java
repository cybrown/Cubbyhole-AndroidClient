package com.cubbyhole.android.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.cubbyhole.android.CubbyholeAndroidClientApp;
import com.cubbyhole.android.R;
import com.cubbyhole.android.adapter.FileListAdapter;
import com.cubbyhole.android.cell.FileCell;
import com.cubbyhole.android.parcelable.ParcelableFile;
import com.cubbyhole.client.CurrentAccountService;
import com.cubbyhole.client.http.FileRestWebService;
import com.cubbyhole.client.model.File;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

public class FileListFragment extends DialogFragment {

    @Inject FileRestWebService fileService;
    @Inject CurrentAccountService currentAccountService;
    @InjectView(R.id.lstFiles) ListView lstFiles;
    private List<FileCell> fileCells = new LinkedList<FileCell>();
    private File currentFile;
    private FileListFragmentListener listener;
    private File fileForMenu;

    private boolean showParentButton = true;
    private boolean dialogButtonVisible = false;

    public FileListFragment(FileListFragmentListener listener) {
        this.listener = listener;
    }

    private void refreshFileList() {
        final Observer<List<File>> listCurrentFileObserver = new Observer<List<File>>() {
            @Override public void onCompleted() { }
            @Override public void onError(Throwable throwable) { }
            @Override
            public void onNext(final List<File> files) {
                FileListFragment.this.fileCells.clear();
                for (File file: files) {
                    FileListFragment.this.fileCells.add(new FileCell(file));
                }
                ((FileListAdapter) lstFiles.getAdapter()).notifyDataSetChanged();
            }
        };
        if (currentFile == null) {
            fileService.find(currentAccountService.get().getHome())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<File>() {
                        @Override
                        public void onCompleted() {
                            fileService.list(currentFile.getId())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(listCurrentFileObserver);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            Toast.makeText(getActivity(), "Error while getting current directory", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onNext(File file) {
                            currentFile = file;
                        }
                    });
        } else {
            fileService.list(this.currentFile.getId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(listCurrentFileObserver);
        }
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

    @InjectView(R.id.lytDialogButtons) LinearLayout lytDialogButtons;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_file_list, null);
        ButterKnife.inject(this, view);
        lstFiles.setAdapter(new FileListAdapter(getActivity(), fileCells));
        this.registerForContextMenu(view);
        lytDialogButtons.setVisibility(dialogButtonVisible ? View.VISIBLE : View.INVISIBLE);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
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
            case R.id.action_rename:
                renameFile(fileForMenu);
                break;
            case R.id.action_move:
                moveFile(fileForMenu);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void moveFile(final File fileForMenu) {
        FileListFragment fragment = new FileListFragment(new FileListFragmentListener() {
            @Override
            public boolean onOpenFolder(ParcelableFile file) {
                return false;
            }

            @Override
            public boolean onOpenFile(ParcelableFile file) {
                return false;
            }

            @Override
            public void onSelect(FileListFragment fileListFragment, File currentFile) {
                fileListFragment.dismiss();
                fileForMenu.setParent(currentFile == null ? 0 : currentFile.getId());
                fileService.save(fileForMenu.getId(),fileForMenu)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Void>() {
                            @Override
                            public void onCompleted() {
                                FileListFragment.this.refreshFileList();
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                Toast.makeText(getActivity(), "Error while moving", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onNext(Void aVoid) {

                            }
                        });
            }
        });
        fragment.setDialogButtonVisible(true);
        fragment.show(getFragmentManager(), null);
    }

    @OnClick(R.id.btnParent)
    public void onClickBtnParent(View view) {
        if (currentFile != null) {
            fileService.find(currentFile.getParent())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<File>() {
                        @Override public void onCompleted() { }
                        @Override public void onError(Throwable throwable) { }
                        @Override
                        public void onNext(File file) {
                            currentFile = file;
                            refreshFileList();
                        }
                    });
        }
    }

    @OnClick(R.id.btnPositive)
    public void onClickBtnPositive(View view) {
        listener.onSelect(this, this.currentFile);
    }

    @OnClick(R.id.btnNegative)
    public void onClickBtnNegative(View view) {
        this.dismiss();
    }

    @OnItemClick(R.id.lstFiles)
    public void onItemClickLstFiles(AdapterView<?> adapterView, View view, int i, long l) {
        File file = fileCells.get(i).getFile();
        if (file.isFolder()) {
            openFolder(fileCells.get(i).getFile());
        } else {
            openFile(fileCells.get(i).getFile());
        }

    }

    @OnItemLongClick(R.id.lstFiles)
    public boolean onItemLongClickLstFiles(AdapterView<?> adapterView, View view, int i, long l) {
        fileForMenu = fileCells.get(i).getFile();
        return false;
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
                builder.setNegativeButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void openFolder(File file) {
        if (!this.listener.onOpenFolder(new ParcelableFile(file))) {
            currentFile = file;
            refreshFileList();
        }
    }

    private void openFile(File file) {
        if (!this.listener.onOpenFile(new ParcelableFile(file))) {
            Toast.makeText(getActivity(), "Can not open file", Toast.LENGTH_LONG).show();
        }
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

    private void renameFile(final File file) {
        final EditText txtName = new EditText(getActivity());
        txtName.setText(file.getName());
        new AlertDialog.Builder(getActivity())
            .setTitle("Rename")
            .setView(txtName)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    file.setName(txtName.getText().toString());
                    fileService.save(file.getId(), file)
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<Void>() {
                                @Override
                                public void onError(Throwable throwable) {
                                }

                                @Override
                                public void onNext(Void aVoid) {
                                }

                                @Override
                                public void onCompleted() {
                                    refreshFileList();
                                }
                            });
                }
            })
            .setNegativeButton("Cancel", null)
            .create()
            .show();
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

    public void setDialogButtonVisible(boolean dialogButtonVisible) {
        this.dialogButtonVisible = dialogButtonVisible;
    }

    public boolean isDialogButtonVisible() {
        return dialogButtonVisible;
    }
}
