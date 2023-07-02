package com.genev.a100nts.client.ui.user;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;
import static com.genev.a100nts.client.common.Constants.MAX_DISTANCE_IN_METERS;
import static com.genev.a100nts.client.utils.ActivityHolder.setActivity;
import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.genev.a100nts.client.R;
import com.genev.a100nts.client.common.Constants;
import com.genev.a100nts.client.data.login.LoginRepository;
import com.genev.a100nts.client.databinding.ActivityUserBinding;
import com.genev.a100nts.client.entities.SiteUI;
import com.genev.a100nts.client.entities.User;
import com.genev.a100nts.client.ui.login.LoginActivity;
import com.genev.a100nts.client.ui.sites.SitesActivity;
import com.genev.a100nts.client.utils.Dialog;
import com.genev.a100nts.client.utils.rest.RestService;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserActivity extends AppCompatActivity {

    private ActivityUserBinding binding;
    private Location location;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setActivity(this);

        setUpButtons();
        checkForVisitingSites();
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

        binding.buttonViewAllSites.setOnClickListener(l -> startViewSitesActivity(false, false, false, false));

        binding.buttonViewTopSites.setOnClickListener(l -> startViewSitesActivity(true, false, false, false));

        binding.buttonViewMySites.setOnClickListener(l -> startViewSitesActivityWithLocationCheck(true, false));

        binding.buttonViewFavouriteSites.setOnClickListener(l -> startViewSitesActivity(false, false, true, false));

        binding.buttonViewSitesNearMe.setOnClickListener(l -> startViewSitesActivityWithLocationCheck(false, true));

        binding.buttonViewTopUsers.setOnClickListener(l -> {
            if (hasLocationAccess()) {
                Intent viewTopUsers = new Intent(this, UserRankingActivity.class);
                startActivity(viewTopUsers);
            } else {
                showLocationDeniedDialog();
            }
        });
    }

    private void startViewSitesActivityWithLocationCheck(boolean isUserSites, boolean isNearToUserSites) {
        if (hasLocationAccess()) {
            if (isNearToUserSites && location == null) {
                Dialog.showInfoDialog(this, null, R.string.still_determining_location);
            } else {
                startViewSitesActivity(false, isUserSites, false, isNearToUserSites);
            }
        } else {
            showLocationDeniedDialog();
        }
    }

    private void startViewSitesActivity(boolean isRanking, boolean isUserSites, boolean isUserFavouriteSites, boolean isNearToUserSites) {
        Intent viewAllSites = new Intent(this, SitesActivity.class);
        viewAllSites.putExtra(Constants.ARGUMENT_IS_RANKING_VIEW, isRanking);
        viewAllSites.putExtra(Constants.ARGUMENT_IS_USER_SITES_VIEW, isUserSites);
        viewAllSites.putExtra(Constants.ARGUMENT_IS_USER_FAVOURITE_SITES_VIEW, isUserFavouriteSites);
        viewAllSites.putExtra(Constants.ARGUMENT_IS_NEAR_TO_USER_SITES_VIEW, isNearToUserSites);
        viewAllSites.putExtra(Constants.ARGUMENT_IS_EDIT_SITE_VIEW, false);
        if (isNearToUserSites) {
            viewAllSites.putExtra(Constants.ARGUMENT_USER_LATITUDE, location.getLatitude());
            viewAllSites.putExtra(Constants.ARGUMENT_USER_LONGITUDE, location.getLongitude());
        }
        startActivity(viewAllSites);
    }

    private void checkForVisitingSites() {
        if (hasLocationAccess()) {
            getLocationAndCheckForVisitingSites();
        } else {
            showLocationDeniedDialog();
        }
    }

    private boolean hasLocationAccess() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
                && locationManager.isProviderEnabled(GPS_PROVIDER)
                && locationManager.isProviderEnabled(NETWORK_PROVIDER);
    }

    @SuppressLint("MissingPermission")
    private void getLocationAndCheckForVisitingSites() {
        LocationServices.getFusedLocationProviderClient(this).getCurrentLocation(PRIORITY_HIGH_ACCURACY, new CancellationToken() {
            @Override
            public boolean isCancellationRequested() {
                return false;
            }

            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                return null;
            }
        }).addOnSuccessListener(this::cacheLocation).addOnSuccessListener(this::checkForVisitingSites);
    }

    private void cacheLocation(Location location) {
        this.location = location;
    }

    private void checkForVisitingSites(Location location) {
        User user = LoginRepository.getInstance().getLoggedUser();
        if (user == null) {
            return;
        }
        final String password = user.getPassword();
        List<Long> newlyVisitedSiteIds = getNewlyVisitedSitesIds(location);
        if (!newlyVisitedSiteIds.isEmpty()) {
            user.getVisitedSites().addAll(newlyVisitedSiteIds);
            user = RestService.visitSites(newlyVisitedSiteIds.toArray(new Long[0]));
            if (user == null) {
                finish();
                System.exit(1);
            }
            user.setPassword(password);
            LoginRepository.getInstance().setLoggedUser(user);
        }
    }

    private List<Long> getNewlyVisitedSitesIds(Location location) {
        SiteUI[] sites = RestService.getSitesForVisiting();
        if (sites == null) {
            finish();
            System.exit(1);
        }
        return Arrays.stream(sites)
                .filter(s -> {
                    float[] result = new float[1];
                    Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                            s.getLatitude(), s.getLongitude(), result);
                    return result[0] <= MAX_DISTANCE_IN_METERS;
                })
                .map(SiteUI::getId)
                .collect(Collectors.toList());
    }

    private void showLocationDeniedDialog() {
        Dialog.showWarningDialog(this, R.string.location_not_granted_title, R.string.location_not_granted);
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
