package ru.climatlab.service.ui.requestReport

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.file_list_item.view.*
import ru.climatlab.service.R
import ru.climatlab.service.data.model.SelectedFile

/**
 * Created by tridetch on 01.05.2019. CliamtLabService
 */
class FileAdapter(private var files: MutableList<SelectedFile>, private val interactionListener: InteractionListener) : RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.file_list_item, parent, false)
        return FileViewHolder(view, interactionListener)
    }

    override fun getItemCount(): Int = files.size

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.bind(files[position])
    }

    fun setupDataSet(dataSet: MutableList<SelectedFile>) {
        files = dataSet
        notifyDataSetChanged()
    }

    fun itemAdded() {
        notifyItemInserted(files.size)
    }

    fun itemRemoved(position: Int) {
        notifyItemRemoved(position)
    }

    class FileViewHolder(view: View, private val listener: InteractionListener) : RecyclerView.ViewHolder(view) {
        fun bind(file: SelectedFile) {
            itemView.file_name.text = file.file_name
            itemView.imageButtonRemove.setOnClickListener { listener.onFileRemove(adapterPosition) }
        }
    }

    interface InteractionListener {
        fun onFileRemove(position: Int)
    }
}