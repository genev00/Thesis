package com.genev.a100nts.client.ui.login;

import static android.util.Patterns.EMAIL_ADDRESS;
import static com.genev.a100nts.client.utils.ActivityHolder.getActivity;

import android.content.Intent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.genev.a100nts.client.R;
import com.genev.a100nts.client.common.Constants;
import com.genev.a100nts.client.data.login.LoginRepository;
import com.genev.a100nts.client.data.login.Result;
import com.genev.a100nts.client.utils.ActivityHolder;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<LoginFormState> loginFormState;
    private final MutableLiveData<LoginError> loginResult;
    private final LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
        this.loginFormState = new MutableLiveData<>();
        this.loginResult = new MutableLiveData<>();
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginError> getLoginResult() {
        return loginResult;
    }

    public void login(String email, String password) {
        Result result = loginRepository.login(email, password);
        if (result instanceof Result.Error) {
            ActivityHolder.notifyLoginFailed(email);
            loginResult.setValue(new LoginError(((Result.Error) result).getError().getMessage()));
        } else if (LoginRepository.getInstance().isUserLogged()) {
            Intent loggedIntent = new Intent(getActivity(), LoginRepository.getInstance().calculateUserActivityClass());
            getActivity().startActivity(loggedIntent);
            getActivity().finish();
        }
    }

    public void loginDataChanged(String email, String password) {
        if (!isEmailValid(email)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_email, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    private boolean isEmailValid(String email) {
        return email != null && !email.contains("abv.bg") && !email.contains("mail.bg")
                && EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password != null && Constants.PASSWORD_PATTERN.matcher(password).matches();
    }

}