package com.romanlojko.beactive

import android.content.ContentValues
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.romanlojko.beactive.Objects.Person
import com.romanlojko.beactive.Objects.PersonDataLoader
import com.romanlojko.beactive.Objects.UserActivity
import com.romanlojko.beactive.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private lateinit var mailEditText : EditText
    private lateinit var passwordEditText : EditText

    private lateinit var myAuthorization: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(layoutInflater)

        mailEditText = binding.mailInput!!
        passwordEditText = binding.passwordInput!!

        myAuthorization = FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener { view : View ->
            loginUser()
        }

        binding.registerLink.setOnClickListener { view : View ->
            view.findNavController().navigate(R.id.action_loginFragment2_to_registerFragment)
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { }
        })

        return binding.root
    }

    private fun loginUser() {
        val mail : String = mailEditText?.text.toString()
        val password : String = passwordEditText?.text.toString()

        // Kontrola ci pouzivatel spravne vyplnil polia
        if (TextUtils.isEmpty(mail)) {
            mailEditText?.setError("E-mail nemôže byť prázdny")
            mailEditText?.requestFocus()
        } else if (TextUtils.isEmpty(password)) {
            passwordEditText?.setError("Heslo nemôže byť prázdne")
            passwordEditText?.requestFocus()
        } else {
            myAuthorization.signInWithEmailAndPassword(mail, password).addOnCompleteListener(
                OnCompleteListener<AuthResult?> { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        activity,
                        "Prihlásenie úspešné",
                        Toast.LENGTH_SHORT
                    ).show()
                    // TODO: Ak je prvy krat registrovany
//                    PersonDataLoader.loadDataToPerson()
                    view?.findNavController()?.navigate(R.id.action_loginFragment2_to_mainApplication)
                } else {
                    Toast.makeText(
                        activity,
                        "Prihlásenie sa nepodarilo" + task.exception?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

}