package com.extempo.typescan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.extempo.typescan.R
import com.extempo.typescan.databinding.ListItemDocumentBinding
import com.extempo.typescan.model.DocumentItem
import kotlinx.android.synthetic.main.list_item_document.view.*

class DocumentPagedListAdapter(private val context: Context): PagedListAdapter<DocumentItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ListItemDocumentBinding.inflate(inflater, parent, false)
        return ViewHolder(parent.context, binding.root, binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).apply { holder.binding.documentItem = getItem(position) }
    }

    class ViewHolder(val context: Context, itemView: View, val binding: ListItemDocumentBinding): RecyclerView.ViewHolder(itemView) {
        val documentTitle: TextView = itemView.list_item_document_title
        val documentAuthor: TextView = itemView.list_item_document_author
        val documentDate: TextView = itemView.list_item_document_date
    }

    companion object {
        private val DIFF_CALLBACK = object: DiffUtil.ItemCallback<DocumentItem>() {
            override fun areItemsTheSame(oldItem: DocumentItem, newItem: DocumentItem) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: DocumentItem, newItem: DocumentItem) = oldItem == newItem
        }
    }
}