package com.genev.a100nts.client.ui.admin;

import static com.genev.a100nts.client.utils.ActivityHolder.setActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.genev.a100nts.client.R;
import com.genev.a100nts.client.common.Constants;
import com.genev.a100nts.client.databinding.ActivityAddEditSiteBinding;
import com.genev.a100nts.client.entities.Site;
import com.genev.a100nts.client.entities.SiteUI;
import com.genev.a100nts.client.ui.sites.SiteDetailsActivity;
import com.genev.a100nts.client.utils.rest.RestService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import lombok.var;

public class AddEditSiteActivity extends AppCompatActivity {

    private static final int FIELD_MIN_LENGTH;
    private static final int FIELD_MAX_LENGTH;
    private static final int DESCRIPTION_MIN_LENGTH;
    private static final int DESCRIPTION_MAX_LENGTH;
    private static final int LATITUDE_MIN_VALUE;
    private static final int LATITUDE_MAX_VALUE;
    private static final int LONGITUDE_MIN_VALUE;
    private static final int LONGITUDE_MAX_VALUE;
    private static final Pattern IMAGE_URL_PATTERN;
    private static final Pattern CYRILLIC_LETTERS_PATTERN;
    private static final Pattern ENGLISH_LETTERS_PATTERN;

    static {
        FIELD_MIN_LENGTH = 3;
        FIELD_MAX_LENGTH = 255;
        DESCRIPTION_MIN_LENGTH = 10;
        DESCRIPTION_MAX_LENGTH = 2048;
        LATITUDE_MIN_VALUE = -90;
        LATITUDE_MAX_VALUE = 90;
        LONGITUDE_MIN_VALUE = -180;
        LONGITUDE_MAX_VALUE = 180;
        IMAGE_URL_PATTERN = Pattern.compile("^https?://.*\\.(?:png|jpg|jpeg|gif|svg|PNG|JPG|JPEG|GIF|SVG)$");
        CYRILLIC_LETTERS_PATTERN = Pattern.compile("[а-яА-Я]");
        ENGLISH_LETTERS_PATTERN = Pattern.compile("[a-zA-Z]");
    }

    private ActivityAddEditSiteBinding binding;
    private boolean isEditSiteView;
    private Site site;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddEditSiteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setActivity(this);

        Intent intent = getIntent();
        isEditSiteView = (boolean) intent.getSerializableExtra(Constants.ARGUMENT_IS_EDIT_SITE_VIEW);
        if (isEditSiteView) {
            binding.viewTitle.setText(getString(R.string.edit_site));
            binding.addSaveButton.setText(getString(R.string.save));

            Long siteId = (Long) intent.getSerializableExtra(Constants.ARGUMENT_SITE_ID);
            site = RestService.getSiteAsAdmin(siteId);
            if (site == null) {
                finish();
                System.exit(1);
            }
            loadSiteData();
        }

