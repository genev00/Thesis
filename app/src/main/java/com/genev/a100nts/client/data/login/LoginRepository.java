package com.genev.a100nts.client.data.login;

import android.content.Intent;

import com.genev.a100nts.client.common.Constants;
import com.genev.a100nts.client.entities.User;
import com.genev.a100nts.client.entities.UserRole;
import com.genev.a100nts.client.ui.admin.AdminActivity;
import com.genev.a100nts.client.ui.login.LoginActivity;
import com.genev.a100nts.client.ui.register.RegisterActivity;
import com.genev.a100nts.client.ui.user.ConfirmCodeActivity;
import com.genev.a100nts.client.ui.user.UserActivity;
import com.genev.a100nts.client.utils.ActivityHolder;
import com.genev.a100nts.client.utils.rest.RestService;

import java.io.IOException;

public class LoginRepository {

    private static volatile LoginRepository instance;
    private User loggedUser;

    public static LoginRepository getInstance() {
        if (instance == null) {
            instance = new LoginRepository();
        }
        return instance;
    }

    private LoginRepository() {
    }

    public boolean isUserLogged() {
        return loggedUser != null;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(User loggedUser) {
        if (loggedUser.getPassword() == null || loggedUser.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Cannot set user without password");
        }
        this.loggedUser = loggedUser;
    }

    @SuppressWarnings("unchecked")
    public Result login(String email, String password) {
        Result result = restLogin(email, password);
        if (result instanceof Result.Success && ((Result.Success<?>) result).getData() != null) {
            User user = ((Result.Success<User>) result).getData();
            user.setPassword(password);
            setLoggedUser(user);
        }
        return result;
    }

    public void logout() {
        loggedUser = null;
    }

    public Class<?> calculateUserActivityClass() {
        if (isUserLogged()) {
            if (loggedUser.getConfirmCode() == null || loggedUser.getConfirmCode()) {
                return ConfirmCodeActivity.class;
            }
            if (loggedUser.getRole() == UserRole.ADMIN) {
                return AdminActivity.class;
            }
            return UserActivity.class;
        }
        return LoginActivity.class;
    }

    private Result restLogin(String email, String password) {
        Boolean emailExists = RestService.checkIfEmailExists(email);
        if (emailExists == null) {
            ActivityHolder.getActivity().finish();
            System.exit(1);
        }
        if (!emailExists) {
            Intent register = new Intent(ActivityHolder.getActivity(), RegisterActivity.class);
            register.putExtra(Constants.ARGUMENT_EMAIL, email);
            register.putExtra(Constants.ARGUMENT_PASSWORD, password);
            ActivityHolder.getActivity().startActivity(register);
            return new Result.Success<>(null);
        }
        Object loginResult = RestService.loginUser(new User(email, password));
        if (loginResult == null) {
            ActivityHolder.getActivity().finish();
            System.exit(1);
        }
        if (loginResult instanceof User) {
            return new Result.Success<>((User) loginResult);
        } else {
            return new Result.Error(new IOException((String) loginResult));
        }
    }

}