package ru.gft.termux2ssh;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about);
        ImageButton exitBtn = findViewById(R.id.exitBtn);

        exitBtn.setOnClickListener((View v) -> {
            finish(); // Closes the current activity
        });
    }
}