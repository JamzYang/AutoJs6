package org.autojs.autojs.ui.account

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import org.autojs.autojs.ui.login.LoginActivity
import org.autojs.autojs.user.UserManager
import org.autojs.autojs6.databinding.ActivityAccountBinding

class AccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
    }

    private fun setupUI() {
        binding.renewButton.setOnClickListener { /* 处理续费 */ }
        binding.monthlyButton.setOnClickListener { /* 处理包月 */ }
        binding.quarterlyButton.setOnClickListener { /* 处理包季 */ }
        binding.editPhoneButton.setOnClickListener { onEditPhoneClick() } // 处理修改手机号
        binding.editPasswordButton.setOnClickListener { onEditPasswordClick() } // 处理修改密码
        binding.myInvitation.setOnClickListener { /* 处理我的邀请 */ }
        binding.logoutButton.setOnClickListener { 
            logout()
        }
    }

    private fun onEditPhoneClick() {
        // 处理编辑手机号的点击事件
        // 例如，跳转到编辑手机号的页面
        val intent = Intent(this, EditPhoneActivity::class.java)
        startActivity(intent)
    }

    private fun onEditPasswordClick() {
        // 处理编辑密码的点击事件
        // 例如，跳转到编辑密码的页面
        val intent = Intent(this, EditPasswordActivity::class.java)
        startActivity(intent)
    }

    private fun logout() {
        // 1. 清除用户登录状态
        // 这里应该调用你的登出逻辑,清除用户token等
        // 例如: UserManager.logout()
        UserManager.logout(this)
        // 2. 跳转到登录页面
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish() // 结束当前的AccountActivity
    }
}