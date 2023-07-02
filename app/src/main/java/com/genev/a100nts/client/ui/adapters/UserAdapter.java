package com.genev.a100nts.client.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.genev.a100nts.client.R;
import com.genev.a100nts.client.entities.User;

import java.util.List;

public class UserAdapter extends ArrayAdapter<User> {

    private final Context mContext;
    private final List<User> userList;

    public UserAdapter(Context context, List<User> users) {
        super(context, 0, users);
        this.mContext = context;
        this.userList = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.user_list_item, parent, false);
        }

        final User currentUser = userList.get(position);

        final TextView userPosition = listItem.findViewById(R.id.userRankPos);
        userPosition.setText(String.valueOf(position + 1));

        final TextView names = listItem.findViewById(R.id.userNamesRank);
        final String currentName = currentUser.getFirstName() + " " + currentUser.getLastName();
        names.setText(currentName);

        final TextView sitesCount = listItem.findViewById(R.id.userVisitedSites);
        final String sitesText = currentUser.getVisitedSites().size() + "/100";
        sitesCount.setText(sitesText);

        return listItem;
    }

}
