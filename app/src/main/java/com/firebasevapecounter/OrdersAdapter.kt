package com.firebasevapecounter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebasevapecounter.databinding.ItemOrdersBinding
import com.firebasevapecounter.model.OrderHistory
import java.text.SimpleDateFormat
import java.util.*

class OrdersAdapter(options: FirebaseRecyclerOptions<OrderHistory>) :
    FirebaseRecyclerAdapter<OrderHistory, OrdersAdapter.ViewHolder>(options) {
    class ViewHolder(val binding: ItemOrdersBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrdersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder, position: Int, model: OrderHistory
    ) {
        holder.binding.tvName.text = model.name
        holder.binding.tvCount.text = model.currentCount.toString()

        val cal = Calendar.getInstance()
        cal.timeInMillis = model.created
        val sdf = SimpleDateFormat("dd/MM/yy hh:mm a")
        holder.binding.tvDate.text = sdf.format(model.created)
    }
}