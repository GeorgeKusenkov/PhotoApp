package com.example.attractionsapp.presentation

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.attractionsapp.databinding.PhotoItemBinding

class PhotoAdapter(private var values: List<Uri>, private var date: List<String>): RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = PhotoItemBinding.inflate(LayoutInflater.from(parent.context))
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val item = values[position]
        holder.binding.date.text = date[position]
        item.let {
            Glide.with(holder.binding.photo.context)
                .load(it)
                .into(holder.binding.photo)
        }
    }

    override fun getItemCount(): Int = values.size

    class PhotoViewHolder(val binding: PhotoItemBinding): RecyclerView.ViewHolder(binding.root)
}