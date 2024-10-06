package org.ys.game.network.api;

import java.util.List;
import okhttp3.ResponseBody;
import org.ys.game.network.api.dto.InvitationResponse;
import org.ys.game.network.api.dto.UserRequest;
import org.ys.game.network.api.dto.UserResponse;
import retrofit2.Call;
import retrofit2.http.*;

public interface InvitationApi {

  @PUT("api/v1/invitations/{uid}")
  Call<UserResponse> putInvitorCode(
      @Path("uid") Integer uid,
      @Query("inviterCode") String inviterCode);



  @GET("api/v1/invitations/list/{uid}")
  Call<List<InvitationResponse>> listInvitees(@Path("uid") Integer uid);

}
