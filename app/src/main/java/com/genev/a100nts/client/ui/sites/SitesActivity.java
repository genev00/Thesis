package com.genev.a100nts.client.ui.sites;

import static com.genev.a100nts.client.utils.ActivityHolder.setActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.genev.a100nts.client.R;
import com.genev.a100nts.client.common.Constants;
import com.genev.a100nts.client.data.login.LoginRepository;
import com.genev.a100nts.client.databinding.ActivitySitesBinding;
import com.genev.a100nts.client.entities.SiteUI;
import com.genev.a100nts.client.entities.User;
import com.genev.a100nts.client.ui.adapters.SiteAdapter;
import com.genev.a100nts.client.utils.rest.RestService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SitesActivity extends AppCompatActivity {

    private ActivitySitesBinding binding;
    private boolean isRankingView;
    private boolean isUserSitesView;
    private boolean isUserFavouriteSitesView;
    private boolean isNearToUserSitesView;
    private boolean isEditSiteView;
    private Double userLatitude;
    private Double userLongitude;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySitesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setActivity(this);

        Intent intent = getIntent();
        isRankingView = (boolean) intent.getSerializableExtra(Constants.ARGUMENT_IS_RANKING_VIEW);
        isUserSitesView = (boolean) intent.getSerializableExtra(Constants.ARGUMENT_IS_USER_SITES_VIEW);
        isUserFavouriteSitesView = (boolean) intent.getSerializableExtra(Constants.ARGUMENT_IS_USER_FAVOURITE_SITES_VIEW);
        isNearToUserSitesView = (boolean) intent.getSerializableExtra(Constants.ARGUMENT_IS_NEAR_TO_USER_SITES_VIEW);
        isEditSiteView = (boolean) intent.getSerializableExtra(Constants.ARGUMENT_IS_EDIT_SITE_VIEW);
        if (isNearToUserSitesView) {
            userLatitude = (Double) intent.getSerializableExtra(Constants.ARGUMENT_USER_LATITUDE);
            userLongitude = (Double) intent.getSerializableExtra(Constants.ARGUMENT_USER_LONGITUDE);
            ArrayAdapter<String> spinnerAdapter = getDistanceSpinnerAdapter();
            binding.distanceSpinner.setAdapter(spinnerAdapter);
            binding.distanceSpinner.setSelection(spinnerAdapter.getCount() - 1);
        }

        List<SiteUI> sites = getSites();
        binding.searchSite.setVisibility(sites.isEmpty() || isNearToUserSitesView ? View.INVISIBLE : View.VISIBLE);
        binding.spinnerText.setVisibility(isNearToUserSitesView ? View.VISIBLE : View.INVISIBLE);
        binding.distanceSpinner.setVisibility(isNearToUserSitesView ? View.VISIBLE : View.INVISIBLE);
        binding.textNoSites.setVisibility(sites.isEmpty() ? View.VISIBLE : View.INVISIBLE);

        SiteAdapter siteAdapter = new SiteAdapter(this, sites, isRankingView, isUserSitesView, isNearToUserSitesView, isEditSiteView, userLatitude, userLongitude);
        binding.searchSite.setOnQueryTextListener(getSearchOnEditListener(siteAdapter));
        binding.distanceSpinner.setOnItemSelectedListener(getOnSpinnerItemSelectListener(siteAdapter));
        binding.sitesList.setAdapter(siteAdapter);

        checkViewTitleAndNoSitesText();
    }

    private ArrayAdapter<String> getDistanceSpinnerAdapter() {
        List<String> distances = new ArrayList<>();
        final CharSequence km = this.getString(R.string.km);
        distances.add("5 " + km);
        distances.add("10 " + km);
        distances.add("15 " + km);
        distances.add("25 " + km);
        distances.add("50 " + km);
        return new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, distances);
    }

    private List<SiteUI> getSites() {
        SiteUI[] sites = isEditSiteView ? RestService.getSitesAsAdmin() : RestService.getSites();
        if (sites == null) {
            finish();
            System.exit(1);
        }

        if (isRankingView) {
            return getSitesByRating(sites);
        } else if (isUserSitesView) {
            return getUserSites(sites);
        } else if (isUserFavouriteSitesView) {
            return getUserFavouriteSites(sites);
        } else if (isNearToUserSitesView) {
            return getNearToUserSites(sites);
        }
        return Arrays.asList(sites);
    }

    private void checkViewTitleAndNoSitesText() {
        final TextView sitesTitle = binding.textViewSitesTitle;
        final TextView noSitesText = binding.textNoSites;
        if (isRankingView) {
            sitesTitle.setText(getString(R.string.top_sites));
            noSitesText.setText(getString(R.string.not_enough_site_rankings));
        } else if (isUserSitesView) {
            sitesTitle.setText(getString(R.string.my_sites));
            noSitesText.setText(getString(R.string.not_enough_user_visites));
        } else if (isUserFavouriteSitesView) {
            sitesTitle.setText(getString(R.string.favourite_sites));
            noSitesText.setText(getString(R.string.no_favourite_sites));
        } else if (isNearToUserSitesView) {
            sitesTitle.setText(getString(R.string.sites_near_me));
            noSitesText.setText(getString(R.string.no_near_to_me_sites));
        } else {
            sitesTitle.setText(getString(R.string.sites));
            noSitesText.setText(getString(R.string.no_sites));
        }
    }

    private List<SiteUI> getSitesByRating(SiteUI[] sites) {
        return Arrays.stream(sites)
                .filter(s -> s.getRating() > 0)
                .sorted(Comparator.comparingDouble(SiteUI::getRating).reversed())
                .collect(Collectors.toList());
    }

    private List<SiteUI> getUserSites(SiteUI[] sites) {
        final User user = LoginRepository.getInstance().getLoggedUser();
        final Map<Long, LocalDateTime> visitedSitesTime = new HashMap<>();
        user.getVisitedSitesTime().stream()
                .map(t -> t.split(Constants.ID_AND_DATETIME_SEPARATOR))
                .filter(t -> t.length == 2)
                .forEach(t -> visitedSitesTime.put(Long.parseLong(t[0]),
                        LocalDateTime.parse(t[1], Constants.DATE_TIME_FORMATTER)));
        return Arrays.stream(sites)
                .filter(s -> user.getVisitedSites().contains(s.getId()))
                .sorted((s1, s2) -> visitedSitesTime.get(s2.getId()).compareTo(visitedSitesTime.get(s1.getId())))
                .collect(Collectors.toList());
    }

    private List<SiteUI> getUserFavouriteSites(SiteUI[] sites) {
        return Arrays.stream(sites)
                .filter(s -> LoginRepository.getInstance().getLoggedUser().getFavouriteSites().contains(s.getId()))
                .collect(Collectors.toList());
    }

    private List<SiteUI> getNearToUserSites(SiteUI[] sites) {
        SpinnerAdapter adapter = binding.distanceSpinner.getAdapter();
        String maxDistanceAsString = adapter.getItem(adapter.getCount() - 1).toString();
        final float selectedDistance = Float.parseFloat(maxDistanceAsString.replaceAll("[^0-9]", "")) * 1000;
        return Arrays.stream(sites)
                .filter(s -> {
                    float[] result = new float[1];
                    Location.distanceBetween(userLatitude, userLongitude,
                            s.getLatitude(), s.getLongitude(), result);
                    return result[0] <= selectedDistance;
                })
                .collect(Collectors.toList());
    }

    private SearchView.OnQueryTextListener getSearchOnEditListener(SiteAdapter siteAdapter) {
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchedValue) {
                siteAdapter.getFilter().filter(searchedValue);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchedValue) {
                siteAdapter.getFilter().filter(searchedValue);
                return false;
            }
        };
    }

    private AdapterView.OnItemSelectedListener getOnSpinnerItemSelectListener(SiteAdapter siteAdapter) {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String distanceAsString = adapterView.getItemAtPosition(position).toString().replaceAll("[^0-9]", "");
                siteAdapter.getFilter().filter(distanceAsString);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        };
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

}
