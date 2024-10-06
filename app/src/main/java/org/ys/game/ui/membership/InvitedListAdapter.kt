package org.ys.game.ui.membership

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.ys.game.network.api.dto.InvitationResponse
import org.ys.gamecat.databinding.ItemInvitedUserBinding
import java.time.format.DateTimeFormatter

class InvitedListAdapter : ListAdapter<InvitationResponse, InvitedListAdapter.ViewHolder>(InvitationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemInvitedUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemInvitedUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(invitation: InvitationResponse) {
            binding.apply {
                inviteePhone.text = invitation.inviteePhone
                membership.text = if (invitation.hasPurchased) {
                    invitation.purchasedType?.desc ?: "未知会员类型"
                } else {
                    "非会员"
                }
                registrationDate.text = invitation.invitationDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
            }
        }
    }

    private class InvitationDiffCallback : DiffUtil.ItemCallback<InvitationResponse>() {
        override fun areItemsTheSame(oldItem: InvitationResponse, newItem: InvitationResponse): Boolean {
            return oldItem.inviteeId == newItem.inviteeId
        }

        override fun areContentsTheSame(oldItem: InvitationResponse, newItem: InvitationResponse): Boolean {
            return oldItem == newItem
        }
    }
}