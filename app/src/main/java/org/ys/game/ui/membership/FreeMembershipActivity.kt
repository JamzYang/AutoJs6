package org.ys.game.ui.membership

import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import org.ys.game.theme.widget.ThemeColorToolbar
import org.ys.game.network.RetrofitClient
import org.ys.game.network.api.InvitationApi
import org.ys.game.network.api.dto.InvitationResponse
import org.ys.game.user.UserManager
import org.ys.gamecat.R
import org.ys.gamecat.databinding.ActivityFreeMembershipBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.content.ContextCompat
import org.ys.game.network.api.dto.UserResponse

class FreeMembershipActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFreeMembershipBinding
    private lateinit var invitedListAdapter: InvitedListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFreeMembershipBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<ThemeColorToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true) // 显示返回按钮
            setDisplayShowHomeEnabled(true) // 显示 Home 图标
            title = "我的邀请" // 设置标题
        }

        setupUI()
        loadInvitedList()
    }

    private fun setupUI() {
        binding.fillInviteCodeButton.setOnClickListener {
            showInviteCodeDialog()
        }

        binding.myInviteCodeButton.setOnClickListener {
            showMyInviteCode()
        }

        invitedListAdapter = InvitedListAdapter()
        binding.invitedList.apply {
            layoutManager = LinearLayoutManager(this@FreeMembershipActivity)
            adapter = invitedListAdapter
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showInviteCodeDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_invite_code, null)
        val inviteCodeInput = dialogView.findViewById<EditText>(R.id.inviteCodeInput)

        builder.setView(dialogView)
            .setTitle("填写邀请码")
            .setPositiveButton("确定") { _, _ ->
                val inviteCode = inviteCodeInput.text.toString()
                // 处理邀请码逻辑
                handleInviteCode(inviteCode)
            }
            .setNegativeButton("取消") { dialog, _ ->
                dialog.cancel()
            }

        builder.create().show()
    }

    private fun handleInviteCode(inviteCode: String) {
        val userId = UserManager.getUserId(this)?.toIntOrNull()
        if (userId == null) {
            Toast.makeText(this, "无法获取用户ID", Toast.LENGTH_SHORT).show()
            return
        }

        val invitationApi = RetrofitClient.createApi(InvitationApi::class.java)
        invitationApi.putInvitorCode(userId, inviteCode).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@FreeMembershipActivity, "邀请码填写成功", Toast.LENGTH_SHORT).show()
                    // 可能需要更新用户信息
                    UserManager.refreshUserInfo(this@FreeMembershipActivity) { _ ->
                        // 刷新完成后的操作，如果需要的话
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "未知错误"
                    Toast.makeText(this@FreeMembershipActivity, "邀请码填写失败: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(this@FreeMembershipActivity, "网络错误: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showMyInviteCode() {
        val inviteCode = UserManager.getMyInvitationCode()
        if (inviteCode.isNullOrEmpty()) {
            AlertDialog.Builder(this)
                .setTitle("我的邀请码")
                .setMessage("暂无邀请码")
                .setPositiveButton("确定", null)
                .show()
        } else {
            val dialog = AlertDialog.Builder(this)
                .setTitle("我的邀请码")
                .setMessage(inviteCode)
                .setPositiveButton("确定", null)
                .setNeutralButton("复制") { _, _ ->
                    copyToClipboard(inviteCode)
                }
                .create()

            dialog.show()

            // 获取"复制"按钮并设置为主题色
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL)?.let { button ->
//                button.setTextColor(ContextCompat.getColor(this, R.color.theme_color)) // 主题色
            }
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("邀请码", text)
        clipboard.setPrimaryClip(clip)
    }

    private fun loadInvitedList() {
        val userId = UserManager.getUserId(this)?.toIntOrNull()
        if (userId == null) {
            Toast.makeText(this, "无法获取用户ID", Toast.LENGTH_SHORT).show()
            return
        }

        val invitationApi = RetrofitClient.createApi(InvitationApi::class.java)
        invitationApi.listInvitees(userId).enqueue(object : Callback<List<InvitationResponse>> {
            override fun onResponse(call: Call<List<InvitationResponse>>, response: Response<List<InvitationResponse>>) {
                if (response.isSuccessful) {
                    val invitationList = response.body() ?: emptyList()
                    invitedListAdapter.submitList(invitationList)
                } else {
                    Toast.makeText(this@FreeMembershipActivity, "获取邀请列表失败", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<InvitationResponse>>, t: Throwable) {
                Toast.makeText(this@FreeMembershipActivity, "网络错误", Toast.LENGTH_SHORT).show()
            }
        })
    }
}