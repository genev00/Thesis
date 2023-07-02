package com.genev.a100nts.client.ui.sites;

import static com.genev.a100nts.client.utils.ActivityHolder.setActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.genev.a100nts.client.R;
import com.genev.a100nts.client.common.Constants;
import com.genev.a100nts.client.data.login.LoginRepository;
import com.genev.a100nts.client.databinding.ActivitySiteDetailsBinding;
import com.genev.a100nts.client.entities.Comment;
import com.genev.a100nts.client.entities.SiteUI;
import com.genev.a100nts.client.entities.User;
import com.genev.a100nts.client.entities.UserRole;
import com.genev.a100nts.client.ui.adapters.SiteImageSliderAdapter;
import com.genev.a100nts.client.utils.rest.RestService;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SiteDetailsActivity extends AppCompatActivity {

    private static final int TITLE_MAX_LENGTH;
    private static final int PROVINCE_AND_TOWN_MAX_LENGTH;

    static {
        TITLE_MAX_LENGTH = 28;
        PROVINCE_AND_TOWN_MAX_LENGTH = 28;
    }

    private ActivitySiteDetailsBinding binding;
    private SiteUI currentSite;
    private List<ImageView> stars;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySiteDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setActivity(this);

        Intent intent = getIntent();
        currentSite = (SiteUI) intent.getSerializableExtra(Constants.ARGUMENT_SITE);

        stars = Arrays.asList(binding.oneStar, binding.twoStar, binding.threeStar, binding.fourStar, binding.fiveStar);

        setUpButtons();
        setUpSliderView();
    }

    private void setUpButtons() {
        binding.googleMapsSite.setOnClickListener(l -> {
            final Uri googleMapsUri = Uri.parse("geo:" + currentSite.getLatitude() + ',' + currentSite.getLongitude());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, googleMapsUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Toast.makeText(this, R.string.google_maps_not_found, Toast.LENGTH_SHORT).show();
            }
        });

        String title = currentSite.getTitle();
        if (title.length() > TITLE_MAX_LENGTH) {
            title = title.substring(0, TITLE_MAX_LENGTH) + "...";
        }
        binding.siteDetailsTitle.setText(title);

        final ImageView heart = binding.heart;

        String provinceAndTown = currentSite.getProvince() + ", " + currentSite.getTown();
        if (provinceAndTown.length() > PROVINCE_AND_TOWN_MAX_LENGTH) {
            provinceAndTown = provinceAndTown.substring(0, PROVINCE_AND_TOWN_MAX_LENGTH) + "...";
        }
        binding.siteDetailsProvinceAndTown.setText(provinceAndTown);
        binding.siteDetailsDescription.setText(currentSite.getDetails());

        LoginRepository loginRepository = LoginRepository.getInstance();
        if (loginRepository.isUserLogged() && loginRepository.getLoggedUser().getRole() == UserRole.USER) {
            if (!loginRepository.getLoggedUser().getFavouriteSites().contains(currentSite.getId())) {
                heart.setImageResource(R.drawable.heart_empty);
            }

            heart.setOnClickListener(getHeartOnClickListener(heart));

            binding.buttonViewAllComments.setOnClickListener(l -> {
                currentSite = RestService.getSite(currentSite.getId());
                if (currentSite == null) {
                    finish();
                    System.exit(1);
                }
                Intent comments = new Intent(this, CommentsActivity.class);
                comments.putExtra(Constants.ARGUMENT_SITE_ID, currentSite.getId());
                comments.putExtra(Constants.ARGUMENT_COMMENTS, (ArrayList<Comment>) currentSite.getComments());
                startActivity(comments);
            });

            setUpStars();
        } else {
            heart.setVisibility(View.INVISIBLE);
            binding.votingContainer.setVisibility(View.INVISIBLE);
            binding.buttonViewAllComments.setVisibility(View.INVISIBLE);
        }
    }

    private View.OnClickListener getHeartOnClickListener(final ImageView heart) {
        return l -> {
            User user = LoginRepository.getInstance().getLoggedUser();
            final String password = user.getPassword();
            if (!user.getFavouriteSites().contains(currentSite.getId())) {
                user = RestService.addSiteToFavourites(currentSite.getId());
                heart.setImageResource(R.drawable.heart);
                Toast.makeText(this, R.string.added_to_favourites_successfully, Toast.LENGTH_SHORT).show();
            } else {
                user = RestService.removeSiteFromFavourites(currentSite.getId());
                heart.setImageResource(R.drawable.heart_empty);
                Toast.makeText(this, R.string.removed_from_favourites_successfully, Toast.LENGTH_SHORT).show();
            }
            if (user == null) {
                finish();
                System.exit(1);
            }
            user.setPassword(password);
            LoginRepository.getInstance().setLoggedUser(user);
        };
    }

    private void setUpSliderView() {
        final SliderView cutSiteImgSlider = binding.siteImgSlider;
        cutSiteImgSlider.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);

        SiteImageSliderAdapter adapter = new SiteImageSliderAdapter(currentSite.getImageUrls());
        cutSiteImgSlider.setSliderAdapter(adapter);
        cutSiteImgSlider.setScrollTimeInSec(3);
        cutSiteImgSlider.setAutoCycle(true);
        cutSiteImgSlider.startAutoCycle();
    }

    private void setUpStars() {
        loadRating();
        stars.forEach(s -> s.setOnClickListener(getStartsOnClickListener(stars.indexOf(s) + 1)));
    }

    private void loadRating() {
        stars.forEach(s -> s.setImageResource(R.drawable.star_inactive));

        Integer siteRating = RestService.getVote(currentSite.getId());
        if (siteRating == null) {
            finish();
            System.exit(1);
        }
        siteRating = siteRating == 0 ? currentSite.getRating() : siteRating;
        for (; siteRating > 0; siteRating--) {
            stars.get(siteRating - 1).setImageResource(R.drawable.star_active);
        }
    }

    private View.OnClickListener getStartsOnClickListener(int vote) {
        return l -> {
            if (!LoginRepository.getInstance().getLoggedUser().getVisitedSites().contains(currentSite.getId())) {
                Toast.makeText(this, R.string.voting_not_enabled, Toast.LENGTH_SHORT).show();
                return;
            }
            SiteUI updated = RestService.voteSite(currentSite.getId(), vote);
            if (updated == null) {
                finish();
                System.exit(1);
            }
            currentSite = updated;
            loadRating();
            Toast.makeText(this, R.string.saved_vote, Toast.LENGTH_SHORT).show();
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
