package org.autojs.autojs.ui.account

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.autojs.autojs.ui.main.MainActivity
import org.autojs.autojs.user.UserManager
import org.autojs.autojs6.R

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
            // 处理忘记密码逻辑
            Toast.makeText(this, "忘记密码功能待实现", Toast.LENGTH_SHORT).show()
        }

        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performLogin(mobile: String, password: String) {
        // 这里应该实现实际的登录逻辑
        // 例如,调用API进行身份验证
        // 暂时用简单的Toast消息代替
        Toast.makeText(this, "登录成功: $mobile", Toast.LENGTH_SHORT).show()
        
        // 设置登录状态
        UserManager.setLoggedIn(this, true)
        
        // 登录成功后,返回到主界面
       val intent = Intent(this, MainActivity::class.java)
       intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
       startActivity(intent)
       finish()
    }
}