package org.autojs.autojs.ui.membership

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.autojs.autojs6.databinding.ItemInvitedUserBinding

class InvitedListAdapter : ListAdapter<InvitedUser, InvitedListAdapter.ViewHolder>(InvitedUserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemInvitedUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemInvitedUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: InvitedUser) {
            binding.username.text = user.username
            binding.membershipStatus.text = user.membershipStatus
            binding.registrationDate.text = user.registrationDate
        }
    }
}

class InvitedUserDiffCallback : DiffUtil.ItemCallback<InvitedUser>() {
    override fun areItemsTheSame(oldItem: InvitedUser, newItem: InvitedUser): Boolean {
        return oldItem.username == newItem.username
    }

    override fun areContentsTheSame(oldItem: InvitedUser, newItem: InvitedUser): Boolean {
        return oldItem == newItem
    }
}

data class InvitedUser(
    val username: String,
    val membershipStatus: String,
    val registrationDate: String
)