        binding.addSaveButton.setOnClickListener(getOnAddSaveButtonClickListener());
    }

    private View.OnClickListener getOnAddSaveButtonClickListener() {
        return l -> {
            if (!isSiteDataValid()) {
                return;
            }
            Site siteRequest = buildSite();
            if (isEditSiteView && siteRequest.equals(site)) {
                Toast.makeText(this, R.string.no_changes_to_be_saved, Toast.LENGTH_SHORT).show();
                return;
            }
            SiteUI site = isEditSiteView ? RestService.updateSite(siteRequest) : RestService.addSite(siteRequest);
            if (site == null) {
                finish();
                System.exit(1);
            }
            Toast.makeText(this, R.string.successful_operation, Toast.LENGTH_SHORT).show();
            Intent siteDetails = new Intent(this, SiteDetailsActivity.class);
            siteDetails.putExtra(Constants.ARGUMENT_SITE, site);
            startActivity(siteDetails);
            finish();
        };
    }

    private void loadSiteData() {
        binding.siteTitle.setText(site.getTitle());
        binding.siteTitleBG.setText(site.getTitleBG());
        binding.siteProvince.setText(site.getProvince());
        binding.siteProvinceBG.setText(site.getProvinceBG());
        binding.siteTown.setText(site.getTown());
        binding.siteTownBG.setText(site.getTownBG());
        binding.siteDescription.setText(site.getDescription());
        binding.siteDescriptionBG.setText(site.getDescriptionBG());
        binding.siteLatitude.setText(site.getLatitude());
        binding.siteLongitude.setText(site.getLongitude());
        binding.siteImageLinkSt.setText(site.getImagesUI().get(0));
        binding.siteImageLinkNd.setText(site.getImagesUI().get(1));
        binding.siteImageLinkRd.setText(site.getImagesUI().get(2));
        binding.siteIsVisible.setChecked(site.isVisible());
    }

    private boolean isSiteDataValid() {
        for (var fieldEntry : buildFieldsMap().entrySet()) {
            final EditText field = fieldEntry.getKey();
            final String fieldName = field.getResources().getResourceName(field.getId()).split("/")[1];
            final String fieldValue = field.getText().toString().trim();
            final int minSize = fieldEntry.getValue().first;
            final int maxSize = fieldEntry.getValue().second;

            try {
                if (fieldName.equals("siteLatitude") || fieldName.equals("siteLongitude")) {
                    double fieldValueAsDouble;
                    try {
                        fieldValueAsDouble = Double.parseDouble(fieldValue);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException(getString(R.string.invalid_number_value));
                    }
                    if (fieldValueAsDouble < minSize || fieldValueAsDouble > maxSize) {
                        throw new IllegalArgumentException(getString(R.string.invalid_number_range, minSize, maxSize));
                    }
                } else if (fieldName.startsWith("siteImageLink")) {
                    if (!IMAGE_URL_PATTERN.matcher(fieldValue).matches()) {
                        throw new IllegalArgumentException(getString(R.string.invalid_image_url));
                    }
                } else {
                    if (fieldValue.length() < minSize || fieldValue.length() >= maxSize) {
                        throw new IllegalArgumentException(getString(R.string.invalid_character_count, minSize, maxSize));
                    }

                    if (fieldName.contains("BG")) {
                        if (ENGLISH_LETTERS_PATTERN.matcher(fieldValue).find()) {
                            throw new IllegalArgumentException(getString(R.string.invalid_value_with_english_letters));
                        }
                    } else if (CYRILLIC_LETTERS_PATTERN.matcher(fieldValue).find()) {
                        throw new IllegalArgumentException(getString(R.string.invalid_value_with_cyrillic_letters));
                    }
                }
            } catch (IllegalArgumentException e) {
                field.setError(e.getMessage());
                field.requestFocus();
                return false;
            }
        }
        return true;
    }

    private Map<EditText, Pair<Integer, Integer>> buildFieldsMap() {
        Pair<Integer, Integer> standardRanges = new Pair<>(FIELD_MIN_LENGTH, FIELD_MAX_LENGTH);
        Pair<Integer, Integer> descriptionRanges = new Pair<>(DESCRIPTION_MIN_LENGTH, DESCRIPTION_MAX_LENGTH);

        Map<EditText, Pair<Integer, Integer>> fieldsMap = new LinkedHashMap<>(13);
        fieldsMap.put(binding.siteTitle, standardRanges);
        fieldsMap.put(binding.siteTitleBG, standardRanges);
        fieldsMap.put(binding.siteProvince, standardRanges);
        fieldsMap.put(binding.siteProvinceBG, standardRanges);
        fieldsMap.put(binding.siteTown, standardRanges);
        fieldsMap.put(binding.siteTownBG, standardRanges);
        fieldsMap.put(binding.siteDescription, descriptionRanges);
        fieldsMap.put(binding.siteDescriptionBG, descriptionRanges);
        fieldsMap.put(binding.siteLatitude, new Pair<>(LATITUDE_MIN_VALUE, LATITUDE_MAX_VALUE));
        fieldsMap.put(binding.siteLongitude, new Pair<>(LONGITUDE_MIN_VALUE, LONGITUDE_MAX_VALUE));
        fieldsMap.put(binding.siteImageLinkSt, standardRanges);
        fieldsMap.put(binding.siteImageLinkNd, standardRanges);
        fieldsMap.put(binding.siteImageLinkRd, standardRanges);
        return fieldsMap;
    }

    private Site buildSite() {
        Site site = new Site();
        if (isEditSiteView) {
            site.setId(this.site.getId());
        }
        site.setTitle(getFieldStringValue(binding.siteTitle));
        site.setTitleBG(getFieldStringValue(binding.siteTitleBG));
        site.setProvince(getFieldStringValue(binding.siteProvince));
        site.setProvinceBG(getFieldStringValue(binding.siteProvinceBG));
        site.setTown(getFieldStringValue(binding.siteTown));
        site.setTownBG(getFieldStringValue(binding.siteTownBG));
        site.setDescription(getFieldStringValue(binding.siteDescription));
        site.setDescriptionBG(getFieldStringValue(binding.siteDescriptionBG));
        site.setLatitude(getFieldStringValue(binding.siteLatitude));
        site.setLongitude(getFieldStringValue(binding.siteLongitude));
        List<String> siteImages = new ArrayList<>(3);
        siteImages.add(getFieldStringValue(binding.siteImageLinkSt));
        siteImages.add(getFieldStringValue(binding.siteImageLinkNd));
        siteImages.add(getFieldStringValue(binding.siteImageLinkRd));
        site.setImagesUI(siteImages);
        site.setVisible(binding.siteIsVisible.isChecked());
        return site;
    }

    private String getFieldStringValue(EditText field) {
        return field.getText().toString().trim();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
