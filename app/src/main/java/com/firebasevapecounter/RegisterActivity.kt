package com.britanonestop

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.britanonestop.databinding.ActivityRegisterBinding
import com.britanonestop.model.OrderHistory
import com.britanonestop.model.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase

class RegisterActivity : BaseActivity() {
    private var user: User? = null
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = intent.serializable("user")

        binding.btnRegister.setOnClickListener {
            validateUser()
        }

    }

    fun validateUser() {
        if (binding.etName.text.isNullOrBlank()) {
            binding.etName.error = "Please enter your name"
            return
        }
        if (binding.etEmail.text.isNullOrBlank()) {
            binding.etEmail.error = "Please enter your email"
            return
        }
        if (!binding.etEmail.text.isValidEmail()) {
            binding.etEmail.error = "Please enter valid email"
            return
        }
        /*if (binding.etPassword.text.isNullOrBlank()) {
            binding.etPassword.error = "Please enter a password"
            return
        }
        if (!binding.etConfirmPassword.text.toString().equals(binding.etPassword.text.toString())) {
            binding.etConfirmPassword.error = "Both password doesn't match"
            return
        }*/
        showProgressbar()
//        createUser()
        addUser()

    }

/*    fun createUser() {
        auth.createUserWithEmailAndPassword(
            binding.etEmail.text.toString().trim(), binding.etPassword.text.toString()
        ).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "createUserWithEmail:success")
                val user = auth.currentUser
                user?.let { addUser(binding.etName.text.toString(), it) }
            } else {
                hideProgressbar()
                // If sign in fails, display a message to the user.
                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                Toast.makeText(
                    baseContext, "Authentication failed.", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun addUser(name: String, user: FirebaseUser) {
        val model = User(user.uid, name, user.email.toString(), user.phoneNumber, 0, 0)
        databaseRef.child("users").child(user.uid).setValue(model).addOnSuccessListener {
            hideProgressbar()
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }.addOnFailureListener {
            hideProgressbar()
            Toast.makeText(
                this, "Something went wrong...", Toast.LENGTH_SHORT
            ).show()
        }
    }*/

    fun addUser() {
        user?.name = binding.etName.text.toString()
        user?.email = binding.etEmail.text.toString()
        user?.totalCount = 1
        user?.currentCount = 1
        databaseRef.child("users").child(user?.userId!!).setValue(user).addOnSuccessListener {
            hideProgressbar()
            var win = false
            hideProgressbar()

            val orders = OrderHistory(
                System.currentTimeMillis(), user?.name.toString(), user?.userId.toString(), 1
            )
            val map = HashMap<String, Any>()
            map["/users/${user?.userId}"] = user!!
            map["/orders/${orders.created}"] = orders

            databaseRef.updateChildren(map).addOnSuccessListener {
                hideProgressbar()
                val intent = Intent(this@RegisterActivity, CountActivity::class.java)
                intent.putExtra("data", user)
                intent.putExtra("hasWin", win)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_NEW_TASK  and Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                hideProgressbar()
                Toast.makeText(
                    this, "Something went wrong...", Toast.LENGTH_SHORT
                ).show()
            }
//            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_NEW_TASK
//            startActivity(intent)
        }.addOnFailureListener {
            hideProgressbar()
            Toast.makeText(
                this, "Something went wrong...", Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        private val TAG = RegisterActivity::class.java.name
    }

}