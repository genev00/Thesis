package com.genev.a100nts.client.ui.login;

import lombok.Getter;

@Getter
class LoginFormState {

    private final Integer usernameError;
    private final Integer passwordError;
    private final boolean isDataValid;

    LoginFormState(Integer usernameError, Integer passwordError) {
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.isDataValid = false;
    }

    LoginFormState(boolean isDataValid) {
        this.usernameError = null;
        this.passwordError = null;
        this.isDataValid = isDataValid;
    }

}