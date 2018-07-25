package com.mintosoft.hidephotovideo.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;

public class NewFileUtils {

    public static boolean deleteFile(Context context, @NonNull final Uri uri) {
       File mfile = new File(uri.getPath());
        if (mfile.delete()) {
            return true;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DocumentFile document = DocumentFile.fromSingleUri(context, uri);
            return document != null && document.delete();
        }

         if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {

            String Path = FileUtils.getPath(context,uri);
            if(Path!=null)
            {
                File mfile1 = new File(Path);
                if(mfile1.delete()) {
                    return true;
                }
            }

             try {
                 if (DocumentsContract.deleteDocument(context.getContentResolver(), uri)) {
                     return true;
                 }
             } catch (FileNotFoundException e) {
                 e.printStackTrace();
             }

             ContentResolver resolver = context.getContentResolver();

            try {
                resolver.delete(uri, null, null);
                return !mfile.exists();
            } catch (Exception e) {
                Log.e("", "Error when deleting file " + mfile.getAbsolutePath(), e);
                return false;
            }
        }

        return !mfile.exists();
    }

}
