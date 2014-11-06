package com.chehrak.lib;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.text.TextUtils;


public class downloadManager {

    interface DownloadListener {

        /**
         * 
         * @param File : Image File That Downloaded
         */
        public void onDownloadCompleted(File File, boolean isAvatarFound);


        public void onDownloadFaild();
    }


    /**
     * 
     * @param URL : URL To Download Image
     * @param path : Path To Store File
     * @param listener : Listener To Notify Result
     */
    public void DownloadFile(String URL, String emailMD5, String path, int intSize, DownloadListener listener) {

        HttpURLConnection connection = null;
        InputStream InputStream = null;
        FileOutputStream OutStream = null;

        boolean isAvatarFound = true;
        try {
            URL url = new URL(URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            InputStream = connection.getInputStream();

            // if chehrak not found , X-Source-Avatar will be Empty
            try {
                String Source = connection.getHeaderField("X-Source-Avatar");
                if (Source == null || TextUtils.isEmpty(Source)) {
                    isAvatarFound = false;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                isAvatarFound = false;
            }

            String FileName = emailMD5 + ".jpg";
            File File = new File(path + "/" + FileName);
            if (File.exists()) {
                File.delete();
            }

            OutStream = new FileOutputStream(File);
            byte[] buffer = new byte[8 * 1024];
            int len = 0;
            while ((len = InputStream.read(buffer)) > 0) {
                OutStream.write(buffer, 0, len);
            }
            if (listener != null) {
                listener.onDownloadCompleted(File, isAvatarFound);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onDownloadFaild();
            }
        } finally {
            try {
                InputStream.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            try {
                OutStream.flush();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            try {
                OutStream.close();
                connection.disconnect();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
