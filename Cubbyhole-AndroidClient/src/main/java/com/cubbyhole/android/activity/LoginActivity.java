package com.cubbyhole.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cubbyhole.android.CubbyholeAndroidClientApp;
import com.cubbyhole.android.R;
import com.cubbyhole.client.CurrentAccountService;
import com.cubbyhole.client.http.AccountRestWebService;
import com.cubbyhole.client.http.ConnectionInfo;
import com.cubbyhole.client.model.Account;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

public class LoginActivity extends Activity {

    @Inject ConnectionInfo connectionInfo;
    @Inject AccountRestWebService accountService;
    @InjectView(R.id.txtUsername) EditText txtUsername;
    @InjectView(R.id.txtPassword) EditText txtPassword;
    @InjectView(R.id.btnLogin) Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        ((CubbyholeAndroidClientApp) getApplication()).getObjectGraph().inject(this);
    }

    @Inject CurrentAccountService currentAccountService;

    @OnClick(R.id.btnLogin)
    protected void onClickBtnLogin(View view) {
        connectionInfo.setUsername(txtUsername.getText().toString());
        connectionInfo.setPassword(txtPassword.getText().toString());

        accountService.whoami()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Account>() {
                    @Override
                    public void onCompleted() {
                        btnLogin.setActivated(false);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivityForResult(intent, 1);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Toast.makeText(LoginActivity.this, "Wrong username / password", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(Account account) {
                        currentAccountService.set(account);
                    }
                });
    }

    private void resetForm() {
        txtUsername.setText("");
        txtPassword.setText("");
        btnLogin.setActivated(true);
        txtUsername.requestFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                resetForm();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @OnClick(R.id.btnRegister)
    protected void onClickBtnRegister(View view) {
        Toast.makeText(this, "Not yet implemented, use commercial site", Toast.LENGTH_LONG).show();
    }
}
