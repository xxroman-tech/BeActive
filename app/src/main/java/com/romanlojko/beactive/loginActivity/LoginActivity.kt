package com.romanlojko.beactive.loginActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import com.romanlojko.beactive.databinding.ActivityLoginBinding
import com.romanlojko.beactive.databinding.ActivityMainBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}