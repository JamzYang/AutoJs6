package org.ys.game.network.api.dto

enum class MembershipType(val value: Int) {
    NON(0),
    FREE(1),
    MONTHLY(2),
    QUARTERLY(3),
    SEMIANNUALLY(4),
    YEARLY(5),
    ETERNALLY(9)
}