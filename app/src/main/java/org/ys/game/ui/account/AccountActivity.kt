package org.ys.game.ui.account

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.ys.game.user.UserManager
import org.ys.gamecat.R
import org.ys.gamecat.databinding.ActivityAccountBinding
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.graphics.Color
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.widget.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.common.BitMatrix
import okhttp3.ResponseBody
import org.ys.game.network.RetrofitClient
import org.ys.game.network.api.UserApi
import org.ys.game.network.api.dto.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import org.ys.game.network.api.dto.Membership
import org.ys.game.network.api.dto.MembershipType
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // ... 其他初始化代码 ...

        UserManager.refreshUserInfo(this) {
            updateMembershipUI()
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
        binding.monthlyButton.setOnClickListener { purchaseMembership("MONTHLY") }
        binding.quarterlyButton.setOnClickListener { purchaseMembership("QUARTERLY") }
        binding.editPhoneButton.setOnClickListener { onEditPhoneClick() } // 处理修改手机号
        binding.editPasswordButton.setOnClickListener { onEditPasswordClick() } // 处理修改密码
        binding.logoutButton.setOnClickListener {
            logout()
        }
        updateMembershipUI()
        updateMembershipPrices()
    }

    private fun purchaseMembership(type: String) {
        val uid = UserManager.getUserId(this)!!.toInt()
        val userApi = RetrofitClient.createApi(UserApi::class.java)

        userApi.purchaseMembership(uid, type).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        val qrCodeUrl = response.body()?.string()
                        showQRCodeDialog(qrCodeUrl ?: "")
                    } else {
                        Toast.makeText(this@AccountActivity, "购买失败：${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                runOnUiThread {
                    Toast.makeText(this@AccountActivity, "购买失败：${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun showQRCodeDialog(qrCodeContent: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_qr_code, null)
        val qrCodeImageView = dialogView.findViewById<ImageView>(R.id.qrCodeImageView)

        // 生成二维码
        val qrCodeBitmap = generateQRCode(qrCodeContent)
        qrCodeImageView.setImageBitmap(qrCodeBitmap)

        AlertDialog.Builder(this)
            .setTitle("微信支付")
            .setView(dialogView)
            .setPositiveButton("关闭", null)
            .show()
    }

    private fun generateQRCode(content: String): Bitmap {
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
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

    private fun updateMembershipUI() {
        val user = UserManager.getCurrentUser()
        when (user?.membershipType) {
            MembershipType.NON -> binding.membershipStatus.text = "非会员"
            else -> {
                val expirationDate = user?.expirationDate?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                binding.membershipStatus.text = "会员到期：$expirationDate"
            }
        }
    }

    private fun updateMembershipPrices() {
        val memberships = UserManager.getMemberships()
        val monthlyMembership = memberships.find { it.type == MembershipType.MONTHLY }
        val quarterlyMembership = memberships.find { it.type == MembershipType.QUARTERLY }

        monthlyMembership?.let {
            binding.monthlyPriceText.text = "原价：${it.originalPrice}元   现价：${it.currentPrice}元"
        }

        quarterlyMembership?.let {
            binding.quarterlyPriceText.text = "原价：${it.originalPrice}元   现价：${it.currentPrice}元"
        }
    }
}