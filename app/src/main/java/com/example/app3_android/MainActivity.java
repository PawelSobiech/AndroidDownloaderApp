package com.example.app3_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
    Toast toast;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1;

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

        infoButton.setOnClickListener(infoButtonListener);
        downloadButton.setOnClickListener(downloadButtonListener);
        toast = Toast.makeText(this,"Odmówiono uprawnień", Toast.LENGTH_SHORT);

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            // Mamy uprawnienie, możemy rozpocząć pobieranie pliku
            // ...
        } else {
            // Sprawdzamy, czy użytkownik wcześniej odmówił uprawnienia
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Wyjaśniamy mu, po co nam jest ono potrzebne...
                // (po ewentualnym wyjaśnieniu) prosimy o uprawnienia
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
            } else {
                // Poproszenie użytkownika o uprawnienia
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
            }
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_REQUEST_CODE:
                if (grantResults.length > 0 &&
                        permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Otrzymaliśmy uprawnienia, możemy rozpocząć np. pobieranie pliku
                } else {
                    // Nie otrzymaliśmy uprawnień
                    toast.show();
                    finish();
                }
                break;
            default:
                // Nieznany kod żądania – należy dostosować kod aplikacji i dodać obsługę kodu
                break;
        }
    }


    View.OnClickListener infoButtonListener = view -> {
        asyncTask zadanie = new asyncTask(fileSize, fileType);
        zadanie.execute(addressET.getText().toString());
    };

    View.OnClickListener downloadButtonListener = view ->
    {
        DownloadFile df = new DownloadFile(bytesDownloaded);
        df.execute(addressET.getText().toString());
    };
}