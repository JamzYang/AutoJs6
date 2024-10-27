package org.ys.game.network.api;

import okhttp3.ResponseBody;
import org.ys.gamecat.BuildConfig;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ScriptApi {

    @GET("files/version/{projectName}/main")
    Call<String> getScriptVersion(@Path("projectName") String projectName);

    @GET("files/download/{projectName}/main")
    Call<ResponseBody> downloadScript(@Path("projectName") String projectName);
}