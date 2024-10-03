package org.ys.game.network.api.dto;

import java.time.LocalDateTime;
import java.util.Date;


public record UserResponse(
    Integer uid,
    String phoneNumber,
    /**
     * 自己的邀请码
     */
    String myInvitationCode,
    /**
     * 邀请人编码
     */
    String inviterCode,
    MembershipType membershipType,
    LocalDateTime expirationDate,
    int freeDays
){}
