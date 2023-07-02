package com.genev.a100nts.client.ui.user;

import static com.genev.a100nts.client.utils.ActivityHolder.setActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.genev.a100nts.client.R;
import com.genev.a100nts.client.common.Constants;
import com.genev.a100nts.client.data.login.LoginRepository;
import com.genev.a100nts.client.databinding.ActivityConfirmCodeBinding;
import com.genev.a100nts.client.entities.User;
import com.genev.a100nts.client.utils.rest.RestService;

public class ConfirmCodeActivity extends AppCompatActivity {

    private ActivityConfirmCodeBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityConfirmCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setActivity(this);

        setUpButtons();
    }

    private void setUpButtons() {
        binding.securityCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                final String codeValue = binding.securityCode.getText().toString().trim();
                final boolean isValid = Constants.SECURITY_CODE_PATTERN.matcher(codeValue).matches();
                if (!isValid) {
                    binding.securityCode.setError(getString(R.string.invalid_value));
                }
                binding.confirmButton.setEnabled(isValid);
            }
        });

        binding.confirmButton.setEnabled(false);
        binding.confirmButton.setOnClickListener(l -> {
            final String codeValue = binding.securityCode.getText().toString().trim();
            final String password = LoginRepository.getInstance().getLoggedUser().getPassword();
            Object response = RestService.confirmUserCode(codeValue);
            if (response == null) {
                finish();
                System.exit(1);
            }
            if (response instanceof User) {
                User user = (User) response;
                user.setPassword(password);
                LoginRepository.getInstance().setLoggedUser(user);
                Intent loggedIntent = new Intent(this, LoginRepository.getInstance().calculateUserActivityClass());
                startActivity(loggedIntent);
                super.finish();
            } else {
                Toast.makeText(this, ((String) response), Toast.LENGTH_SHORT).show();
                binding.securityCode.setText("");
                binding.confirmButton.setEnabled(false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void finish() {
        LoginRepository.getInstance().logout();
        super.finish();
    }

}
