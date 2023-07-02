package com.genev.a100nts.client.ui.user;

import static com.genev.a100nts.client.utils.ActivityHolder.setActivity;
import static com.genev.a100nts.client.utils.rest.RestService.checkIfEmailExists;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.genev.a100nts.client.R;
import com.genev.a100nts.client.common.Constants;
import com.genev.a100nts.client.data.login.LoginRepository;
import com.genev.a100nts.client.databinding.ActivityUserEditBinding;
import com.genev.a100nts.client.entities.User;
import com.genev.a100nts.client.utils.rest.RestService;

public class UserEditActivity extends AppCompatActivity {

    private ActivityUserEditBinding binding;

    private EditText editName;
    private EditText editSurname;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editPassword2;
    private Switch editRankSwitch;

    private String currentName;
    private String currentSurname;
    private String currentEmail;
    private boolean currentRanking;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setActivity(this);

        setUpFields();
        setUpButtons();
    }

    private void setUpFields() {
        editName = binding.editName;
        editSurname = binding.editSurname;
        editEmail = binding.editEmail;
        editPassword = binding.editPassword;
        editPassword2 = binding.editPassword2;
        editRankSwitch = binding.editRankSwitch;
        binding.editUserButton.setEnabled(false);
    }

    private void setUpButtons() {
        loadCurrentInformation();

        TextWatcher editUserTextChangedListener = getEditUserTextChangedListener();
        editName.addTextChangedListener(editUserTextChangedListener);
        editSurname.addTextChangedListener(editUserTextChangedListener);
        editEmail.addTextChangedListener(editUserTextChangedListener);
        editPassword.addTextChangedListener(editUserTextChangedListener);
        editPassword2.addTextChangedListener(editUserTextChangedListener);
        editRankSwitch.setOnClickListener(l -> editUserDataChanged());

        binding.editUserButton.setOnClickListener(getEditUserClickListener());
    }

    private void loadCurrentInformation() {
        final User user = LoginRepository.getInstance().getLoggedUser();
        currentName = user.getFirstName();
        currentSurname = user.getLastName();
        currentEmail = user.getEmail();
        currentRanking = user.getRanking();

        editName.setText(currentName);
        editSurname.setText(currentSurname);
        editEmail.setText(currentEmail);
        editRankSwitch.setChecked(currentRanking);
    }

    private TextWatcher getEditUserTextChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                editUserDataChanged();
            }
        };
    }

    private View.OnClickListener getEditUserClickListener() {
        return l -> {
            String password = LoginRepository.getInstance().getLoggedUser().getPassword();
            final String newPassword = editPassword.getText().toString().trim();
            User user = RestService.updateUser(new User(
                    editName.getText().toString().trim(),
                    editSurname.getText().toString().trim(),
                    editEmail.getText().toString().trim(),
                    newPassword.isEmpty() ? null : newPassword,
                    editRankSwitch.isChecked()
            ));
            if (user == null) {
                finish();
                System.exit(1);
            }
            if (!newPassword.isEmpty()) {
                password = newPassword;
            }
            user.setPassword(password);
            LoginRepository.getInstance().setLoggedUser(user);
            Toast.makeText(this, getString(R.string.updated_successfully), Toast.LENGTH_SHORT).show();
            Intent userIntent = new Intent(this, LoginRepository.getInstance().calculateUserActivityClass());
            startActivity(userIntent);
            finish();
        };
    }

    private void editUserDataChanged() {
        binding.editUserButton.setEnabled(false);

        final String newName = editName.getText().toString().trim();
        final String newSurname = editSurname.getText().toString().trim();
        final String newEmail = editEmail.getText().toString().trim();
        final String newPassword = editPassword.getText().toString().trim();
        final String newPassword2 = editPassword2.getText().toString().trim();
        final boolean newRanking = editRankSwitch.isChecked();

        if (newName.length() < 3 || Constants.NON_LETTER_SYMBOLS_PATTERN.matcher(newName).find()) {
            editName.setError(getString(R.string.invalid_name));
            return;
        }

        if (newSurname.length() < 3 || Constants.NON_LETTER_SYMBOLS_PATTERN.matcher(newSurname).find()) {
            editSurname.setError(getString(R.string.invalid_surname));
            return;
        }

        if (!newEmail.equals(currentEmail)) {
            if (newEmail.contains("abv.bg") || newEmail.contains("mail.bg")
                    || !Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                editEmail.setError(getString(R.string.invalid_email));
                return;
            }

            Boolean emailExists = checkIfEmailExists(newEmail);
            if (emailExists == null) {
                finish();
                System.exit(1);
            }
            if (emailExists) {
                editEmail.setError(getString(R.string.existing_email));
                return;
            }
        }

        if (!newPassword.isEmpty() || !newPassword2.isEmpty()) {
            if (!Constants.PASSWORD_PATTERN.matcher(newPassword).matches()) {
                editPassword.setError(getString(R.string.invalid_password));
                return;
            }
            if (!newPassword.equals(newPassword2)) {
                editPassword2.setError(getString(R.string.nonmatching_passwords));
                return;
            }
        }

        if (!newName.equals(currentName) || !newSurname.equals(currentSurname)
                || !newEmail.equals(currentEmail) || !newPassword.isEmpty()
                || newRanking != currentRanking) {
            binding.editUserButton.setEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent userIntent = new Intent(this, LoginRepository.getInstance().calculateUserActivityClass());
        startActivity(userIntent);
        finish();
    }

}
