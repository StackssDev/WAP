package com.firebasevapecounter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebasevapecounter.databinding.ItemOrdersBinding
import com.firebasevapecounter.databinding.ItemUserBinding
import com.firebasevapecounter.model.OrderHistory
import com.firebasevapecounter.model.User
import java.text.SimpleDateFormat
import java.util.*

class UsersAdapter(options: FirebaseRecyclerOptions<User>) :
    FirebaseRecyclerAdapter<User, UsersAdapter.ViewHolder>(options) {
    class ViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder, position: Int, model: User
    ) {
        holder.binding.tvName.text = model.name
        holder.binding.tvCount.text = model.currentCount.toString()
    }
}