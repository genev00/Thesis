package com.genev.a100nts.client.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.genev.a100nts.client.R;
import com.genev.a100nts.client.entities.Comment;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class CommentAdapter extends ArrayAdapter<Comment> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER;

    static {
        DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
    }

    private final Context mContext;
    private final List<Comment> commentList;

    public CommentAdapter(Context context, List<Comment> comments) {
        super(context, 0, comments);
        this.mContext = context;
        this.commentList = comments;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.comment_list_item, parent, false);
        }

        final Comment currentComment = commentList.get(position);

        final TextView commenter = listItem.findViewById(R.id.userNamesComment);
        commenter.setText(currentComment.getCommenter());

        final TextView commentTime = listItem.findViewById(R.id.commentDateTime);
        commentTime.setText(currentComment.getDateTime().format(DATE_TIME_FORMATTER));

        final TextView commentBody = listItem.findViewById(R.id.commentBody);
        commentBody.setText(currentComment.getComment());

        return listItem;
    }

}
