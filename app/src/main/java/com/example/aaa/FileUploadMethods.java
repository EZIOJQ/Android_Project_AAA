package com.example.aaa;

import android.content.Context;
import android.net.Uri;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FileUploadMethods {
    public static RequestBody createPartFromString(String markersString) {
        return RequestBody.create(MultipartBody.FORM, markersString);
    }

    public static MultipartBody.Part prepareFilePart(Context context, String partName, Uri fileUri, File file){
        RequestBody requestFile = RequestBody.create(
                MediaType.parse(context.getContentResolver().getType(fileUri)),
                file
        );
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

}
