package com.example.multimedia.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class FileUtils {

    public static void savePic(final String fileName, final Bitmap bitmap) {
        File file = new File(fileName);
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static void saveToPictures(final Context context, final Bitmap bitmap, final String fileName,
                               final OnPictureSavedListener listener) {
        new SaveTask(context, bitmap, fileName, listener).execute();
    }

    private static class SaveTask extends AsyncTask<Void, Void, Void> {

        private final Bitmap bitmap;
        private final String fileName;
        private final OnPictureSavedListener listener;
        private final Handler handler;
        private final Context context;

        public SaveTask(final Context context, final Bitmap bitmap,
                        final String fileName, final OnPictureSavedListener listener) {
            this.context = context;
            this.bitmap = bitmap;
            this.fileName = fileName;
            this.listener = listener;
            handler = new Handler();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            saveImage(fileName, bitmap);
            return null;
        }

        private void saveImage(final String fileName, final Bitmap image) {
            File file = new File(fileName);
            try {
                image.compress(Bitmap.CompressFormat.JPEG, 80, new FileOutputStream(file));
                MediaScannerConnection.scanFile(context,
                        new String[]{
                                file.toString()
                        }, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(final String path, final Uri uri) {
                                if (listener != null) {
                                    handler.post(new Runnable() {

                                        @Override
                                        public void run() {
                                            listener.onPictureSaved(uri);
                                        }
                                    });
                                }
                            }
                        });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public interface OnPictureSavedListener {
        void onPictureSaved(Uri uri);
    }

}
