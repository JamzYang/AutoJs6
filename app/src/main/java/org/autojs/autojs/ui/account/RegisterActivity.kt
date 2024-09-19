package org.autojs.autojs.ui.account

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.autojs.autojs6.R
import org.autojs.autojs6.databinding.ActivityPreferencesBinding
import org.autojs.autojs6.databinding.ActivityRegisterBinding

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
            Toast.makeText(this, "获取验证码功能待实现", Toast.LENGTH_SHORT).show()
        }

        btnRegister.setOnClickListener {
            // 处理注册逻辑
            if (validateInputs()) {
                performRegister()
            }
        }
    }

    private fun validateInputs(): Boolean {
        // 验证输入
        // 这里应该添加更详细的验证逻辑
        return true
    }

    private fun performRegister() {
        // 执行注册逻辑
        Toast.makeText(this, "注册功能待实现", Toast.LENGTH_SHORT).show()
    }
}