package com.firebasevapecounter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.firebasevapecounter.databinding.ActivityRegisterBinding
import com.firebasevapecounter.model.User

class RegisterActivity : BaseActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        if (binding.etPassword.text.isNullOrBlank()) {
            binding.etPassword.error = "Please enter a password"
            return
        }
        if (!binding.etConfirmPassword.text.toString().equals(binding.etPassword.text.toString())) {
            binding.etConfirmPassword.error = "Both password doesn't match"
            return
        }
        showProgressbar()
        createUser()
    }

    fun createUser() {
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
        val model = User(user.uid, name, user.email.toString(), 0, 0)
        databaseRef.child("users").child(user.uid).setValue(model).addOnSuccessListener {
            hideProgressbar()
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
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