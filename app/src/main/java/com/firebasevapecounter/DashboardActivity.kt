package com.firebasevapecounter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.firebasevapecounter.databinding.ActivityDashboardBinding
import com.firebasevapecounter.model.OrderHistory

class DashboardActivity : BaseActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnUsers.setOnClickListener {
            startActivity(Intent(this, UsersActivity::class.java))
        }

        binding.btnOrders.setOnClickListener {
            startActivity(Intent(this, OrdersActivity::class.java))
        }

    }
}