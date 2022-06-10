package com.romanlojko.beactive

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.romanlojko.beactive.databinding.FragmentLoginBinding
import com.romanlojko.beactive.databinding.FragmentProfileBinding

class Profile : Fragment() {

    lateinit var binding: FragmentProfileBinding

    lateinit var myAuthorization: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        myAuthorization = FirebaseAuth.getInstance()

        initButtonListeners()

        return binding.root
    }

    /**
     * Metoda ktora inicializuje clicklistenery pre buttony vo fragmente
     */
    private fun initButtonListeners() {
        binding.buttonLogOut.setOnClickListener { view: View ->
            if (myAuthorization!=null && myAuthorization.currentUser != null) {
                myAuthorization.signOut()
                view.findNavController().navigate(R.id.action_profile_to_loginFragment2)
            }
        }
    }
}