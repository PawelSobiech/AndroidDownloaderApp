package com.example.app3_android;

import android.os.AsyncTask;
import android.widget.TextView;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class asyncTask extends AsyncTask<String, Void, fileInfo> {
    int mRozmiar;
    String mTyp;
    private TextView fileSizeTextView;
    private TextView fileTypeTextView;
    public asyncTask(TextView fileSizeTextView, TextView fileTypeTextView) {
        this.fileSizeTextView = fileSizeTextView;
        this.fileTypeTextView = fileTypeTextView;
    }
    @Override
    protected fileInfo doInBackground(String... params) {
    //do parametr params traktuje się jak tablicę np.
        String i = params[0];
        fileInfo f = null;
        HttpsURLConnection polaczenie = null;
        try {
            URL url = new URL(i);
            polaczenie = (HttpsURLConnection) url.openConnection();
            polaczenie.setRequestMethod("GET");
            mRozmiar = polaczenie.getContentLength();
            mTyp = polaczenie.getContentType();
            f = new fileInfo(mTyp, mRozmiar);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (polaczenie != null) polaczenie.disconnect();
        }
    //wykonanie zadania...
    //w trakcie wykonania zadania można wysłać informację o
    //postępie//argumentem publishProgress też jest Integer.. params – stąd „dziwny” argument
    //po zakończeniu zadania zwracamy wynik
        return f;
    }
    @Override
    protected void onPostExecute(fileInfo f) {
        super.onPostExecute(f);

        if (f != null) {
            fileSizeTextView.setText(String.valueOf(f.fileSize));
            fileTypeTextView.setText(f.fileType);
        }
    }

}
