package org.ys.game.ui.account

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.ys.gamecat.R
import org.ys.gamecat.databinding.ActivityPreferencesBinding
import org.ys.gamecat.databinding.ActivityRegisterBinding
import org.ys.game.network.RetrofitClient
import org.ys.game.network.api.UserApi
import org.ys.game.network.api.dto.UserRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.ResponseBody

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var etMobile: EditText
    private lateinit var etVerificationCode: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnGetVerificationCode: Button
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater).also {
            setContentView(it.root)
            //标题栏设置
            it.toolbar.apply {
                setTitle(R.string.text_register)
                setSupportActionBar(this)
                setNavigationOnClickListener { finish() }
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        etMobile = findViewById(R.id.et_mobile)
        etVerificationCode = findViewById(R.id.et_verification_code)
        etPassword = findViewById(R.id.et_password)
        etConfirmPassword = findViewById(R.id.et_confirm_password)
        btnGetVerificationCode = findViewById(R.id.btn_get_verification_code)
        btnRegister = findViewById(R.id.btn_register)

        btnGetVerificationCode.setOnClickListener {
            // 处理获取验证码逻辑
            sendTextCode()
        }

        btnRegister.setOnClickListener {
            if (validateInputs()) {
                performRegister()
            }
        }
    }

    private fun validateInputs(): Boolean {
        val mobile = etMobile.text.toString()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()
        val verificationCode = etVerificationCode.text.toString()

        if (mobile.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || verificationCode.isEmpty()) {
            Toast.makeText(this, "请填写所有必要信息", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show()
            return false
        }

        // 可以添加更多验证，如手机号格式、密码强度等

        return true
    }

    private fun performRegister() {
        val mobile = etMobile.text.toString()
        val password = etPassword.text.toString()
        val verificationCode = etVerificationCode.text.toString()

        if (mobile.isEmpty() || password.isEmpty() || verificationCode.isEmpty()) {
            Toast.makeText(this, "请填写所有必要信息", Toast.LENGTH_SHORT).show()
            return
        }

        val userApi = RetrofitClient.createApi(UserApi::class.java)
        val userRequest = UserRequest(mobile, password)

        userApi.registerUser(verificationCode, userRequest).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "注册成功", Toast.LENGTH_SHORT).show()
                    // 注册成功后，可以自动登录或返回登录页面
                    finish()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "登录失败，未知错误"
                    Toast.makeText(this@RegisterActivity, "注册失败: ${errorMessage}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "网络请求失败: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private  fun sendTextCode(){
        val mobile = etMobile.text.toString()
        val userApi = RetrofitClient.createApi(UserApi::class.java)
        userApi.sendTextCode(mobile).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "发送成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@RegisterActivity, "发送失败,请稍后重试", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "网络请求失败: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}