package com.firebasevapecounter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.firebasevapecounter.databinding.ActivityCountBinding
import com.firebasevapecounter.model.User

class CountActivity : BaseActivity() {
    private lateinit var binding: ActivityCountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val hasWin = intent.getBooleanExtra("hasWin", false)
        val data = intent.serializable<User>("data")

        if(hasWin){
            binding.tvCount.text = "You win a free VAPE!!"
        }else{
            binding.tvCount.text = "${data?.currentCount} / 10"
        }

        binding.btnLogout.setOnClickListener {
            val intent = Intent(this@CountActivity,MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

    }
}