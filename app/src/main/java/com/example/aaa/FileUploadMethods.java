package com.example.aaa;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FileUploadMethods {
    public static RequestBody createPartFromString(String markersString) {
        return RequestBody.create( markersString, MultipartBody.FORM);
    }

    public static MultipartBody.Part prepareFilePart(Context context, String partName, Uri fileUri, File file){

        String extension = MimeTypeMap.getFileExtensionFromUrl(String.valueOf(fileUri));
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        assert type != null;
        Log.d("networkDebug", "extension" + extension);
        Log.d("networkDebug", "media type:" + type);
        RequestBody requestFile = RequestBody.create(
                file,
                MediaType.parse(type)
        );
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

}
