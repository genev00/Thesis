package com.genev.a100nts.client.ui.error;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.genev.a100nts.client.common.Constants;
import com.genev.a100nts.client.databinding.ActivityErrorBinding;

public class ErrorActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.genev.a100nts.client.databinding.ActivityErrorBinding binding = ActivityErrorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String errorMessage = (String) intent.getSerializableExtra(Constants.ARGUMENT_ERROR_MESSAGE);
        binding.errorMessage.setText(errorMessage);

        binding.buttonExitOnFailure.setOnClickListener(o -> {
            finish();
            System.exit(1);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        System.exit(1);
    }

}
