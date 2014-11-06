package com.chehrak.lib;

import java.io.File;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.TextUtils;
import com.chehrak.lib.downloadManager.DownloadListener;


public class Chehrak {

    private String          path;
    private String          defaultUrl;
    public static final int SIZE_32  = 32;
    public static final int SIZE_64  = 64;
    public static final int SIZE_128 = 128;
    public static final int SIZE_256 = 256;

    private final String    baseURL  = "http://rokh.chehrak.com/";
    Handler                 handler  = new Handler();


    public interface ChehrakListener {

        /**
         * 
         * @param avatarBitmap
         * @param avatarFile
         * @param isAvatarFound : if Avatar Found Value is equals true , else means default avatar is Downloaded
         */
        public void onSuccess(Bitmap avatarBitmap, File avatarFile, boolean isAvatarFound);


        public void onFailed();
    }


    /**
     * @param path : path To Store Avatar Images
     * @param defaultUrl : Default Url To Load Image If Avatar Not Found !
     */
    public Chehrak(String path, String defaultUrl) {
        this.path = path;
        this.defaultUrl = defaultUrl;
    }


    /**
     * 
     * @param Email : Email Address LIKE Sample@gmail.com
     * @param path : path To Store Avatar Image
     * @param listener : Listener To Handle Job Down
     * @param intSize : Size Of Image , 32,64,128 Or 256 | it's Better To Use Final Fileds Like Chehrak.SIZE_32
     */
    public void getAvatar(final String Email, final ChehrakListener chehrakListener, final int size) {

        final String emailMD5 = MD5(Email);
        if (emailMD5 == null || TextUtils.isEmpty(emailMD5)) {
            onFailed(chehrakListener);
            return;
        }

        final DownloadListener downloadListener = new DownloadListener() {

            @Override
            public void onDownloadFaild() {
                onFailed(chehrakListener);
            }


            @Override
            public void onDownloadCompleted(final File File, final boolean isAvatarFound) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        Bitmap bitmap = BitmapFactory.decodeFile(File.toString());
                        onSuccess(chehrakListener, bitmap, File, isAvatarFound);
                    }
                }).start();
            }
        };

        new Thread(new Runnable() {

            @Override
            public void run() {

                String avatarUrl = baseURL + emailMD5 + "?size=" + size;
                if (defaultUrl != null && !TextUtils.isEmpty(defaultUrl)) {
                    avatarUrl += "&default=" + defaultUrl;
                }

                downloadManager dlManager = new downloadManager();
                dlManager.DownloadFile(avatarUrl, emailMD5, path, size, downloadListener);
            }
        }).start();

    }


    private void onFailed(final ChehrakListener chehrakListener) {
        if (chehrakListener == null) {
            return;
        }
        // Run in UI Thread
        handler.post(new Runnable() {

            @Override
            public void run() {
                chehrakListener.onFailed();
            }
        });
    }


    private void onSuccess(final ChehrakListener chehrakListener, final Bitmap avatarBitmap, final File avatarFile, final boolean isAvatarFound) {
        if (chehrakListener == null) {
            return;
        }
        // Run in UI Thread
        handler.post(new Runnable() {

            @Override
            public void run() {
                chehrakListener.onSuccess(avatarBitmap, avatarFile, isAvatarFound);
            }
        });
    }


    /**
     * 
     * @param simpleText : String To Encode in MD5
     * @return MD5 encoded String
     */
    private String MD5(String simpleText) {
        try {
            java.security.MessageDigest md5 = java.security.MessageDigest.getInstance("MD5");

            byte[] array = md5.digest(simpleText.getBytes());
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                stringBuffer.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return stringBuffer.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 
     * @param defaultUrl : defualt Avatar Url
     * @return
     */
    public Chehrak setDefaultUrl(String defaultUrl) {
        this.defaultUrl = defaultUrl;
        return this;
    }


    /**
     * 
     * @param path : Path To Store Avatar File
     * @return
     */
    public Chehrak setPath(String path) {
        this.path = path;
        return this;
    }


    /**
     * 
     * @return : default path of Stroage
     */
    public String getPath() {
        return path;
    }


    /**
     * 
     * @return default Avatar Url
     */
    public String getDefaultUrl() {
        return defaultUrl;
    }
}
