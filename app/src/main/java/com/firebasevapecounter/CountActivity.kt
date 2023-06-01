package com.britanonestop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.britanonestop.databinding.ActivityCountBinding
import com.britanonestop.model.User

class CountActivity : BaseActivity() {
    private lateinit var binding: ActivityCountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val hasWin = intent.getBooleanExtra("hasWin", false)
        val data = intent.serializable<User>("data")

        if (hasWin) {
            binding.tvCount.text = "You win a free VAPE!!"
        } else {
            binding.tvCount.text = "${data?.currentCount} / 10"
        }

        Handler(Looper.getMainLooper()).postDelayed({
            logout()
        }, 30000)

        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        val intent = Intent(this@CountActivity, LoginActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {

    }

}