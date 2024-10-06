package org.ys.game.network.api.dto;

import java.time.LocalDateTime;

public record InvitationResponse(
    Integer inviterId,
    Integer inviteeId,
    String inviteePhone,
    LocalDateTime invitationDate,
    boolean hasPurchased,
    MembershipType purchasedType,
    LocalDateTime purchaseDate,
    int regRewardStatus,
    int purchaseRewardStatus
) { }
