package org.ys.game.ui.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.ys.gamecat.databinding.ItemNotificationBinding

class NotificationAdapter : ListAdapter<NotificationItem, NotificationAdapter.ViewHolder>(NotificationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun markAllAsRead() {
        val updatedList = currentList.map { it.copy(isRead = true) }
        submitList(updatedList)
    }

    class ViewHolder(private val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NotificationItem) {
            binding.timeText.text = item.time
            binding.contentText.text = item.content
            binding.contentText.maxLines = if (item.isExpanded) Int.MAX_VALUE else 5
            binding.expandArrow.rotation = if (item.isExpanded) 180f else 0f
            binding.root.alpha = if (item.isRead) 0.6f else 1f

            binding.root.setOnClickListener {
                item.isExpanded = !item.isExpanded
                binding.contentText.maxLines = if (item.isExpanded) Int.MAX_VALUE else 5
                binding.expandArrow.rotation = if (item.isExpanded) 180f else 0f
            }
        }
    }
}

class NotificationDiffCallback : DiffUtil.ItemCallback<NotificationItem>() {
    override fun areItemsTheSame(oldItem: NotificationItem, newItem: NotificationItem): Boolean {
        return oldItem.time == newItem.time
    }

    override fun areContentsTheSame(oldItem: NotificationItem, newItem: NotificationItem): Boolean {
        return oldItem == newItem
    }
}

data class NotificationItem(
    val time: String,
    val content: String,
    var isRead: Boolean,
    var isExpanded: Boolean = false
)