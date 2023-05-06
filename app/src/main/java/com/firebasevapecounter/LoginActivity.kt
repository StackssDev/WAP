package com.firebasevapecounter

import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import com.davidmiguel.numberkeyboard.NumberKeyboardListener
import com.firebasevapecounter.databinding.ActivityLoginBinding
import com.firebasevapecounter.model.OrderHistory
import com.firebasevapecounter.model.User
import com.firebasevapecounter.service.NotificationsService
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class LoginActivity : BaseActivity(), NumberKeyboardListener {

    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var storedVerificationId: String = ""
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etOtp.isVisible = false
        binding.etPhone.isVisible = true
        binding.numPad.setListener(this)
        binding.btnLogin.setOnClickListener {
            if (binding.etPhone.text.isNullOrBlank()) return@setOnClickListener
            showProgressbar()
            if (storedVerificationId.isNullOrBlank()) {
//                loginPhone()
                addToCount()
            } else {
                val credential = PhoneAuthProvider.getCredential(
                    storedVerificationId, binding.etOtp.text.toString()
                )
                signInWithPhoneAuthCredential(credential)
            }
        }

    }

    /*private fun loginUser() {
        auth.signInWithEmailAndPassword(
            binding.etPhone.text.toString().trim(), binding.etOtp.text.toString()
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
    }*/

    private fun addToCount() {
        databaseRef.child("users").orderByChild("phone").equalTo("+1${binding.etPhone.text}").get()
            .addOnSuccessListener {
                if (it.value != null) {
                    var model = it.children.first().getValue(User::class.java)
                    if (model?.admin == true) {
                        hideProgressbar()
                        val i = Intent(applicationContext, NotificationsService::class.java)
                        i.putExtra("data", model)
                        startService(i)
                        val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
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
                                model.userId.toString(),
                                model.currentCount
                            )
                            val map = HashMap<String, Any>()
                            map["/users/${model.userId}"] = model
                            map["/orders/${orders.created}"] = orders

                            databaseRef.updateChildren(map).addOnSuccessListener {
                                hideProgressbar()
                                val intent = Intent(this@LoginActivity, CountActivity::class.java)
                                intent.putExtra("data", model)
                                intent.putExtra("hasWin", win)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(intent)
                                finish()
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
                } else {
                    loginPhone()
                }
            }.addOnFailureListener {
                hideProgressbar()
                Toast.makeText(this, "Something went wrong...", Toast.LENGTH_SHORT).show()
            }
    }

    fun addNewUserCount(user: FirebaseUser) {
        hideProgressbar()
        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
        val model = User(
            userId = user.uid,
            email = user.email.toString(),
            phone = user.phoneNumber,
            totalCount = 1,
            currentCount = 1
        )
        intent.putExtra("user", model)
        startActivity(intent)
        finish()
    }

    fun loginPhone() {
        if (PhoneNumberUtils.isGlobalPhoneNumber("+1${binding.etPhone.text}")) {
            val options =
                PhoneAuthOptions.newBuilder(auth).setPhoneNumber("+1${binding.etPhone.text}")
                    .setTimeout(60L, TimeUnit.SECONDS).setActivity(this).setCallbacks(callbacks)
                    .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = task.result?.user
                addNewUserCount(user!!)
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


    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(TAG, "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e)
            hideProgressbar()

            when (e) {
                is FirebaseAuthInvalidCredentialsException -> {
                    Toast.makeText(this@LoginActivity, "Invalid Phone Number. Pls try again...", Toast.LENGTH_SHORT).show()
                }
/*
                is FirebaseTooManyRequestsException -> {
                    // The SMS quota for the project has been exceeded
                }

                is FirebaseAuthMissingActivityForRecaptchaException -> {
                    // reCAPTCHA verification attempted with null Activity

                }*/
                else->{
                    Toast.makeText(this@LoginActivity, e.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun onCodeSent(
            verificationId: String, token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            resendToken = token
            binding.etOtp.isVisible = true
            binding.etPhone.isVisible = false
            hideProgressbar()
        }
    }

    companion object {
        private val TAG = LoginActivity::class.java.name
    }

    override fun onLeftAuxButtonClicked() {

    }

    override fun onNumberClicked(number: Int) {
        var num = binding.etPhone.text.toString()
        num+=number.toString()
        binding.etPhone.setText(num)
    }

    override fun onRightAuxButtonClicked() {
        var num = binding.etPhone.text.toString()
        if(num.isNullOrBlank()) return
        num = num.removeRange(num.lastIndex,num.lastIndex+1)
        binding.etPhone.setText(num)
    }
}