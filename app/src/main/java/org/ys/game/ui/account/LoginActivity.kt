package org.ys.game.ui.account

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.ResponseBody
import org.ys.game.network.RetrofitClient
import org.ys.game.network.api.ScriptApi
import org.ys.game.network.api.UserApi
import org.ys.game.network.api.dto.UserRequest
import org.ys.game.ui.main.MainActivity
import org.ys.game.user.UserManager
import org.ys.gamecat.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var etMobile: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etMobile = findViewById(R.id.et_mobile)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)

        btnLogin.setOnClickListener {
            val mobile = etMobile.text.toString()
            val password = etPassword.text.toString()

            if (mobile.isNotEmpty() && password.isNotEmpty()) {
                // 这里应该调用实际的登录逻辑
                performLogin(mobile, password)
            } else {
                Toast.makeText(this, "请输入手机号和密码", Toast.LENGTH_SHORT).show()
            }
        }

        val tvForgotPassword = findViewById<TextView>(R.id.tv_forgot_password)
        val tvRegister = findViewById<TextView>(R.id.tv_register)

        tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performLogin(mobile: String, password: String) {
        val userApi = RetrofitClient.createApi(UserApi::class.java)
        userApi.loginUser(UserRequest(mobile, password)).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // 登录成功
                    Toast.makeText(this@LoginActivity, "登录成功", Toast.LENGTH_SHORT).show()
                    UserManager.setLoggedIn(this@LoginActivity, true)

                    // 登录成功后,返回到主界面
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    // 登录失败，尝试获取错误信息
                    val errorMessage = response.errorBody()?.string() ?: "登录失败，未知错误"
                    Toast.makeText(this@LoginActivity, "登录失败: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // 网络请求失败
                Toast.makeText(this@LoginActivity, "网络请求失败: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}