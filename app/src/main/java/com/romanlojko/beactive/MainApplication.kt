package com.romanlojko.beactive

import android.os.Bundle
import android.text.style.TtsSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.romanlojko.beactive.databinding.FragmentLoginBinding
import com.romanlojko.beactive.databinding.FragmentMainApplicationBinding
import java.util.*

class MainApplication : Fragment() {

    lateinit var binding: FragmentMainApplicationBinding

    lateinit var myAuthorization: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMainApplicationBinding.inflate(layoutInflater)

        myAuthorization = FirebaseAuth.getInstance()

        binding.buttonAddActivity.setOnClickListener{view : View ->
            view.findNavController().navigate(R.id.action_mainApplication_to_activityCounter)
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val user = myAuthorization.currentUser
        if (user == null) {
            view?.findNavController()?.navigate(R.id.action_mainApplication_to_loginFragment2)
        }
    }

}