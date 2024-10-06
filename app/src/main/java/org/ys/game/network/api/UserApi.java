package org.ys.game.network.api;

import java.util.List;
import okhttp3.ResponseBody;
import org.ys.game.network.api.dto.Membership;
import org.ys.game.network.api.dto.UserRequest;
import org.ys.game.network.api.dto.UserResponse;
import retrofit2.Call;
import retrofit2.http.*;

public interface UserApi {

    @POST("api/v1/users")
    Call<ResponseBody> registerUser(
        @Query("textCode") String textCode,
        @Body UserRequest body
    );

    @POST("api/v1/users/login")
    Call<UserResponse> loginUser(@Body UserRequest body);

    @PUT("api/v1/users/{uid}/password")
    Call<ResponseBody> updatePassword(
        @Path("uid") Integer uid,
        @Query("oldPwd") String oldPwd,
        @Query("password") String password);


    @GET("api/v1/users/{uid}")
    Call<UserResponse> getUserById(@Path("uid") Integer uid);

    @GET("api/v1/users/{phone}/send")
    Call<ResponseBody> sendTextCode(@Path("phone") String phone);

    @FormUrlEncoded
    @POST("api/v1/memberships/purchase")
    Call<ResponseBody> purchaseMembership(
        @Field("uid") Integer uid,
        @Field("type") String type
    );

    @GET("api/v1/memberships/list")
    Call<List<Membership>> listMembership();

}
