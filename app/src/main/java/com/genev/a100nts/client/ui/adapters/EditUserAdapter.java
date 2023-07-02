package com.genev.a100nts.client.ui.adapters;

import static com.genev.a100nts.client.utils.ActivityHolder.getActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.genev.a100nts.client.R;
import com.genev.a100nts.client.data.login.LoginRepository;
import com.genev.a100nts.client.entities.User;
import com.genev.a100nts.client.entities.UserRole;
import com.genev.a100nts.client.utils.rest.RestService;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class EditUserAdapter extends ArrayAdapter<User> {

    private final Context mContext;
    private final List<User> userList;

    public EditUserAdapter(Context context, List<User> users) {
        super(context, 0, users);
        this.mContext = context;
        this.userList = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.edit_user_list_item, parent, false);
        }

        AtomicReference<User> currentUser = new AtomicReference<>(userList.get(position));

        final TextView names = listItem.findViewById(R.id.userNames);
        final String currentName = currentUser.get().getFirstName() + " " + currentUser.get().getLastName();
        names.setText(currentName);

        final TextView email = listItem.findViewById(R.id.userEmail);
        email.setText(currentUser.get().getEmail());

        final TextView role = listItem.findViewById(R.id.userRole);
        role.setText(currentUser.get().getRole().name());

        final Button button = listItem.findViewById(R.id.buttonChange);
        if (currentUser.get().getId().equals(LoginRepository.getInstance().getLoggedUser().getId())) {
            button.setVisibility(View.INVISIBLE);
        }
        button.setOnClickListener(l -> {
            final UserRole newRole = currentUser.get().getRole() == UserRole.USER ? UserRole.ADMIN : UserRole.USER;
            User user = RestService.changeUserRole(currentUser.get().getId(), newRole);
            if (user == null) {
                getActivity().finish();
                System.exit(1);
            }
            Toast.makeText(mContext.getApplicationContext(), R.string.successful_operation, Toast.LENGTH_SHORT).show();
            currentUser.set(user);
            role.setText(user.getRole().name());
            userList.set(position, user);
        });

        return listItem;
    }

}