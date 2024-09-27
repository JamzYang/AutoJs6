package org.autojs.autojs.network.api;

import okhttp3.ResponseBody;
import org.autojs.autojs.network.api.dto.UserRequest;
import org.autojs.autojs.network.api.dto.UserResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApi {

    @POST("api/v1/users")
    Call<ResponseBody> registerUser(
        @Query("textCode") String textCode,
        @Body UserRequest body
    );

    @POST("api/v1/users/login")
    Call<ResponseBody> loginUser(@Body UserRequest body);


    @GET("api/v1/users/{uid}")
    Call<UserResponse> getUserById(@Path("uid") Integer uid);
}
