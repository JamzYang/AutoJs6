package org.ys.game.ui.account

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.ys.gamecat.R
import org.ys.gamecat.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var etMobile: EditText
    private lateinit var etVerificationCode: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnGetVerificationCode: Button
    private lateinit var btnConfirm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater).also {
            setContentView(it.root)
            //标题栏设置
            it.toolbar.apply {
                setTitle(R.string.text_forgot_password)
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
        btnConfirm = findViewById(R.id.btn_confirm)

        btnGetVerificationCode.setOnClickListener {
            // 处理获取验证码逻辑
            Toast.makeText(this, "获取验证码功能待实现", Toast.LENGTH_SHORT).show()
        }

        btnConfirm.setOnClickListener {
            // 处理重置密码逻辑
            if (validateInputs()) {
                performResetPassword()
            }
        }
    }

    private fun validateInputs(): Boolean {
        // 验证输入
        // 这里应该添加更详细的验证逻辑
        return true
    }

    private fun performResetPassword() {
        // 执行重置密码逻辑
        Toast.makeText(this, "重置密码功能待实现", Toast.LENGTH_SHORT).show()
    }
}