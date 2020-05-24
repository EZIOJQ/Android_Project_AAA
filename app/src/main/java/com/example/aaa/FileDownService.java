package com.example.aaa;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FileDownService {

    @GET("test_get")
    Call<ResponseBody> getContent(
            @Query("shareLink") String shareLink
    );
}
