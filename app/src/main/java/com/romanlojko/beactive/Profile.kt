package com.romanlojko.beactive

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.romanlojko.beactive.Objects.Person
import com.romanlojko.beactive.databinding.FragmentLoginBinding
import com.romanlojko.beactive.databinding.FragmentProfileBinding

class Profile : Fragment() {

    lateinit var binding: FragmentProfileBinding

    lateinit var myAuthorization: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        myAuthorization = FirebaseAuth.getInstance()

        initButtonListeners()

        initData()

        return binding.root
    }

    /**
     * Pridanie dat na obrazovku z PersonDataLoader
     */
    private fun initData() {
        binding.textViewMenoPreizvisko.text = Person.getName() + " " + Person.getSurname()
        binding.editTextWeight.setText(Person.getWeight().toString(), TextView.BufferType.EDITABLE)
        binding.editTextHeight.setText(Person.getHeight().toString(), TextView.BufferType.EDITABLE)
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