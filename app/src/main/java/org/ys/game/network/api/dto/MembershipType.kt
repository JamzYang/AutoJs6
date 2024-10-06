package org.ys.game.network.api.dto

enum class MembershipType(val value: Int, val desc: String) {
    NON(0,"非会员"),
    FREE(1,"免费会员"),
    MONTHLY(2,"月卡会员"),
    QUARTERLY(3,"季卡会员"),
    SEMIANNUALLY(4,"半年卡会员"),
    YEARLY(5,"年卡会员"),
    ETERNALLY(9,"永久会员")
}