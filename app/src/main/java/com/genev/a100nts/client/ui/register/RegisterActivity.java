package com.genev.a100nts.client.ui.register;

import static com.genev.a100nts.client.utils.ActivityHolder.setActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.genev.a100nts.client.R;
import com.genev.a100nts.client.common.Constants;
import com.genev.a100nts.client.data.login.LoginRepository;
import com.genev.a100nts.client.databinding.ActivityRegisterBinding;
import com.genev.a100nts.client.entities.User;
import com.genev.a100nts.client.utils.rest.RestService;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setActivity(this);

        Intent intent = getIntent();
        String email = (String) intent.getSerializableExtra(Constants.ARGUMENT_EMAIL);
        String password = (String) intent.getSerializableExtra(Constants.ARGUMENT_PASSWORD);

        setUpButtons(email, password);
    }

    private void setUpButtons(String email, String password) {
        binding.regEmail.setText(email);
        binding.regEmail.setEnabled(false);
        binding.regPassword.setText(password);
        binding.regButton.setEnabled(false);

        TextWatcher registerTextChangedListener = getRegisterTextChangedListener();
        binding.regName.addTextChangedListener(registerTextChangedListener);
        binding.regSurname.addTextChangedListener(registerTextChangedListener);
        binding.regPassword.addTextChangedListener(registerTextChangedListener);
        binding.regPassword2.addTextChangedListener(registerTextChangedListener);
        binding.regPassword2.setOnEditorActionListener(getOnEditPasswordActionListener());

        binding.regButton.setOnClickListener(v -> registerUser());
    }

    @NonNull
    private TextWatcher getRegisterTextChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                registerDataChanged();
            }
        };
    }

    private TextView.OnEditorActionListener getOnEditPasswordActionListener() {
        return (v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE && binding.regButton.isEnabled()) {
                registerUser();
            }
            return false;
        };
    }

    private void registerDataChanged() {
        binding.regButton.setEnabled(false);

        String nameS = binding.regName.getText().toString().trim();
        String surnameS = binding.regSurname.getText().toString().trim();
        String passwordS = binding.regPassword.getText().toString().trim();
        String password2S = binding.regPassword2.getText().toString().trim();

        if (nameS.length() < 3 || Constants.NON_LETTER_SYMBOLS_PATTERN.matcher(nameS).find()) {
            binding.regName.setError(getString(R.string.invalid_name));
            return;
        }

        if (surnameS.length() < 3 || Constants.NON_LETTER_SYMBOLS_PATTERN.matcher(surnameS).find()) {
            binding.regSurname.setError(getString(R.string.invalid_surname));
            return;
        }

        if (!Constants.PASSWORD_PATTERN.matcher(passwordS).matches()) {
            binding.regPassword.setError(getString(R.string.invalid_password));
            return;
        }

        if (!password2S.equals(passwordS)) {
            binding.regPassword2.setError(getString(R.string.nonmatching_passwords));
            return;
        }

        binding.regButton.setEnabled(true);
    }

    private void registerUser() {
        final String password = binding.regPassword.getText().toString().trim();
        final User userRequest = new User(
                binding.regName.getText().toString().trim(),
                binding.regSurname.getText().toString().trim(),
                binding.regEmail.getText().toString().trim(),
                password,
                binding.rankSwitch.isChecked()
        );
        User user = RestService.registerUser(userRequest);
        if (user == null) {
            finish();
            System.exit(1);
        }
        user.setPassword(password);
        LoginRepository.getInstance().setLoggedUser(user);
        Intent loggedIntent = new Intent(this, LoginRepository.getInstance().calculateUserActivityClass());
        startActivity(loggedIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
