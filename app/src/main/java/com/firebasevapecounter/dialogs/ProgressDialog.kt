package com.britanonestop.dialogs

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.britanonestop.R
import com.britanonestop.databinding.DialogProgressBinding

class ProgressDialog @JvmOverloads constructor(
    context: Context
) : AlertDialog(context, R.style.ProgressDialog) {

    private lateinit var binding: DialogProgressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogProgressBinding.inflate(LayoutInflater.from(context), null, false)
        setContentView(binding.root)
        setCanceledOnTouchOutside(false)
        setCancelable(false)
    }
}