package com.genev.a100nts.client.ui.user;

import static com.genev.a100nts.client.utils.ActivityHolder.setActivity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.genev.a100nts.client.databinding.ActivityUserRankingBinding;
import com.genev.a100nts.client.entities.User;
import com.genev.a100nts.client.ui.adapters.UserAdapter;
import com.genev.a100nts.client.utils.rest.RestService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserRankingActivity extends AppCompatActivity {

    private ActivityUserRankingBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserRankingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setActivity(this);

        binding.userRankingList.setAdapter(
                new UserAdapter(this, getUsersAndCheckTexts())
        );
    }

    private List<User> getUsersAndCheckTexts() {
        User[] users = RestService.getUsersForRanking();
        if (users == null) {
            finish();
            System.exit(1);
        }

        final List<User> sortedUsersList = getSortedUsers(users);
        if (sortedUsersList.isEmpty()) {
            binding.textNotEnoughUserRankings.setVisibility(View.VISIBLE);
        } else {
            binding.textNotEnoughUserRankings.setVisibility(View.INVISIBLE);
        }
        return sortedUsersList;
    }

    private List<User> getSortedUsers(User[] users) {
        return Arrays.stream(users)
                .sorted((st, nd) -> {
                    int result = Integer.compare(nd.getVisitedSites().size(), st.getVisitedSites().size());
                    if (result == 0) {
                        result = st.getFirstName().compareToIgnoreCase(nd.getFirstName());
                        if (result == 0) {
                            result = st.getLastName().compareToIgnoreCase(nd.getLastName());
                        }
                    }
                    return result;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
