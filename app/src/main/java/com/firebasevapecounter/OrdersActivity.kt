package com.firebasevapecounter

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebasevapecounter.databinding.ActivityOrdersBinding
import com.firebasevapecounter.model.OrderHistory
import com.firebasevapecounter.model.Status
import com.firebasevapecounter.model.User

class OrdersActivity : BaseActivity() {

    private lateinit var binding: ActivityOrdersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setAdapter()
    }

    private fun setAdapter() {
        val allOrdersQuery = databaseRef.child("orders").orderByChild("created").limitToLast(20)
        val pendingOrdersQuery = databaseRef.child("orders").orderByChild("status").equalTo(
            Status.PENDING
        )
        val options = FirebaseRecyclerOptions.Builder<OrderHistory>()
            .setQuery(pendingOrdersQuery, OrderHistory::class.java)
            .setLifecycleOwner(this@OrdersActivity).build()
        val adapter = OrdersAdapter(options) { data, isAccepted ->
            updateStatus(data, isAccepted)
        }
        binding.rvOrders.adapter = adapter
    }

    private fun updateStatus(data: OrderHistory, accepted: Boolean) {
        if (accepted) {
            val map = HashMap<String, Any>()
            data.status = Status.ACCEPTED
            map["/orders/${data.created}"] = data

            databaseRef.updateChildren(map).addOnSuccessListener {
                hideProgressbar()
                Toast.makeText(
                    this, "Status Updated", Toast.LENGTH_SHORT
                ).show()
            }.addOnFailureListener {
                hideProgressbar()
                Toast.makeText(
                    this, "Something went wrong...", Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            databaseRef.child("users").child(data.userId).get().addOnSuccessListener {
                val model = it.getValue(User::class.java)
                val map = HashMap<String, Any?>()
                if (model?.currentCount == 0 && model.totalCount > 0) {
                    model.currentCount = 9
                } else if (model?.currentCount == 0 && model.totalCount == 0) {
                    model.currentCount = 0
                } else {
                    model?.currentCount = (model?.currentCount ?: 0) - 1
                }
                map["/users/${model?.userId}"] = model
                data.status = Status.REJECTED
                map["/orders/${data.created}"] = data

                databaseRef.updateChildren(map).addOnSuccessListener {
                    hideProgressbar()
                    Toast.makeText(
                        this, "Status Updated", Toast.LENGTH_SHORT
                    ).show()
                }.addOnFailureListener {
                    hideProgressbar()
                    Toast.makeText(
                        this, "Something went wrong...", Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener {
                hideProgressbar()
                Toast.makeText(this, "Something went wrong...", Toast.LENGTH_SHORT).show()
            }
        }
    }
}