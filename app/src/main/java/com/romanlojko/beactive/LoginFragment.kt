package com.romanlojko.beactive

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.navigation.findNavController
import com.romanlojko.beactive.databinding.ActivityMainBinding
import com.romanlojko.beactive.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(layoutInflater)

        binding.loginButton.setOnClickListener { view : View ->
            view.findNavController().navigate(R.id.action_loginFragment2_to_mainApplication)
        }

        binding.registerLink.setOnClickListener { view : View ->
            view.findNavController().navigate(R.id.action_loginFragment2_to_registerFragment)
        }

        return binding.root
    }

}