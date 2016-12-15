package com.margin.mgms.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.margin.barcode.listeners.OnBarcodeClearListener;
import com.margin.barcode.listeners.OnBarcodeReaderError;
import com.margin.barcode.listeners.OnBarcodeReceivedListener;
import com.margin.barcode.views.BarcodeEditText;
import com.margin.components.utils.ImeUtils;
import com.margin.mgms.R;
import com.margin.mgms.mvp.login.LoginContract;
import com.margin.mgms.mvp.login.LoginPresenter;

import butterknife.Bind;
import butterknife.BindInt;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;

/**
 * Created on May 06, 2016.
 *
 * @author Marta.Ginosyan
 */
public class LoginActivity extends AppCompatActivity implements LoginContract.View,
        OnBarcodeReceivedListener, OnBarcodeReaderError, OnBarcodeClearListener {

    private static final int DURATION_SHOW_PROGRESS = 300;
    @Bind(R.id.login_root)
    ViewGroup mRoot;
    @Bind(R.id.login_login_button)
    Button mLoginButton;
    @Bind(R.id.barcode_edit_text_til)
    TextInputLayout mBarcodeTextInputLayout;
    @Bind(R.id.barcode_edit_text)
    BarcodeEditText mBarcodeEditText;
    @Bind(R.id.login_progress)
    ProgressBar mProgressBar;
    @Bind(R.id.login_button_layout)
    ViewGroup mLoginButtonLayout;
    @BindInt(android.R.integer.config_longAnimTime)
    int mAnimTime;
    private LoginContract.Presenter mPresenter;
    /**
     * Consumes first emission of {@link #onError(Exception)}, which is
     * automatically triggered on view inflation.
     */
    private boolean mSkipFirst = false;

    public static void launch(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mPresenter = new LoginPresenter(this);
        mPresenter.create();
        mBarcodeEditText.setOnBarcodeClearListener(this);
        mBarcodeEditText.setOnBarcodeNumberListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    @Override
    public void setLoginButtonEnabled(boolean enable) {
        if (mLoginButton.isEnabled() != enable) {
            if (enable) {
                mLoginButton.setAlpha(0.3f);
                mLoginButton.setEnabled(true);
                mLoginButton.setAlpha(0.1f);
                mLoginButton.animate()
                        .alpha(1.0f)
                        .setDuration(mAnimTime)
                        .withEndAction(() -> mPresenter.setCanLogin(true));
            } else {
                mLoginButton.setAlpha(1f);
                mLoginButton.animate()
                        .alpha(0f)
                        .setDuration(mAnimTime)
                        .withStartAction(() -> mPresenter.setCanLogin(false))
                        .withEndAction(() -> {
                                    mLoginButton.setEnabled(false);
                                    mLoginButton.setAlpha(1.0f);
                                }
                        );
            }
        }
    }

    @Override
    public void showProgress(boolean show) {
        if (!show) {
            TransitionManager.beginDelayedTransition(mLoginButtonLayout);
            mProgressBar.setVisibility(View.GONE);
            mLoginButton.setText(getString(R.string.title_login));
        } else {
            new Handler().postDelayed(() -> {
                // if response hasn't been received - show progress
                if (!mPresenter.canLogin()) {
                    TransitionManager.beginDelayedTransition(mLoginButtonLayout);
                    mLoginButton.setText("");
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }, DURATION_SHOW_PROGRESS);
        }
    }

    @OnClick({R.id.login_login_button})
    public void onLoginButtonClicked(Button button) {
        ImeUtils.hideIme(button);
        mPresenter.onLoginClicked(mBarcodeEditText.getText().toString());
    }

    @Override
    public void addBarcodeTextChangeListener() {
        Observable<CharSequence> barcodeChangedObservable = RxTextView.textChanges(mBarcodeEditText);
        mPresenter.setBarcodeTextChangedObservable(barcodeChangedObservable);
    }

    @Override
    public void setErrorMessage(String errorMsg) {
        mBarcodeTextInputLayout.setError(errorMsg);
    }

    @Override
    public void onBarcodeReceived(String barcode) {
        mPresenter.setCanLogin(true);
        mPresenter.onLoginClicked(barcode);
    }

    @Override
    public void onError(Exception e) {
        if (!mSkipFirst) {
            mSkipFirst = true;
            return;
        }
        showToast(getString(R.string.error_scanning_barcode));
    }

    @Override
    public void showToast(String... strings) {
        StringBuilder builder = new StringBuilder();

        for (String s : strings) builder.append(s).append(" ");
        Toast.makeText(LoginActivity.this, builder.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBarcodeCleared(String erasedText) {
        mPresenter.cancelCalls();
        showProgress(false);
    }

    @Override
    public void launchTaskManager() {
        TaskManagerActivity.launch(this, true);
    }
}
