package ru.climatlab.service.ui.requestReport

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.photo_list_item.view.*
import ru.climatlab.service.R

/**
 * Created by tridetch on 01.05.2019. CliamtLabService
 */
class PhotoAdapter(private var photos: MutableList<Uri>, private val interactionListener: InteractionListener) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.photo_list_item, parent, false)
        return PhotoViewHolder(view, interactionListener)
    }

    override fun getItemCount(): Int = photos.size

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    fun setupDataSet(dataSet: MutableList<Uri>) {
        photos = dataSet
        notifyDataSetChanged()
    }

    fun itemAdded() {
        notifyItemInserted(photos.size)
    }

    fun itemRemoved(position: Int) {
        notifyItemRemoved(position)
    }

    class PhotoViewHolder(view: View, private val listener: InteractionListener) : RecyclerView.ViewHolder(view) {
        fun bind(imageUri: Uri) {
            itemView.imageViewDamagePhoto.setImageURI(imageUri)
            itemView.imageButtonRemove.setOnClickListener { listener.onPhotoRemove(adapterPosition) }
        }
    }

    interface InteractionListener {
        fun onPhotoRemove(position: Int)
    }
}