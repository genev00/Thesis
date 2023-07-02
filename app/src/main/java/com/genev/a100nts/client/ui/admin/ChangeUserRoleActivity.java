package com.genev.a100nts.client.ui.admin;

import static com.genev.a100nts.client.utils.ActivityHolder.setActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.genev.a100nts.client.databinding.ActivityChangeUserRoleBinding;
import com.genev.a100nts.client.entities.User;
import com.genev.a100nts.client.ui.adapters.EditUserAdapter;
import com.genev.a100nts.client.utils.rest.RestService;

import java.util.Arrays;

public class ChangeUserRoleActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.genev.a100nts.client.databinding.ActivityChangeUserRoleBinding binding = ActivityChangeUserRoleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setActivity(this);

        User[] users = RestService.getUsersAsAdmin();
        if (users == null) {
            finish();
            System.exit(1);
        }
        binding.userList.setAdapter(
                new EditUserAdapter(this, Arrays.asList(users))
        );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
