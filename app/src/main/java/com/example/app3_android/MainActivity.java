package com.example.app3_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button infoButton;
    Button downloadButton;
    TextView fileSize;
    TextView fileType;
    TextView bytesDownloaded;
    EditText addressET;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infoButton = findViewById(R.id.infoButton);
        downloadButton = findViewById(R.id.fileButton);
        fileSize = findViewById(R.id.fileSize);
        fileType = findViewById(R.id.fileType);
        bytesDownloaded = findViewById(R.id.bytesDownloaded);
        addressET = findViewById(R.id.addressET);
        progressBar = findViewById(R.id.progress_bar);

        downloadButton.setOnClickListener(downloadButtonListener);
        infoButton.setOnClickListener(infoButtonListener);

    }
    View.OnClickListener infoButtonListener = view -> {
        asyncTask zadanie = new asyncTask(fileSize, fileType);
        zadanie.execute(addressET.getText().toString());
    };
    View.OnClickListener downloadButtonListener = view -> {
        String urlText = addressET.getText().toString();
        if (urlText.isEmpty()) {
            addressET.setError("Wpisz adres URL");
        } else {
            if (ActivityCompat.checkSelfPermission(
                    MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                DownloadFile.uruchomUsluge(MainActivity.this, urlText);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(MainActivity.this, "Wymagane uprawnienia", Toast.LENGTH_SHORT).show();
                }
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[] {
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        }, 1);
            }
        }
    };
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Broadcast statusInfo = intent.getParcelableExtra(DownloadFile.INFO);
            bytesDownloaded.setText(Integer.toString(statusInfo.getDownloadedBytes()));
            progressBar.setProgress(statusInfo.getProgress());
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(DownloadFile.NOTIFICATION));
    }
    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String fileSizeValue = fileSize.getText().toString();
        String fileTypeValue = fileType.getText().toString();
        String downloadedBytesValue = bytesDownloaded.getText().toString();
        int progressBarValue = progressBar.getProgress();
        outState.putString("fileSize", fileSizeValue);
        outState.putString("fileType", fileTypeValue);
        outState.putString("bytesDownloaded", downloadedBytesValue);
        outState.putInt("progressBar", progressBarValue);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String fileSizeValue = savedInstanceState.getString("fileSize");
        String fileTypeValue = savedInstanceState.getString("fileType");
        String downloadedBytesValue = savedInstanceState.getString("bytesDownloaded");
        int progressBarValue = savedInstanceState.getInt("progressBar");
        fileSize.setText(fileSizeValue);
        fileType.setText(fileTypeValue);
        bytesDownloaded.setText(downloadedBytesValue);
        progressBar.setProgress(progressBarValue);
    }
}