package com.cubbyhole.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cubbyhole.android.CubbyholeAndroidClientApp;
import com.cubbyhole.android.R;
import com.cubbyhole.client.http.ConnectionInfo;
import com.cubbyhole.client.http.FileRestWebService;
import com.cubbyhole.client.model.File;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

public class LoginActivity extends Activity {

    @Inject ConnectionInfo connectionInfo;
    @Inject FileRestWebService fileService;
    @InjectView(R.id.txtUsername) EditText txtUsername;
    @InjectView(R.id.txtPassword) EditText txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        ((CubbyholeAndroidClientApp) getApplication()).getObjectGraph().inject(this);
    }

    @OnClick(R.id.btnLogin)
    protected void onClickBtnLogin(View view) {
        connectionInfo.setUsername(txtUsername.getText().toString());
        connectionInfo.setPassword(txtPassword.getText().toString());

        fileService.listRoot()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<File>>() {
                    @Override
                    public void onCompleted() {
                        resetForm();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Toast.makeText(LoginActivity.this, "Wrong username / password", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(List<File> files) {

                    }
                });
    }

    private void resetForm() {
        txtUsername.setText("");
        txtPassword.setText("");
        txtUsername.requestFocus();
    }

    @OnClick(R.id.btnRegister)
    protected void onClickBtnRegister(View view) {
        Toast.makeText(this, "Not yet implemented, use commercial site", Toast.LENGTH_LONG).show();
    }
}
