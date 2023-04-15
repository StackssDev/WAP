package com.firebasevapecounter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebasevapecounter.databinding.ActivityOrdersBinding
import com.firebasevapecounter.model.OrderHistory

class OrdersActivity : BaseActivity() {

    private lateinit var binding: ActivityOrdersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setAdapter()
    }

    private fun setAdapter() {
        val query = databaseRef.child("orders").orderByChild("created").limitToLast(20)
        val options = FirebaseRecyclerOptions.Builder<OrderHistory>()
            .setQuery(query, OrderHistory::class.java).setLifecycleOwner(this@OrdersActivity)
            .build()
        val adapter = OrdersAdapter(options)
        binding.rvOrders.adapter = adapter
    }
}