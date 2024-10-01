package org.ys.game.network.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ScriptApi {
    @GET("files/version/main")
    Call<String> getScriptVersion();

    @GET("files/download/main")
    Call<ResponseBody> downloadScript();
}