package com.mobile.app_iara.ui.spreadsheets

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.R

class SpreadSheetsAdapter :
    ListAdapter<SpreadSheets, SpreadSheetsAdapter.SpreadSheetsViewHolder>(DiffCallback) {

    class SpreadSheetsViewHolder(spreadSheetsView: View) : RecyclerView.ViewHolder(spreadSheetsView) {
        private val title: TextView = spreadSheetsView.findViewById(R.id.titleSpreadsheets)
        private val date: TextView = spreadSheetsView.findViewById(R.id.dateSpreadsheets)
        private val linkButton: ImageButton = spreadSheetsView.findViewById(R.id.imageButtonSpreadsheets)

        fun bind(spreadsheet: SpreadSheets) {
            title.text = spreadsheet.title
            date.text = spreadsheet.date

            linkButton.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, SpreadSheetsWebActivity::class.java)
                intent.putExtra(SpreadSheetsWebActivity.EXTRA_URL, spreadsheet.urlSpreadSheet)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpreadSheetsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_spreadsheets, parent, false)
        return SpreadSheetsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpreadSheetsViewHolder, position: Int) {
        val currentSheet = getItem(position)
        holder.bind(currentSheet)
    }


    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<SpreadSheets>() {
            override fun areItemsTheSame(oldItem: SpreadSheets, newItem: SpreadSheets): Boolean {
                return oldItem.urlSpreadSheet == newItem.urlSpreadSheet
            }

            override fun areContentsTheSame(oldItem: SpreadSheets, newItem: SpreadSheets): Boolean {
                return oldItem == newItem
            }
        }
    }
}