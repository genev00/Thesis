package com.genev.a100nts.client.ui.user;

import static com.genev.a100nts.client.utils.ActivityHolder.setActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.genev.a100nts.client.R;
import com.genev.a100nts.client.common.Constants;
import com.genev.a100nts.client.data.login.LoginRepository;
import com.genev.a100nts.client.databinding.ActivityResetPasswordBinding;
import com.genev.a100nts.client.entities.User;
import com.genev.a100nts.client.utils.rest.RestService;

public class ResetPasswordActivity extends AppCompatActivity {

    private ActivityResetPasswordBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setActivity(this);

        Intent intent = getIntent();
        String email = (String) intent.getSerializableExtra(Constants.ARGUMENT_EMAIL);
        if (RestService.sendUserCode(email) == null) {
            finish();
            System.exit(1);
        }

        setUpButtons(email);
    }

    private void setUpButtons(String email) {
        binding.email.setText(email);

        TextWatcher editUserTextChangedListener = getResetPasswordTextChangedListener();
        binding.securityCode.addTextChangedListener(editUserTextChangedListener);
        binding.resetPassword.addTextChangedListener(editUserTextChangedListener);
        binding.resetPassword2.addTextChangedListener(editUserTextChangedListener);

        binding.resetPasswordButton.setEnabled(false);
        binding.resetPasswordButton.setOnClickListener(getResetPasswordClickListener());
    }

    private TextWatcher getResetPasswordTextChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.resetPasswordButton.setEnabled(false);

                final String codeValue = binding.securityCode.getText().toString().trim();
                final String newPassword = binding.resetPassword.getText().toString().trim();
                final String newPassword2 = binding.resetPassword2.getText().toString().trim();

                if (!Constants.SECURITY_CODE_PATTERN.matcher(codeValue).matches()) {
                    binding.securityCode.setError(getString(R.string.invalid_value));
                    return;
                }

                if (!Constants.PASSWORD_PATTERN.matcher(newPassword).matches()) {
                    binding.resetPassword.setError(getString(R.string.invalid_password));
                    return;
                }
                if (!newPassword.equals(newPassword2)) {
                    binding.resetPassword2.setError(getString(R.string.nonmatching_passwords));
                    return;
                }

                binding.resetPasswordButton.setEnabled(true);
            }
        };
    }

    private View.OnClickListener getResetPasswordClickListener() {
        return l -> {
            final String password = binding.resetPassword.getText().toString().trim();
            User userRequest = new User(
                    binding.email.getText().toString().trim(),
                    password
            );
            Object response = RestService.resetUserPass(userRequest, binding.securityCode.getText().toString().trim());
            if (response == null) {
                finish();
                System.exit(1);
            }
            if (response instanceof User) {
                User user = (User) response;
                user.setPassword(password);
                LoginRepository.getInstance().setLoggedUser(user);
                Toast.makeText(this, getString(R.string.updated_successfully), Toast.LENGTH_SHORT).show();
                Intent userIntent = new Intent(this, LoginRepository.getInstance().calculateUserActivityClass());
                startActivity(userIntent);
                finish();
            } else {
                Toast.makeText(this, ((String) response), Toast.LENGTH_SHORT).show();
                binding.securityCode.setText("");
                binding.resetPassword.setText("");
                binding.resetPassword2.setText("");
                binding.resetPasswordButton.setEnabled(false);
            }
        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
