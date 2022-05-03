package com.romanlojko.beactive

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.romanlojko.beactive.databinding.FragmentRegisterBinding


class RegisterFragment : Fragment() {

    lateinit var binding: FragmentRegisterBinding

    lateinit var mailEditText : EditText
    lateinit var passwordEditText : EditText
    lateinit var passwordCheckEditText : EditText

    lateinit var myAuthorization: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(layoutInflater)

        mailEditText = binding.mailInput!!
        passwordEditText = binding.passwordInput!!
        passwordCheckEditText = binding.passwordInputCheck!!

        myAuthorization = FirebaseAuth.getInstance()

        binding.registerButton.setOnClickListener { view : View ->
            createUser()
        }

        return binding.root;
    }

    private fun createUser() {
        val mail : String = mailEditText?.text.toString()
        val password : String = passwordEditText?.text.toString()
        val passwordCheck : String = passwordCheckEditText?.text.toString()

        // Kontrola ci pouzivatel spravne vyplnil polia
        if (TextUtils.isEmpty(mail)) {
            mailEditText?.setError("E-mail nemôže byť prázdny")
            mailEditText?.requestFocus()
        } else if (TextUtils.isEmpty(password)) {
            passwordEditText?.setError("Heslo nemôže byť prázdne")
            passwordEditText?.requestFocus()
        } else if (TextUtils.isEmpty(passwordCheck)) {
            passwordCheckEditText?.setError("Heslo je potrebné zopakovať")
            passwordCheckEditText?.requestFocus()
        } else {
            // Ak je vsetko vyplene spravne tak sa vytvori user na Firebase Auth
            myAuthorization.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(
                OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            activity,
                            "Registrácia prebehla úspešne",
                            Toast.LENGTH_SHORT
                        ).show()
                        view?.findNavController()?.navigate(R.id.action_registerFragment_to_mainApplication)
                    } else {
                        Toast.makeText(
                            activity,
                            "Pri registrácií nastala chyba " + task.exception?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
        }
    }

}