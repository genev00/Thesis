package com.genev.a100nts.client.ui.admin;

import static com.genev.a100nts.client.utils.ActivityHolder.setActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.genev.a100nts.client.common.Constants;
import com.genev.a100nts.client.data.login.LoginRepository;
import com.genev.a100nts.client.databinding.ActivityAdminBinding;
import com.genev.a100nts.client.ui.login.LoginActivity;
import com.genev.a100nts.client.ui.sites.SitesActivity;
import com.genev.a100nts.client.ui.user.UserEditActivity;

public class AdminActivity extends AppCompatActivity {

    private ActivityAdminBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setActivity(this);

        setUpButtons();
    }

    private void setUpButtons() {
        binding.buttonEditProfile.setText(LoginRepository.getInstance().getLoggedUser().getFirstName());

        binding.buttonEditProfile.setOnClickListener(l -> {
            Intent editUser = new Intent(this, UserEditActivity.class);
            startActivity(editUser);
            super.finish();
        });

        binding.buttonExit.setOnClickListener(l -> {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        });

        binding.buttonAddSite.setOnClickListener(l -> {
            Intent addEditSite = new Intent(this, AddEditSiteActivity.class);
            addEditSite.putExtra(Constants.ARGUMENT_IS_EDIT_SITE_VIEW, false);
            startActivity(addEditSite);
        });

        binding.buttonEditSite.setOnClickListener(l -> {
            Intent viewAllSites = new Intent(this, SitesActivity.class);
            viewAllSites.putExtra(Constants.ARGUMENT_IS_RANKING_VIEW, false);
            viewAllSites.putExtra(Constants.ARGUMENT_IS_USER_SITES_VIEW, false);
            viewAllSites.putExtra(Constants.ARGUMENT_IS_USER_FAVOURITE_SITES_VIEW, false);
            viewAllSites.putExtra(Constants.ARGUMENT_IS_NEAR_TO_USER_SITES_VIEW, false);
            viewAllSites.putExtra(Constants.ARGUMENT_IS_EDIT_SITE_VIEW, true);
            startActivity(viewAllSites);
        });

        binding.buttonChangeUserRole.setOnClickListener(l -> {
            Intent changeRole = new Intent(this, ChangeUserRoleActivity.class);
            startActivity(changeRole);
        });
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

    @Override
    public void finish() {
        LoginRepository.getInstance().logout();
        super.finish();
    }

}
