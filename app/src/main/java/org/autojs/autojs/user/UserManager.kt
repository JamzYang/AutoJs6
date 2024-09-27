package org.autojs.autojs.user

import android.content.Context
import org.autojs.autojs.network.RetrofitClient
import org.autojs.autojs.network.api.UserApi
import org.autojs.autojs.network.api.dto.MembershipType
import org.autojs.autojs.network.api.dto.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

object UserManager {
    private const val PREF_NAME = "user_pref"
    private const val KEY_LOGGED_IN = "logged_in"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_MEMBERSHIP_TYPE = "membership_type"
    private const val KEY_EXPIRATION_DATE = "expiration_date"

    private var currentUser: UserResponse? = null

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
        val expirationDate = Date(prefs.getLong(KEY_EXPIRATION_DATE, 0))

        return checkMemberStatus(membershipType, expirationDate)
    }

    private fun checkMemberStatus(membershipType: MembershipType?, expirationDate: Date?): Boolean {
        return membershipType != MembershipType.NON && 
               (expirationDate?.after(Date()) == true || membershipType == MembershipType.ETERNALLY)
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
    }

    private fun saveMembershipInfo(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            currentUser?.let {
                putInt(KEY_MEMBERSHIP_TYPE, it.membershipType?.value ?: MembershipType.NON.value)
                putLong(KEY_EXPIRATION_DATE, it.expirationDate?.time ?: 0)
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
}

