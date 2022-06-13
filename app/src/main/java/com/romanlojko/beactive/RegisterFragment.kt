package com.romanlojko.beactive

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.romanlojko.beactive.Objects.Person
import com.romanlojko.beactive.databinding.FragmentRegisterBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_register.*

/**
 * Trieda Register fragment ktora dedi od triedy Fragment predstavuje samotne okno registracie
 * noveho uzivatela s jej funkcionalitami
 * @author Roman Lojko
 */
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val myAuthorization: FirebaseAuth = FirebaseAuth.getInstance()
    private var registrovany = false
    private var nechceSaRegistrovat = false

    /**
     * Lyfecycle metoda, zavola sa vzdy pri otvoreni fragmentu
     * a naicializuje objekty v nom
     * @return binding.root
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(layoutInflater)

        binding.registerButton.setOnClickListener { view : View ->
            createUser()
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                nechceSaRegistrovat = true
                NavHostFragment.findNavController(myNavHostFragment).navigate(R.id.action_registerFragment_to_loginFragment2)
            }
        })

        return binding.root;
    }

    /**
     * Nacita data zo sharedPreferences pri orentation change
     */
    override fun onStart() {
        super.onStart()
        loadRegisterInputData()
    }

    /**
     * Ulozi data z inputov pre orientation change
     */
    override fun onPause() {
        super.onPause()
        if (registrovany || nechceSaRegistrovat)
            deleteData()
        else
            saveRegisteInputData()
    }

    /**
     * Metoda ktora nacita data z input boxov a pokusi sa
     * prihlasit uzivatela do firebase, ak sa podari, tak je uzivatel
     * presmerovany na profile fragment kde vyplni potrebne udaje
     */
    private fun createUser() {
        val mail : String = binding.regMailInput.text.toString()
        val password : String = binding.regPasswordInput.text.toString()
        val passwordCheck : String = binding.regPasswordInputCheck.text.toString()

        // Kontrola ci pouzivatel spravne vyplnil polia
        if (TextUtils.isEmpty(binding.regNameInput.text)) {
            binding.regNameInput.setError("Meno nemôže byť prázdne")
            binding.regNameInput.requestFocus()
        } else if (TextUtils.isEmpty(binding.regSurnameInput.text)) {
            binding.regSurnameInput.setError("Prezvisko nemôže byť prázdne")
            binding.regSurnameInput.requestFocus()
        } else if (TextUtils.isEmpty(mail)) {
            binding.regMailInput.setError("E-mail nemôže byť prázdny")
            binding.regMailInput.requestFocus()
        } else if (TextUtils.isEmpty(password)) {
            binding.regPasswordInput.setError("Heslo nemôže byť prázdne")
            binding.regPasswordInput.requestFocus()
        } else if (TextUtils.isEmpty(passwordCheck)) {
            binding.regPasswordInputCheck.setError("Heslo je potrebné zopakovať")
            binding.regPasswordInputCheck.requestFocus()
        } else {
            // Ak je vsetko vyplene spravne tak sa vytvori user na Firebase Auth
            myAuthorization.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(
                OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
                        registrovany = true
                        Person.setName((binding.regNameInput.text).toString())
                        Person.setSurname((binding.regSurnameInput.text).toString())
                        Toast.makeText(
                            activity,
                            "Registrácia prebehla úspešne, prosím vyplnte základné údaje",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Najprv sa presmeruje na profile kde je potrebne vyplnit udaje
                        view?.findNavController()?.navigate(R.id.action_registerFragment_to_profile)
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

    /**
     * Metoda uklada data do sharedPreferences pre zachovanie v pamati
     */
    private fun saveRegisteInputData() {
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("myPrefRegister", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("regName", binding.regNameInput.text.toString())
        editor.putString("regSurname", binding.regSurnameInput.text.toString())
        editor.putString("regMail", binding.regMailInput.text.toString())
        editor.putString("regPass", binding.regPasswordInput.text.toString())
        editor.putString("regPassCheck", binding.regPasswordInputCheck.text.toString())
        editor.apply()
    }

    /**
     * Nacitavanie dat zo sharedPreferences
     */
    private fun loadRegisterInputData() {
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("myPrefRegister", Context.MODE_PRIVATE)
        binding.regNameInput.setText(sharedPreferences.getString("regName", ""))
        binding.regSurnameInput.setText(sharedPreferences.getString("regSurname", ""))
        binding.regMailInput.setText(sharedPreferences.getString("regMail", ""))
        binding.regPasswordInput.setText(sharedPreferences.getString("regPass", ""))
        binding.regPasswordInputCheck.setText(sharedPreferences.getString("regPassCheck", ""))
    }

    /**
     * Zmaze data zo sharedPref ak ich uz nepotrebujeme
     */
    private fun deleteData() {
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("myPrefRegister", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }

}