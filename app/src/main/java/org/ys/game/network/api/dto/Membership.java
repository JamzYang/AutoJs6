package org.ys.game.network.api.dto;

import java.math.BigDecimal;

/**
 * @author yang
 * @createTime 2024年10月04日 00:22:00
 */
public record Membership(
    MembershipType type,
    BigDecimal originalPrice,
    BigDecimal currentPrice,
    int days
) {
}
