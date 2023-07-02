package com.genev.a100nts.client.ui.sites;

import static com.genev.a100nts.client.utils.ActivityHolder.setActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.genev.a100nts.client.R;
import com.genev.a100nts.client.common.Constants;
import com.genev.a100nts.client.databinding.ActivityCommentsBinding;
import com.genev.a100nts.client.entities.Comment;
import com.genev.a100nts.client.ui.adapters.CommentAdapter;
import com.genev.a100nts.client.utils.rest.RestService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    private static final Comparator<Comment> COMMENT_COMPARATOR;
    private static final int COMMENT_MIN_LENGTH;
    private static final int COMMENT_MAX_LENGTH;

    static {
        COMMENT_COMPARATOR = (c1, c2) -> c2.getDateTime().compareTo(c1.getDateTime());
        COMMENT_MIN_LENGTH = 5;
        COMMENT_MAX_LENGTH = 300;
    }

    private ActivityCommentsBinding binding;
    private Long siteId;

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCommentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setActivity(this);

        Intent intent = getIntent();
        siteId = (Long) intent.getSerializableExtra(Constants.ARGUMENT_SITE_ID);
        List<Comment> comments = (List<Comment>) intent.getSerializableExtra(Constants.ARGUMENT_COMMENTS);

        if (!comments.isEmpty()) {
            binding.textNotComments.setVisibility(View.INVISIBLE);
            comments.sort(COMMENT_COMPARATOR);
            binding.commentsList.setAdapter(
                    new CommentAdapter(this, comments)
            );
        }

        setUpButtons();
    }

    private void setUpButtons() {
        binding.editTextComment.addTextChangedListener(getCommentTextChangedListener());

        binding.buttonPostComment.setOnClickListener(l -> {
            Comment newComment = RestService.postComment(new Comment(
                    siteId,
                    binding.editTextComment.getText().toString().trim(),
                    LocalDateTime.now()
            ));
            if (newComment == null) {
                finish();
                System.exit(1);
            }
            Toast.makeText(this, R.string.saved_comment, Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @NonNull
    private TextWatcher getCommentTextChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.buttonPostComment.setEnabled(false);
                int commentLength = binding.editTextComment.getText().toString().trim().length();
                if (commentLength < COMMENT_MIN_LENGTH || commentLength > COMMENT_MAX_LENGTH) {
                    binding.editTextComment.setError(getString(R.string.invalid_comment, COMMENT_MIN_LENGTH, COMMENT_MAX_LENGTH));
                } else {
                    binding.buttonPostComment.setEnabled(true);
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
