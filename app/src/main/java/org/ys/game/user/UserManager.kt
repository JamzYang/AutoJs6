package org.ys.game.user

import android.content.Context
import org.ys.game.network.RetrofitClient
import org.ys.game.network.api.UserApi
import org.ys.game.network.api.dto.Membership
import org.ys.game.network.api.dto.MembershipType
import org.ys.game.network.api.dto.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset

object UserManager {
    private const val PREF_NAME = "user_pref"
    private const val KEY_LOGGED_IN = "logged_in"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_MEMBERSHIP_TYPE = "membership_type"
    private const val KEY_EXPIRATION_DATE = "expiration_date"

    private var currentUser: UserResponse? = null

    private var memberships: List<Membership> = initMemberships()

    fun setLoggedIn(context: Context, loggedIn: Boolean, userId: String? = null) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putBoolean(KEY_LOGGED_IN, loggedIn)
            userId?.let { putString(KEY_USER_ID, it) }
            apply()
        }
    }

    fun isLoggedIn(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_LOGGED_IN, false)
    }

    fun getUserId(context: Context): String? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_USER_ID, null)
    }

    fun isMember(context: Context): Boolean {
        // 优先从内存中读取
        currentUser?.let {
            return checkMemberStatus(it.membershipType, it.expirationDate)
        }

        // 如果内存中没有，从本地缓存读取
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val membershipType = MembershipType.values().find { it.value == prefs.getInt(KEY_MEMBERSHIP_TYPE, 0) } ?: MembershipType.NON
        
        val expirationDate = LocalDateTime.ofEpochSecond(prefs.getLong(KEY_EXPIRATION_DATE, 0), 0, ZoneOffset.of("+8"))

        return checkMemberStatus(membershipType, expirationDate)
    }

    private fun checkMemberStatus(membershipType: MembershipType?, expirationDate: LocalDateTime?): Boolean {
        return membershipType != MembershipType.NON && 
               (expirationDate?.isAfter(LocalDateTime.now()) == true || membershipType == MembershipType.ETERNALLY)
    }

    fun refreshUserInfo(context: Context, callback: (Boolean) -> Unit) {
        val userId = getUserId(context)?.toIntOrNull()
        if (userId == null) {
            callback(false)
            return
        }

        val userApi = RetrofitClient.createApi(UserApi::class.java)
        userApi.getUserById(userId).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    currentUser = response.body()
                    saveMembershipInfo(context)
                    callback(isMember(context))
                } else {
                    callback(isMember(context))
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                callback(isMember(context))
            }
        })

        userApi.listMembership().enqueue(object : Callback<List<Membership>> {
            override fun onResponse(call: Call<List<Membership>>, response: Response<List<Membership>>) {
                if (response.isSuccessful) {
                    memberships = response.body() ?: initMemberships()
                }
            }

            override fun onFailure(call: Call<List<Membership>>, t: Throwable) {}
        })
    }

    private fun saveMembershipInfo(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            currentUser?.let {
                putInt(KEY_MEMBERSHIP_TYPE, it.membershipType?.value ?: MembershipType.NON.value)
                putLong(KEY_EXPIRATION_DATE, it.expirationDate?.toEpochSecond(ZoneOffset.of("+8")) ?: 0)
            }
            apply()
        }
    }

    fun logout(context: Context) {
        setLoggedIn(context, false)
        currentUser = null
        clearMembershipInfo(context)
    }

    private fun clearMembershipInfo(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            remove(KEY_MEMBERSHIP_TYPE)
            remove(KEY_EXPIRATION_DATE)
            apply()
        }
    }

    fun getExpirationDateString(): String {
        return currentUser?.expirationDate?.toString() ?: "未知"
    }

    fun getCurrentUser(): UserResponse? {
        return currentUser
    }

    private fun initMemberships(): List<Membership> {
        return listOf(
            Membership(MembershipType.NON, BigDecimal.ZERO, BigDecimal.ZERO, 0),
            Membership(MembershipType.FREE, BigDecimal.ZERO, BigDecimal.ZERO, 0),
            Membership(MembershipType.MONTHLY, BigDecimal("19.9"), BigDecimal("9.9"), 30),
            Membership(MembershipType.QUARTERLY, BigDecimal("55"), BigDecimal("45"), 60),
//            Membership(MembershipType.SEMIANNUALLY, BigDecimal("100"), BigDecimal("100"), 180),
//            Membership(MembershipType.YEARLY, BigDecimal("200"), BigDecimal("200"), 365),
//            Membership(MembershipType.ETERNALLY, BigDecimal("400"), BigDecimal("400"), 1000)
        )
    }

    fun getMemberships(): List<Membership> {
        return memberships
    }

    fun getMyInvitationCode(): String? {
        return currentUser?.myInvitationCode
    }
}

