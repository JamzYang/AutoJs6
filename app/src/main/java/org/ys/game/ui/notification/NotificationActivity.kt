package org.ys.game.ui.notification

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import org.ys.gamecat.R
import org.ys.gamecat.databinding.ActivityNotificationBinding

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private lateinit var adapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adapter = NotificationAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // TODO: 从数据源加载通知数据
        loadNotifications()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_notification, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_mark_all_read -> {
                markAllAsRead()
                true
            }
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadNotifications() {
        // TODO: 实现从数据源加载通知的逻辑
        val notifications = listOf(
            NotificationItem("2023-05-01 10:30:00", "这是一条测试通知", false),
            NotificationItem("2023-05-02 14:45:00", "这是另一条测试通知,内容较长。这是另一条测试通知,内容较长。这是另一条测试通知,内容较长。这是另一条测试通知,内容较长。这是另一条测试通知,内容较长。这是另一条测试通知,内容较长。这是另一条测试通知,内容较长。这是另一条测试通知,内容较长。这是另一条测试通知,内容较长。这是另一条测试通知,内容较长。这是另一条测试通知,内容较长。这是另一条测试通知,内容较长。这是另一条测试通知,内容较长。这是另一条测试通知,内容较长。这是另一条测试通知,内容较长。这是另一条测试通知,内容较长。这是另一条测试通知,内容较长。这是另一条测试通知,内容较长。这是另一条测试通知,内容较长。这是另一条测试通知,内容较长。这是另一条测试通知,内容较长。这是另一条测试通知,内容较长。这是另一条测试通知,内容较长。这是另一条测试通知,内容较长。这是另一条测试通知,内容较长。这是另一条测试通知,内容较长。这是另一条测试通知,内容较长。这是另一条测试通知,内容较长。这是另一条测试通知,内容较长。", true),
            NotificationItem("2023-05-01 10:30:00", "这是第三条测试通知,内容长度中等,内容长度中等,内容长度中等", true),
            NotificationItem("2023-05-01 10:30:00", "这是第四条测试通知,内容长度中等,内容长度中等,内容长度中等", true),
            NotificationItem("2023-05-01 10:30:00", "这是第五条测试通知,内容长度中等,内容长度中等,内容长度中等", true),
            NotificationItem("2023-05-01 10:30:00", "这是第六条测试通知,内容长度中等,内容长度中等,内容长度中等", true),
            NotificationItem("2023-05-01 10:30:00", "这是第七条测试通知,内容长度中等,内容长度中等,内容长度中等", true),
            NotificationItem("2023-05-01 10:30:00", "这是第八条测试通知,内容长度中等,内容长度中等,内容长度中等", true),

        )
        adapter.submitList(notifications)
    }

    private fun markAllAsRead() {
        // TODO: 实现将所有通知标记为已读的逻辑
        adapter.markAllAsRead()
    }
}