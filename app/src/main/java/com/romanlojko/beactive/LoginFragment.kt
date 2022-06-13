package com.romanlojko.beactive

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
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
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.romanlojko.beactive.Objects.Person
import com.romanlojko.beactive.Objects.PersonDataLoader
import com.romanlojko.beactive.Objects.UserActivity
import com.romanlojko.beactive.databinding.FragmentLoginBinding
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Trieda Login fragment ktora dedi od triedy Fragment predstavuje samotne okno loginu
 * s jej funkcionalitami
 * @author Roman Lojko
 */
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val myAuthorization = FirebaseAuth.getInstance()
    private var prihlaseny = false

    /**
     * Lyfecycle metoda, zavola sa vzdy pri otvoreni fragmentu
     * a naicializuje objekty v nom
     * @return binding.root
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(layoutInflater)

        binding.loginButton.setOnClickListener {
            loginUser()
        }

        binding.registerLink.setOnClickListener {
            NavHostFragment.findNavController(myNavHostFragment).navigate(R.id.action_loginFragment2_to_registerFragment)
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { }
        })

        return binding.root
    }

    /**
     * Nacita data zo sharedPreferences pri orentation change
     */
    override fun onStart() {
        super.onStart()
        loadLoginInputData()
    }

    /**
     * Ulozi data z inputov pre orientation change
     */
    override fun onPause() {
        super.onPause()
        if (prihlaseny)
            deleteData()
        else
            saveLoginInputData()
    }

    /**
     * Metoda nacita data z inputov a nasledne sa pokusi prihlasit
     * ak je prihlasenie uspesne, tak sa zobrazi mainAplicationFragment
     */
    private fun loginUser() {
        val mail : String = binding.mailInput.text.toString()
        val password : String = binding.passwordInput.text.toString()

        // Kontrola ci pouzivatel spravne vyplnil polia
        if (TextUtils.isEmpty(mail)) {
            binding.mailInput.setError("E-mail nemôže byť prázdny")
            binding.mailInput.requestFocus()
        } else if (TextUtils.isEmpty(password)) {
            binding.passwordInput.setError("Heslo nemôže byť prázdne")
            binding.passwordInput.requestFocus()
        } else {
            myAuthorization.signInWithEmailAndPassword(mail, password).addOnCompleteListener(
                OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
                        prihlaseny = true
                        Toast.makeText(
                            activity,
                            "Prihlásenie úspešné",
                            Toast.LENGTH_SHORT
                        ).show()
                        // TODO: Ak je prvy krat registrovany
                        // PersonDataLoader.loadDataToPerson()
                        NavHostFragment.findNavController(myNavHostFragment).navigate(R.id.action_loginFragment2_to_mainApplication)
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

    /**
     * Metoda uklada data do sharedPreferences pre zachovanie v pamati
     */
    private fun saveLoginInputData() {
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("myPrefLogin", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("loginMail", binding.mailInput.text.toString())
        editor.putString("loginPass", binding.passwordInput.text.toString())
        editor.apply()
    }

    /**
     * Nacitavanie dat zo sharedPreferences
     */
    private fun loadLoginInputData() {
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("myPrefLogin", Context.MODE_PRIVATE)
        binding.mailInput.setText(sharedPreferences.getString("loginMail", ""))
        binding.passwordInput.setText(sharedPreferences.getString("loginPass", ""))
    }

    /**
     * Zmaze data zo sharedPref ak ich uz nepotrebujeme
     */
    private fun deleteData() {
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("myPrefLogin", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }

}