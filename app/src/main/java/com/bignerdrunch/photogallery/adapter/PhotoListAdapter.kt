package com.bignerdrunch.photogallery.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bignerdrunch.photogallery.R
import com.bignerdrunch.photogallery.api.GalleryItem
import com.bignerdrunch.photogallery.databinding.ListItemGalleryBinding

class PhotoListAdapter(private val galleryItem: List<GalleryItem>)
    :RecyclerView.Adapter<PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemGalleryBinding.inflate(inflater, parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val item = galleryItem[position]
        holder.bind(item)
    }

    override fun getItemCount() = galleryItem.size
}

class PhotoViewHolder(private val binding: ListItemGalleryBinding)
    :RecyclerView.ViewHolder(binding.root) {

    fun bind(galleryItem: GalleryItem) {

        /**
         * Load the image referenced by data and set it on this ImageView using Coil library
         */
        binding.itemImageView.load(galleryItem.url) {

            /**
             * filling the empty places
             */
            placeholder(R.drawable.bill_up_close)
        }
    }

}