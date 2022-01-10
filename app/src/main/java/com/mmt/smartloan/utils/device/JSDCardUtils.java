package com.mmt.smartloan.utils.device;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class JSDCardUtils {


    public int getVideoExternalCount(Context context){
        int count = 0;
        try {
            Uri contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
            if (cursor != null) {
                count = cursor.getCount();
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return count;
    }

    public static int getVideoInternalCount(Context context){
        int count = 0;
        try {
            Uri contentUri = MediaStore.Video.Media.INTERNAL_CONTENT_URI;
            Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
            if (cursor != null) {
                count = cursor.getCount();
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return count;
    }


    public int getAudioExternalCount(Context context){
        int count = 0;
        try {
            Uri contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
            if (cursor != null) {
                count = cursor.getCount();
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return count;
    }

    public static int getAudioInternalCount(Context context){
        int count = 0;
        try {
            Uri contentUri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
            Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
            if (cursor != null) {
                count = cursor.getCount();
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return count;
    }

    public static int getImagesExternalCount(Context context){
        int count = 0;
        try {
            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
            if (cursor != null) {
                count = cursor.getCount();
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return count;
    }

    public static int getImagesInternalCount(Context context){
        int count = 0;

        try {
            Uri contentUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
            Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
            if (cursor != null) {
                count = cursor.getCount();
                cursor.close();
            }
        }catch (Exception e){

        }
        return count;
    }


}
