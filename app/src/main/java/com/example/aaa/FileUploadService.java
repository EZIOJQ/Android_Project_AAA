package com.example.aaa;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface FileUploadService {

    @Multipart
    @POST("upload")
    Call<ResponseBody> uploadFiles(
            @Part MultipartBody.Part imageFile,
            @Part ArrayList<MultipartBody.Part> audioFiles,
            @Part ("markers") RequestBody markers
            );
}
