package ru.gft.termux2ssh;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about);
        ImageButton exitBtn = findViewById(R.id.exitBtn);
        ImageView telegram = findViewById(R.id.tg);
        ImageView github = findViewById(R.id.github);
        MaterialButton btnInstaller = findViewById(R.id.btnInstaller);

        exitBtn.setOnClickListener((View v) -> {
            finish();
        });
        telegram.setOnClickListener((View v) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(MainActivity.Companion.getTg_link()));
            startActivity(intent);
        });
        github.setOnClickListener((View v) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(MainActivity.Companion.getGithub_link()));
            startActivity(intent);
        });
        btnInstaller.setOnClickListener((View v) -> {
            if (this.checkSelfPermission("com.termux.permission.RUN_COMMAND")
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{"com.termux.permission.RUN_COMMAND"}, 1);
            } else {
                TermuxHelper.Companion.sendsToTermux(this, this, List.of("apt update -y", "apt upgrade -y", "apt install -y openssh inetutils"));
            }
        });
    }
}