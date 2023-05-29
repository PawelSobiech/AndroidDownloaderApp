package com.example.app3_android;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DownloadFile extends AsyncTask<String, Integer, Integer> {

private final TextView bytesDownloaded;
        static int ROZMIAR_BLOKU = 1024;
        public DownloadFile(TextView bytesDownloaded) {
                this.bytesDownloaded = bytesDownloaded;
        }
        @Override
        protected Integer doInBackground(String... params) {
                //do parametr params traktuje się jak tablicę np.
                String i = params[0];
                int mPobranychBajtow = 0;
                HttpsURLConnection polaczenie = null;
                try {
                        URL url = new URL(i);
                        polaczenie = (HttpsURLConnection) url.openConnection();
                        polaczenie.setRequestMethod("GET");
                        File plikRoboczy = new File(url.getFile());
                        File plikWyjsciowy = new File(Environment.getExternalStorageDirectory() + File.separator+ plikRoboczy.getName());
                        if (plikWyjsciowy.exists())
                        {
                                plikWyjsciowy.delete();
                        }

                        InputStream strumienZSieci = null;
                        FileOutputStream strumienDoPliku = null;
                        //tworzenie połączenia, tworzenie pliku wyjściowego...
                        DataInputStream czytnik = new DataInputStream(polaczenie.getInputStream());
                        strumienDoPliku = new FileOutputStream(plikWyjsciowy.getPath());
                        byte bufor[] = new byte[ROZMIAR_BLOKU];
                        int pobrano = czytnik.read(bufor, 0, ROZMIAR_BLOKU);
                        while (pobrano != -1)
                        {
                                strumienDoPliku.write(bufor, 0, pobrano);
                                mPobranychBajtow += pobrano;
                                pobrano = czytnik.read(bufor, 0, ROZMIAR_BLOKU);
                                publishProgress(mPobranychBajtow);
                        }
                        if (strumienZSieci != null)
                        {
                                try
                                {
                                        strumienZSieci.close();
                                } catch (IOException e)
                                {
                                        e.printStackTrace();
                                }
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                } finally {
                        if (polaczenie != null) polaczenie.disconnect();
                }
                //wykonanie zadania...
                //w trakcie wykonania zadania można wysłać informację o
                //postępie//argumentem publishProgress też jest Integer.. params – stąd „dziwny” argument
                //po zakończeniu zadania zwracamy wynik
                return mPobranychBajtow;

        }
    @Override
    protected void onPostExecute(Integer mPobranychBajtow) {
            super.onPostExecute(mPobranychBajtow);
            if (mPobranychBajtow != null) {
            bytesDownloaded.setText(String.valueOf(mPobranychBajtow));
            }
    }

}