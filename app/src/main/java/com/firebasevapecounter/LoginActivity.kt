package com.firebasevapecounter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.firebasevapecounter.databinding.ActivityLoginBinding
import com.firebasevapecounter.model.OrderHistory
import com.firebasevapecounter.model.User
import com.firebasevapecounter.service.NotificationsService
import com.google.firebase.auth.FirebaseUser

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            if (binding.etEmail.text.isNullOrBlank() || binding.etPassword.text.isNullOrBlank()) return@setOnClickListener

            showProgressbar()
            loginUser()
        }

    }

    private fun loginUser() {
        auth.signInWithEmailAndPassword(
            binding.etEmail.text.toString().trim(), binding.etPassword.text.toString()
        ).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithEmail:success")
                val user = auth.currentUser
                user?.let { addToCount(it) }
            } else {
                hideProgressbar()
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithEmail:failure", task.exception)
                Toast.makeText(
                    baseContext, "Authentication failed.", Toast.LENGTH_SHORT
                ).show()

            }
        }
    }


    private fun addToCount(user: FirebaseUser) {
        databaseRef.child("users").child(user.uid).get().addOnSuccessListener {
            var model = it.getValue(User::class.java)
            if (model?.admin == true) {
                hideProgressbar()
                val i = Intent(applicationContext, NotificationsService::class.java)
                i.putExtra("data",model)
                startService(i)
                val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)

            } else {
                var win = false
                if (model != null) {
                    hideProgressbar()
                    if (model.currentCount + 1 >= 10) {
                        model.currentCount = 0
                        win = true
                    } else {
                        model.currentCount += 1
                    }
                    model.totalCount += 1

                    val orders = OrderHistory(
                        System.currentTimeMillis(),
                        model.name.toString(),
                        user.uid,
                        model.currentCount
                    )
                    val map = HashMap<String, Any>()
                    map["/users/${user.uid}"] = model
                    map["/orders/${System.currentTimeMillis()}"] = orders

                    databaseRef.updateChildren(map).addOnSuccessListener {
                        hideProgressbar()
                        val intent = Intent(this@LoginActivity, CountActivity::class.java)
                        intent.putExtra("data", model)
                        intent.putExtra("hasWin", win)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                    }.addOnFailureListener {
                        hideProgressbar()
                        Toast.makeText(
                            this, "Something went wrong...", Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    hideProgressbar()
                }
            }
        }.addOnFailureListener {
            hideProgressbar()
            Toast.makeText(this, "Something went wrong...", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private val TAG = LoginActivity::class.java.name
    }
}