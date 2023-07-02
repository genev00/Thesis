package com.genev.a100nts.client.ui.adapters;

import static com.genev.a100nts.client.utils.ActivityHolder.getActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.genev.a100nts.client.R;
import com.genev.a100nts.client.common.Constants;
import com.genev.a100nts.client.data.login.LoginRepository;
import com.genev.a100nts.client.entities.SiteUI;
import com.genev.a100nts.client.ui.admin.AddEditSiteActivity;
import com.genev.a100nts.client.ui.sites.SiteDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.stream.Collectors;

public class SiteAdapter extends ArrayAdapter<SiteUI> {

    private final Context mContext;
    private final boolean isRankingEnabled;
    private final boolean isUserSitesView;
    private final boolean isNearToUserSitesView;
    private final boolean isEditSiteView;
    private final Double userLatitude;
    private final Double userLongitude;
    private final List<SiteUI> initialSiteList;
    private List<SiteUI> siteList;

    public SiteAdapter(Context context, List<SiteUI> sites, boolean isRankingEnabled, boolean isUserSitesView, boolean isNearToUserSitesView, boolean isEditSiteView, Double userLatitude, Double userLongitude) {
        super(context, 0, sites);
        this.mContext = context;
        this.initialSiteList = sites;
        this.siteList = sites;
        this.isRankingEnabled = isRankingEnabled;
        this.isUserSitesView = isUserSitesView;
        this.isNearToUserSitesView = isNearToUserSitesView;
        this.isEditSiteView = isEditSiteView;
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.site_list_item, parent, false);
        }

        final SiteUI currentSite = siteList.get(position);

        final TextView title = listItem.findViewById(R.id.userNamesRank);
        title.setText(currentSite.getTitle());

        final TextView province = listItem.findViewById(R.id.siteProvince);
        if (isUserSitesView) {
            province.setText(mContext.getString(R.string.visited_on));
        } else {
            province.setText(currentSite.getProvince());
        }

        final TextView town = listItem.findViewById(R.id.siteTown);
        if (isUserSitesView) {
            final String siteVisitTime = LoginRepository.getInstance().getLoggedUser().getVisitedSitesTime().stream()
                    .map(t -> t.split(Constants.ID_AND_DATETIME_SEPARATOR))
                    .filter(t -> t.length == 2 && Long.parseLong(t[0]) == currentSite.getId())
                    .map(t -> t[1])
                    .findFirst()
                    .orElse(null);
            town.setText(siteVisitTime);
        } else {
            town.setText(currentSite.getTown());
        }

        final TextView rating = listItem.findViewById(R.id.siteRating);
        final ImageView star = listItem.findViewById(R.id.starRating);
        if (isRankingEnabled) {
            star.setVisibility(View.VISIBLE);
            rating.setVisibility(View.VISIBLE);
            rating.setText(String.valueOf(currentSite.getRating()));
        } else {
            star.setVisibility(View.INVISIBLE);
            rating.setVisibility(View.INVISIBLE);
        }

        final ImageView image = listItem.findViewById(R.id.siteImage);
        Picasso.get().load(currentSite.getImageUrls().get(0)).into(image);

        listItem.setOnClickListener(view -> {
            Intent newIntent;
            if (isEditSiteView) {
                newIntent = new Intent(mContext, AddEditSiteActivity.class);
                newIntent.putExtra(Constants.ARGUMENT_IS_EDIT_SITE_VIEW, true);
                newIntent.putExtra(Constants.ARGUMENT_SITE_ID, currentSite.getId());
            } else {
                newIntent = new Intent(mContext, SiteDetailsActivity.class);
                newIntent.putExtra(Constants.ARGUMENT_SITE, currentSite);
            }
            mContext.startActivity(newIntent);
            getActivity().finish();
        });

        return listItem;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence searchValue) {
                FilterResults filteredSites = new FilterResults();
                if (isNearToUserSitesView) {
                    final float selectedDistance = Float.parseFloat(String.valueOf(searchValue)) * 1000;
                    List<SiteUI> filteredItems = initialSiteList.stream()
                            .filter(s -> {
                                float[] result = new float[1];
                                Location.distanceBetween(userLatitude, userLongitude,
                                        s.getLatitude(), s.getLongitude(), result);
                                return result[0] <= selectedDistance;
                            })
                            .collect(Collectors.toList());
                    filteredSites.values = filteredItems;
                    filteredSites.count = filteredItems.size();

                    if (filteredItems.isEmpty()) {
                        Toast.makeText(mContext.getApplicationContext(), R.string.no_near_to_me_sites,
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String searchKey = searchValue.toString().toLowerCase();
                    if (!searchKey.isEmpty()) {
                        List<SiteUI> filteredItems = initialSiteList.stream()
                                .filter(s -> s.getTitle().toLowerCase().contains(searchKey)
                                        || s.getProvince().toLowerCase().contains(searchKey)
                                        || s.getTown().toLowerCase().contains(searchKey))
                                .collect(Collectors.toList());
                        filteredSites.values = filteredItems;
                        filteredSites.count = filteredItems.size();
                        if (filteredItems.isEmpty()) {
                            Toast.makeText(mContext.getApplicationContext(), R.string.no_search_results,
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        filteredSites.values = initialSiteList;
                        filteredSites.count = initialSiteList.size();
                    }
                }
                return filteredSites;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                if (results.count > 0 || isNearToUserSitesView) {
                    siteList = (List<SiteUI>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    @Override
    public int getCount() {
        return siteList.size();
    }

}
