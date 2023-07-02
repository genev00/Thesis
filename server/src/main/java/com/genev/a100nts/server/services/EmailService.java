package com.genev.a100nts.server.services;

public interface EmailService {

    void sendSecurityCode(String email, String name, String code);

}
