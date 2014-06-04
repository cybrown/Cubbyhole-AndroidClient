package com.cubbyhole.android.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.cubbyhole.android.CubbyholeAndroidClientApp;
import com.cubbyhole.android.R;
import com.cubbyhole.android.adapter.ShareListAdapter;
import com.cubbyhole.android.util.CellWrapper;
import com.cubbyhole.client.http.FileRestWebService;
import com.cubbyhole.client.model.Share;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

public class PermissionActivity extends Activity {

    @Inject FileRestWebService fileService;
    @InjectView(R.id.lstShares) ListView lstShares;
    private long fileId;
    private List<CellWrapper<Share>> shareCells = new ArrayList<CellWrapper<Share>>();

    private void refreshList() {
        ((ShareListAdapter) lstShares.getAdapter()).notifyDataSetChanged();
    }

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

        fileService.getPermissions(fileId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Share>>() {
                    @Override
                    public void onCompleted() {
                        refreshList();
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onNext(List<Share> shares) {
                        shareCells.clear();
                        for (Share share: shares) {
                            shareCells.add(new CellWrapper<Share>(share));
                        }
                        refreshList();
                    }
                });
    }

    @OnClick(R.id.btnAddPerm)
    public void onClickBtnAddPerm(View view) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_perm, null);
        final EditText txtPerm = (EditText) dialogView.findViewById(R.id.txtPerm);
        final EditText txtAccount = (EditText) dialogView.findViewById(R.id.txtAccount);
        new AlertDialog.Builder(this)
                .setTitle("Add permission")
                .setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        fileService.addPermission(fileId, txtPerm.getText().toString(), Long.parseLong(txtAccount.getText().toString()))
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<Void>() {
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
}
