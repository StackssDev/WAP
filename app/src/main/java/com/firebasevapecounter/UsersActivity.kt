package com.britanonestop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.britanonestop.databinding.ActivityUsersBinding
import com.britanonestop.model.OrderHistory
import com.britanonestop.model.User

class UsersActivity : BaseActivity() {
    private lateinit var binding: ActivityUsersBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setAdapter()
    }

    private fun setAdapter() {
        val query = databaseRef.child("users")
        val options = FirebaseRecyclerOptions.Builder<User>().setQuery(query, User::class.java)
            .setLifecycleOwner(this@UsersActivity).build()
        val adapter = UsersAdapter(options)
        binding.rvUsers.adapter = adapter
    }
}