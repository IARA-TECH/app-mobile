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
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast

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
                downloadFile(itemView.context, spreadsheet.urlSpreadSheet, spreadsheet.title)
            }
        }

        private fun downloadFile(context: Context, url: String, fileTitle: String) {
            if (url.isNullOrEmpty()) {
                Toast.makeText(context, "URL da planilha é inválida.", Toast.LENGTH_SHORT).show()
                return
            }

            try {
                val fileNameWithExtension = url.substringAfterLast('/')

                val request = DownloadManager.Request(Uri.parse(url))
                    .setTitle(fileTitle)
                    .setDescription("Baixando planilha...")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileNameWithExtension)
                    .setAllowedOverMetered(true)

                val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                downloadManager.enqueue(request)

                Toast.makeText(context, "Iniciando download...", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Erro ao tentar baixar: ${e.message}", Toast.LENGTH_LONG).show()
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