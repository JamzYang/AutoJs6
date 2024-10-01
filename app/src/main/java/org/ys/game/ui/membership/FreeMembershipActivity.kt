package org.ys.game.ui.membership

import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import org.ys.game.theme.widget.ThemeColorToolbar
import org.ys.gamecat.R
import org.ys.gamecat.databinding.ActivityFreeMembershipBinding

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
            // 显示我的邀请码逻辑
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
        // 这里处理邀请码的逻辑
        // 例如,发送到服务器验证等
        Toast.makeText(this, "邀请码: $inviteCode", Toast.LENGTH_SHORT).show()
    }

    private fun loadInvitedList() {
        // 从服务器加载邀请列表
        // 这里使用模拟数据
        val mockData = listOf(
            InvitedUser("用户1", "包月会员", "2023-05-01"),
            InvitedUser("用户2", "非会员", "2023-06-15"),
            InvitedUser("用户3", "包季会员", "2023-07-20")
        )
        invitedListAdapter.submitList(mockData)
    }
}