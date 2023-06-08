package com.example.app3_android;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DownloadFile extends IntentService {
    private static final String AKCJA_ZADANIE1 = "com.example.intent_service.action.zadanie1";
    private static final String PARAMETR1 = "com.example.intent_service.extra.parametr1";
    private static final int ID_POWIADOMIENIA = 1;
    private NotificationManager mNotificationManager;
    public int bytes = 0;
    public int fileSize = 0;
    public final static String NOTIFICATION = "com.example.intent_service.receiver";
    public final static String INFO = "info";
    public static void uruchomUsluge(Context context, String parametr) {
        Intent zamiar = new Intent(context, DownloadFile.class);
        zamiar.setAction(AKCJA_ZADANIE1);
        zamiar.putExtra(PARAMETR1, parametr);
        context.startService(zamiar);
    }
    public DownloadFile() {
        super("DownloadFileIntentService");
    }
    //metoda wykonująca zadanie
    @Override
    protected void onHandleIntent(Intent intent) {
        //usługi muszą wyświetlać powiadomienie i przejść na pierwszyplan
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        przygotujKanalPowiadomien();
        startForeground(ID_POWIADOMIENIA, utworzPowiadomienie());
        if (intent != null) {
            final String action = intent.getAction();
            //sprawdzenie o jaką akcję chodzi
            if (AKCJA_ZADANIE1.equals(action)) {
                final String param1 = intent.getStringExtra(PARAMETR1);
                //wykonanie zadania
                wykonajZadanie(param1);
            } else {
                Log.e("DownloadFile", "nieznana akcja");
            }
        }
        Log.d("DownloadFile", "usługa wykonała zadanie");
    }
    private void wykonajZadanie(String parametr) {
        HttpsURLConnection polaczenie = null;
        InputStream strumienWejsciowy = null;
        FileOutputStream strumienWyjsciowy = null;
        try {
            URL url = new URL(parametr);
            polaczenie = (HttpsURLConnection) url.openConnection();
            polaczenie.setRequestMethod("GET");
            fileSize = polaczenie.getContentLength();

            File plikRoboczy = new File(url.getFile());
            File plikWyjsciowy = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), plikRoboczy.getName());
            if(plikWyjsciowy.exists()){
                plikWyjsciowy.delete();
            }

            DataInputStream czytnik = new DataInputStream(polaczenie.getInputStream());
            strumienWyjsciowy = new FileOutputStream(plikWyjsciowy.getPath());
            final int rozmiarBufora = 4096;
            byte[] bufor = new byte[rozmiarBufora];
            int pobranoBajtow = czytnik.read(bufor, 0, rozmiarBufora);
            while(pobranoBajtow != -1){
                strumienWyjsciowy.write(bufor, 0, pobranoBajtow);
                bytes += pobranoBajtow;
                pobranoBajtow = czytnik.read(bufor, 0, rozmiarBufora);
                mNotificationManager.notify(ID_POWIADOMIENIA, utworzPowiadomienie());
                sendBroadcast(bytes, fileSize, "Pobieranie...", progressValue());
            }
            if (strumienWejsciowy != null)
            {
                try
                {
                    strumienWejsciowy.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            Log.d("DownloadFile", "pobrano bajtów: " + bytes);
            Log.d("DownloadFile", "pobrano plik");

        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (polaczenie != null) polaczenie.disconnect();
        }
    }
    private void przygotujKanalPowiadomien(){
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String name = getString(R.string.app_name);
        NotificationChannel channel = new NotificationChannel("DownloadFile", name, NotificationManager.IMPORTANCE_LOW);
        mNotificationManager.createNotificationChannel(channel);
    }

    private Notification utworzPowiadomienie(){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("downloadedBytes", bytes);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        //Powrót do MainActivity po kliknięciu na powiadomienie
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("DownloadFile")
                .setProgress(100, progressValue(), false)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_HIGH);

        builder.setOngoing(progressValue() < 100);
        builder.setChannelId("DownloadFile");
        return builder.build();
    }

    private void sendBroadcast(int downloadedBytes, int fileSize, String status, int progress){
        Intent intent = new Intent(NOTIFICATION);
        Broadcast statusInfo = new Broadcast(downloadedBytes, fileSize, status, progress);
        intent.putExtra(INFO, statusInfo);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private int progressValue(){
        if(fileSize == 0){
            return 0;
        }else
            return (int) ((bytes * 100L) / fileSize);
    }
}
