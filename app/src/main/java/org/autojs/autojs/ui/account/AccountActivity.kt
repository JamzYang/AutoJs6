package org.autojs.autojs.ui.account

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.autojs.autojs.user.UserManager
import org.autojs.autojs6.R
import org.autojs.autojs6.databinding.ActivityAccountBinding
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class AccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // ... 其他初始化代码 ...

        UserManager.refreshUserInfo(this) { isMember ->
            updateMembershipUI(isMember)
        }

        setupUI()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
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
        // 显示修改手机号对话框
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_phone, null)
        val newPhoneNumber = dialogView.findViewById<EditText>(R.id.newPhoneNumber)
        val verificationCode = dialogView.findViewById<EditText>(R.id.verificationCode)

        val dialog = AlertDialog.Builder(this)
            .setTitle("修改手机号")
            .setView(dialogView)
            .setPositiveButton("确定") { _, _ ->
                // 处理确定按钮点击事件
                val phone = newPhoneNumber.text.toString()
                val code = verificationCode.text.toString()
                // 验证输入并处理逻辑
                if (phone.isNotEmpty() && code.isNotEmpty()) {
                    // 处理修改手机号逻辑
                    Toast.makeText(this, "手机号已修改", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "请输入完整信息", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .create()

        dialogView.findViewById<Button>(R.id.sendVerificationCodeButton).setOnClickListener {
            // 处理发送验证码逻辑
            Toast.makeText(this, "验证码已发送", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    private fun onEditPasswordClick() {
        // 显示修改密码对话框
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_password, null)
        val oldPassword = dialogView.findViewById<EditText>(R.id.oldPassword)
        val newPassword = dialogView.findViewById<EditText>(R.id.newPassword)
        val confirmPassword = dialogView.findViewById<EditText>(R.id.confirmPassword)

        val dialog = AlertDialog.Builder(this)
            .setTitle("修改密码")
            .setView(dialogView)
            .setPositiveButton("确定") { _, _ ->
                // 处理确定按钮点击事件
                val oldPwd = oldPassword.text.toString()
                val newPwd = newPassword.text.toString()
                val confirmPwd = confirmPassword.text.toString()
                // 验证输入并处理逻辑
                if (newPwd.length >= 8 && newPwd == confirmPwd) {
                    // 处理修改密码逻辑
                    Toast.makeText(this, "密码已修改", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "请检查输入", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .create()

        dialog.show()
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

    private fun updateMembershipUI(isMember: Boolean) {
        if (isMember) {
            binding.membershipStatus.text = "会员状态：${UserManager.getExpirationDateString()}"
            binding.renewButton.visibility = View.VISIBLE
        } else {
            binding.membershipStatus.text = "会员状态：非会员"
            binding.renewButton.visibility = View.GONE
        }
    }
}