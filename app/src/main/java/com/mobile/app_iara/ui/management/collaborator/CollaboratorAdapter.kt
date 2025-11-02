package com.mobile.app_iara.ui.management.collaborator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobile.app_iara.R
import com.mobile.app_iara.data.model.CollaboratorData

class CollaboratorAdapter(private val onArrowClicked: (CollaboratorData) -> Unit) : ListAdapter<CollaboratorData, CollaboratorAdapter.CollaboratorViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollaboratorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collaborator, parent, false)
        return CollaboratorViewHolder(view, onArrowClicked)
    }

    override fun onBindViewHolder(holder: CollaboratorViewHolder, position: Int) {
        val collaborator = getItem(position)
        holder.bind(collaborator)
    }

    class CollaboratorViewHolder(
        itemView: View,
        private val onArrowClicked: (CollaboratorData) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val profileImageView: ImageView = itemView.findViewById(R.id.profile_image)
        private val nameTextView: TextView = itemView.findViewById(R.id.name_textview)
        private val emailTextView: TextView = itemView.findViewById(R.id.email_textview)
        private val roleTextView: TextView = itemView.findViewById(R.id.role_textview)
        private val arrowButton: ImageView = itemView.findViewById(R.id.arrow_button)

        fun bind(collaborator: CollaboratorData) {
            nameTextView.text = collaborator.name
            emailTextView.text = collaborator.email
            roleTextView.text = collaborator.roleName

            Glide.with(itemView.context)
                .load(collaborator.urlPhoto)
                .placeholder(R.drawable.ic_profile_circle)
                .error(R.drawable.ic_profile_circle)
                .circleCrop()
                .into(profileImageView)

            arrowButton.setOnClickListener {
                onArrowClicked(collaborator)
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<CollaboratorData>() {
            override fun areItemsTheSame(oldItem: CollaboratorData, newItem: CollaboratorData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: CollaboratorData, newItem: CollaboratorData): Boolean {
                return oldItem == newItem
            }
        }
    }
}