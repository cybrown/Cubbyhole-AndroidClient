package com.cubbyhole.android.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import com.cubbyhole.android.CubbyholeAndroidClientApp;
import com.cubbyhole.android.R;
import com.cubbyhole.android.adapter.ShareListAdapter;
import com.cubbyhole.android.cell.ShareCell;
import com.cubbyhole.android.util.CellWrapper;
import com.cubbyhole.client.http.AccountRestWebService;
import com.cubbyhole.client.http.FileRestWebService;
import com.cubbyhole.client.model.PartialAccount;
import com.cubbyhole.client.model.Share;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;

public class PermissionActivity extends Activity implements AdapterView.OnItemLongClickListener {

    @Inject FileRestWebService fileService;
    @Inject AccountRestWebService accountService;
    @InjectView(R.id.lstShares) ListView lstShares;
    private long fileId;
    private List<CellWrapper<Share>> shareCells = new ArrayList<CellWrapper<Share>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        ((CubbyholeAndroidClientApp)getApplication()).getObjectGraph().inject(this);
        ButterKnife.inject(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            fileId = bundle.getLong("fileId");
        }
        lstShares.setAdapter(new ShareListAdapter(this, shareCells));
        lstShares.setOnItemLongClickListener(this);
        refreshList();
    }

    private void refreshList() {
        shareCells.clear();
        fileService.getPermissions(fileId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Share>>() {
                    @Override public void onCompleted() { }

                    @Override public void onError(Throwable throwable) {
                        Log.e("CUBBYHOLE", "PermissionActivity.onClickBtnAddPerm", throwable);
                    }

                    @Override
                    public void onNext(List<Share> shares) {
                        final List<Observable<PartialAccount>> listPartialAccounts = new ArrayList<Observable<PartialAccount>>();
                        for (final Share share : shares) {
                            listPartialAccounts.add(accountService.findPartialById(share.getAccount()));
                        }
                        Observable.from(shares).zip(Observable.merge(listPartialAccounts), new Func2<Share, PartialAccount, Map.Entry<PartialAccount, Share>>() {
                            @Override
                            public Map.Entry<PartialAccount, Share> call(Share share, PartialAccount partialAccount) {
                                return new AbstractMap.SimpleImmutableEntry<PartialAccount, Share>(partialAccount, share);
                            }
                        })
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<Map.Entry<PartialAccount, Share>>() {
                                    @Override
                                    public void onCompleted() {
                                        ((ShareListAdapter) lstShares.getAdapter()).notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onError(Throwable throwable) {
                                        Log.e("CUBBYHOLE", "PermissionActivity.refreshList", throwable);
                                    }

                                    @Override
                                    public void onNext(Map.Entry<PartialAccount, Share> partialAccountShareEntry) {
                                        ShareCell cell = new ShareCell(partialAccountShareEntry.getValue());
                                        cell.setAccount(partialAccountShareEntry.getKey());
                                        shareCells.add(cell);
                                    }
                                });
                    }
                });
    }

    @OnClick(R.id.btnAddReadPerm)
    public void onClickBtnAddReadPerm(View view) {
        final String permission = "READ";
        showAddPermDialog(permission);
    }

    @OnClick(R.id.btnAddWritePerm)
    public void onClickBtnAddWritePerm(View view) {
        final String permission = "WRITE";
        showAddPermDialog(permission);
    }

    private void showAddPermDialog(final String permission) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_perm, null);
        final AutoCompleteTextView txtAccount = (AutoCompleteTextView) dialogView.findViewById(R.id.txtAccount);
        txtAccount.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                String begin = txtAccount.getText().toString();
                if (begin.length() != 0) {
                    accountService.findStartsWith(begin)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<List<PartialAccount>>() {
                                @Override
                                public void onCompleted() {
                                }

                                @Override
                                public void onError(Throwable throwable) {
                                    Log.e("CUBBYHOLE", "PermissionActivity.onClickBtnAddPerm", throwable);
                                }

                                @Override
                                public void onNext(List<PartialAccount> partialAccounts) {
                                    List<String> usernames = new ArrayList<String>();
                                    for (PartialAccount account : partialAccounts) {
                                        usernames.add(account.getUsername());
                                    }
                                    txtAccount.setAdapter(new ArrayAdapter<String>(PermissionActivity.this, android.R.layout.simple_dropdown_item_1line, usernames));
                                }
                            });
                }
                return false;
            }
        });
        new AlertDialog.Builder(this)
                .setTitle("Add permission")
                .setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        accountService.findPartialByUsername(txtAccount.getText().toString())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<PartialAccount>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable throwable) {
                                        Log.e("CUBBYHOLE", "PermissionActivity.onClickBtnAddPerm", throwable);
                                    }

                                    @Override
                                    public void onNext(PartialAccount partialAccount) {
                                        fileService.addPermission(fileId, permission, partialAccount.getId())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new Observer<Void>() {
                                                    @Override
                                                    public void onCompleted() {
                                                        refreshList();
                                                    }

                                                    @Override
                                                    public void onError(Throwable throwable) {
                                                        Log.e("CUBBYHOLE", "PermissionActivity.onClickBtnAddPerm", throwable);
                                                    }

                                                    @Override
                                                    public void onNext(Void aVoid) {

                                                    }
                                                });
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        ShareCell cell = (ShareCell) adapterView.getItemAtPosition(i);
        fileService.removePermission(
                cell.get().getFile(),
                cell.get().getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {
                        refreshList();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("CUBBYHOLE", "PermissionActivity.onItemLongClick", throwable);
                    }

                    @Override
                    public void onNext(Void aVoid) {

                    }
                });
        return false;
    }
}
