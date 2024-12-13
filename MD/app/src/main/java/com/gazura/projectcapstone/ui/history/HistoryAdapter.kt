package com.gazura.projectcapstone.ui.history

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gazura.projectcapstone.R
import com.gazura.projectcapstone.data.history.HistoryEntity
import java.util.Locale

class HistoryAdapter(private val onItemClick: (HistoryEntity) -> Unit) : ListAdapter<HistoryEntity, HistoryAdapter.ViewHolder>(
    DIFF_CALLBACK
) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HistoryEntity>() {
            override fun areItemsTheSame(oldItem: HistoryEntity, newItem: HistoryEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: HistoryEntity, newItem: HistoryEntity): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_history, parent, false)
        return ViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val history = getItem(position)
        holder.bind(history)
    }

    class ViewHolder(itemView: View, private val onItemClick: (HistoryEntity) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.gambar_rv_buah)
        private val textClass: TextView = itemView.findViewById(R.id.rv_nama_buah)
        private val textConfidence: TextView = itemView.findViewById(R.id.indikator)
        private val textRekomendasi: TextView = itemView.findViewById(R.id.tv_rekomendasi)
        fun bind(history: HistoryEntity) {
            val context = itemView.context
            val resources = context.resources
            Glide.with(itemView.context)
                .load(Uri.parse(history.imageUri))
                .error(R.drawable.buahnaga)
                .into(imageView)
            textClass.text = history.predictedClass
            textConfidence.text = "${history.confidence}"
            val currentLanguage = Locale.getDefault().language
            val bahasaRekomendasi =if (currentLanguage == "jv") {
                when (history.recommendation) {
                    "Buah mentah, simpan hingga matang sebelum dikonsumsi." ->
                        resources.getString(R.string.mentah)
                    "Buah busuk, disarankan untuk dibuang atau digunakan sebagai pupuk kompos." ->
                        resources.getString(R.string.busuk)
                    "Buah matang, segera konsumsi untuk rasa terbaik." ->
                        resources.getString(R.string.matang)
                    else ->history.recommendation
                }
            } else {
                history.recommendation
            }
            textRekomendasi.text = bahasaRekomendasi
            itemView.setOnClickListener {
                onItemClick(history)
            }
        }
    }
}


