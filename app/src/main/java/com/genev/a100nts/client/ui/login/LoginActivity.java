package com.genev.a100nts.client.ui.login;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.genev.a100nts.client.utils.ActivityHolder.setActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.genev.a100nts.client.common.Constants;
import com.genev.a100nts.client.databinding.ActivityLoginBinding;
import com.genev.a100nts.client.ui.sites.SitesActivity;
import com.genev.a100nts.client.ui.user.ResetPasswordActivity;
import com.genev.a100nts.client.utils.Language;

public class LoginActivity extends AppCompatActivity {

    private final static int LOCATION_REQUEST_CODE;
    private final static int FAIL_ATTEMPTS_BEFORE_RESET;

    static {
        LOCATION_REQUEST_CODE = 1000;
        FAIL_ATTEMPTS_BEFORE_RESET = 3;
    }

    private LoginViewModel loginViewModel;
    private String failedLoginEmail;
    private int failedLoginAttempts;
    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.genev.a100nts.client.databinding.ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setActivity(this);

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(LoginViewModel.class);

        emailEditText = binding.email;
        passwordEditText = binding.password;
        final TextView languageText = binding.languageText;
        final Button loginButton = binding.login;
        final Button viewAllButton = binding.viewAllSitesCut;

        loginViewModel.getLoginFormState().observe(this, getLoginFormStateObserver(loginButton));
        loginViewModel.getLoginResult().observe(this, getLoginErrorObserver());

        setUpButtons(languageText, loginButton, viewAllButton);
        requestLocationAccess();
    }

    private void setUpButtons(TextView languageText, Button loginButton, Button viewAllButton) {
        languageText.setText(Language.getCurrent().toUpperCase());
        languageText.setOnClickListener(v -> Language.changeLanguage());

        TextWatcher loginTextChangedListener = getLoginTextChangedListener();
        emailEditText.addTextChangedListener(loginTextChangedListener);
        passwordEditText.addTextChangedListener(loginTextChangedListener);
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE && loginButton.isEnabled()) {
                login();
            }
            return false;
        });

        loginButton.setOnClickListener(v -> login());

        viewAllButton.setOnClickListener(v -> {
            Intent viewAllSites = new Intent(this, SitesActivity.class);
            viewAllSites.putExtra(Constants.ARGUMENT_IS_RANKING_VIEW, false);
            viewAllSites.putExtra(Constants.ARGUMENT_IS_USER_SITES_VIEW, false);
            viewAllSites.putExtra(Constants.ARGUMENT_IS_USER_FAVOURITE_SITES_VIEW, false);
            viewAllSites.putExtra(Constants.ARGUMENT_IS_NEAR_TO_USER_SITES_VIEW, false);
            viewAllSites.putExtra(Constants.ARGUMENT_IS_EDIT_SITE_VIEW, false);
            startActivity(viewAllSites);
            clearTextFields();
        });
    }

    private void login() {
        loginViewModel.login(emailEditText.getText().toString().trim(), passwordEditText.getText().toString().trim());
        clearTextFields();
    }

    private void clearTextFields() {
        emailEditText.setText("");
        passwordEditText.setText("");
    }

    @NonNull
    private Observer<LoginError> getLoginErrorObserver() {
        return loginResult -> {
            if (loginResult == null) {
                return;
            }
            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
                return;
            }
            setResult(Activity.RESULT_OK);
        };
    }

    @NonNull
    private Observer<LoginFormState> getLoginFormStateObserver(Button loginButton) {
        return loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                emailEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        };
    }

    private TextWatcher getLoginTextChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(emailEditText.getText().toString().trim(), passwordEditText.getText().toString().trim());
            }
        };
    }

    private void showLoginFailed(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    public void loginFailed(String email) {
        if (!email.equals(failedLoginEmail)) {
            failedLoginAttempts = 0;
            failedLoginEmail = email;
        }
        if (++failedLoginAttempts >= FAIL_ATTEMPTS_BEFORE_RESET) {
            failedLoginAttempts = 0;
            clearTextFields();

            Intent resetPassword = new Intent(this, ResetPasswordActivity.class);
            resetPassword.putExtra(Constants.ARGUMENT_EMAIL, email);
            startActivity(resetPassword);
        }
    }

    private void requestLocationAccess() {
        ActivityCompat.requestPermissions(
                LoginActivity.this,
                new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION},
                LOCATION_REQUEST_CODE
        );
    }

    @Override
    protected void onResume() {
        setActivity(this);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}