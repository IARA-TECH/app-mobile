package com.mobile.app_iara.ui.spreadsheets

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.R

class SpreadSheetsAdapter(private val spreadSheets: List<SpreadSheets>) :
    RecyclerView.Adapter<SpreadSheetsAdapter.SpreadSheetsViewHolder>(){

    class SpreadSheetsViewHolder(spreadSheetsView:  View) : RecyclerView.ViewHolder(spreadSheetsView) {
        val title: TextView = spreadSheetsView.findViewById(R.id.titleSpreadsheets)
        val date: TextView =  spreadSheetsView.findViewById(R.id.dateSpreadsheets)
        val link: ImageButton = spreadSheetsView.findViewById(R.id.imageButtonSpreadsheets)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpreadSheetsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_spreadsheets, parent, false)
        return SpreadSheetsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpreadSheetsViewHolder, position: Int) {
        val currentSheet = spreadSheets[position]
        holder.title.text = currentSheet.title
        holder.date.text = currentSheet.date

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context

            val intent = Intent(context, SpreadSheetsWebActivity::class.java)
            intent.putExtra(SpreadSheetsWebActivity.EXTRA_URL, currentSheet.urlSpreadSheet)
            context.startActivity(intent)
        }
    }
    override fun getItemCount() = spreadSheets.size
}