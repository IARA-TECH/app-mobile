package com.mobile.app_iara.ui.management.collaborator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.R

class RolesAdapter(
    private val roles: List<Role>,
    private val onRoleSelected: (Role) -> Unit
) : RecyclerView.Adapter<RolesAdapter.RoleViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    init {
        selectedPosition = roles.indexOfFirst { it.isSelected }
    }

    inner class RoleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val roleName: TextView = itemView.findViewById(R.id.role_name)
        private val radioButton: RadioButton = itemView.findViewById(R.id.role_selection)

        fun bind(role: Role) {
            roleName.text = role.name
            radioButton.isChecked = role.isSelected

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    if (selectedPosition != RecyclerView.NO_POSITION) {
                        roles[selectedPosition].isSelected = false
                        notifyItemChanged(selectedPosition)
                    }

                    selectedPosition = adapterPosition
                    roles[selectedPosition].isSelected = true
                    notifyItemChanged(selectedPosition)

                    onRoleSelected(roles[selectedPosition])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_role, parent, false)
        return RoleViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoleViewHolder, position: Int) {
        holder.bind(roles[position])
    }

    override fun getItemCount() = roles.size
}