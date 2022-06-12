package com.romanlojko.beactive

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.romanlojko.beactive.Objects.Person
import com.romanlojko.beactive.Objects.PersonDataLoader
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
        binding.editTextAge.setText(Person.getAge().toString(),TextView.BufferType.EDITABLE)

        calcAndShowBMI()
        zaklikniSex()
    }

    /**
     * Metoda ktora vypocita a zobrazi BMI
     */
    private fun calcAndShowBMI() {
        var bmi = ""

        bmi = (Person.getWeight() / ((Person.getHeight()/ 100) * (Person.getHeight()/ 100))).toString()

        binding.textViewBMI.text = bmi

        showBMIDescp(bmi.toDouble())
    }

    /**
     * Metoda zobrazi opis bmi (obezita, normalna a tak)
     */
    private fun showBMIDescp(pBmi: Double) {
        if (pBmi < 16.5)
            binding.BMIOpis.text = "Ťažká podváha"
        else if (pBmi > 16.5  && pBmi < 18.5)
            binding.BMIOpis.text = "Podváha"
        else if (pBmi > 18.5 && pBmi < 25)
            binding.BMIOpis.text = "Ideálna(zdravá) váha"
        else if (pBmi > 25 && pBmi < 30)
            binding.BMIOpis.text = "Ľahká nadváha"
        else if (pBmi > 30 && pBmi < 35)
            binding.BMIOpis.text = "Stredná nadváha "
        else if (pBmi > 35 && pBmi < 40)
            binding.BMIOpis.text = "Vyššia nadváha"
        else if (pBmi > 40)
            binding.BMIOpis.text = "Veľmi vysoká nadváha"
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

        binding.buttonSave.setOnClickListener {
            if (myAuthorization!=null && myAuthorization.currentUser != null) {
                nacitajDataZFragmentu()
                updateDataOnFirebase()
            }
        }

    }

    /**
     * Ulozi nove data z profile fragmentu do Person Objectu pre
     * odoslanie do DB
     */
    private fun nacitajDataZFragmentu() {
        Person.setWeight((binding.editTextWeight.text).toString().toDouble())
        Person.setHeight((binding.editTextHeight.text).toString().toDouble())
        Person.setAge((binding.editTextAge.text).toString().toInt())
        ulozSex()
    }

    /**
     * Metoda zisti ktory radio button je zakliknuty a nasledne ulozi data
     * do Person obj
     */
    private fun ulozSex() {
        if (binding.radioMale.isChecked)
            Person.setSex("male")
        else if (binding.radioFemale.isChecked)
            Person.setSex("female")
    }

    /**
     * Zaklikne ci je person male alebo female
     */
    private fun zaklikniSex() {
        if (Person.getSex().equals("male"))
            binding.radioMale.isChecked = true
        else if (Person.getSex().equals("female"))
            binding.radioFemale.isChecked = true
    }

    /**
     * Metoda ktora sluzi na prepisanie dat uzivatela
     */
    private fun updateDataOnFirebase() {
        val dbRef: DatabaseReference
        val myAuthorization: FirebaseAuth = FirebaseAuth.getInstance()


        dbRef = FirebaseDatabase.getInstance("https://vamzapp-5939a-default-rtdb.europe-west1.firebasedatabase.app").getReference("/personData/" + myAuthorization.currentUser?.uid + "/data")

        dbRef.setValue(Person).addOnCompleteListener { task ->
            if (task.isComplete) {

                Toast.makeText(
                    activity,
                    "Dáta sa podarilo aktializovať",
                    Toast.LENGTH_SHORT
                ).show()

                // UpdateUI
                binding.editTextWeight.onEditorAction(EditorInfo.IME_ACTION_DONE)
                binding.editTextHeight.onEditorAction(EditorInfo.IME_ACTION_DONE)
                binding.editTextAge.onEditorAction(EditorInfo.IME_ACTION_DONE)
                calcAndShowBMI()
                zaklikniSex()

                Log.d(
                    ContentValues.TAG,
                    "Upload prebehol uspesne - " + Person.getName() + Person.getName() + Person.getAge()
                )
            } else {
                Toast.makeText(
                    activity,
                    "Pri aktualizovaní nastala chyba",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}