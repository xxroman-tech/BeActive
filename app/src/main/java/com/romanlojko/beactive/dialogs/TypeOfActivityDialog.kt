package com.romanlojko.beactive.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.romanlojko.beactive.Objects.DataHolder
import com.romanlojko.beactive.R
import kotlinx.android.synthetic.main.typeofactivity_dialog.*
import kotlinx.android.synthetic.main.typeofactivity_dialog.view.*

/**
 * Trieda zdedena od DialogFragmentu
 * Zobrazuje Dialog so vstupnym oknom kam sa zadava typ aktivity pred finalym
 * ulozenim do firebase DB
 * @author Roman Lojko
 */
class TypeOfActivityDialog: DialogFragment() {

    //Firebase
    private val myAuthorization: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var dbRef: DatabaseReference

    /**
     * Nastavi layout
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.typeofactivity_dialog, container, false)
    }

    /**
     * Z lefecycle, ak je view created tak nastavi button listeners
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonSaveActivity.setOnClickListener {
            DataHolder.setTypeOfActivity(inputOnSetTypeOfAct.text.toString())
            pushToFirebase()
            findNavController().navigate(R.id.action_typeOfActivityDialog2_to_mainApplication)
        }
    }

    /**
     * Metoda kotra pushne novu aktivitu do firebase
     */
    private fun pushToFirebase() {

        dbRef = FirebaseDatabase.getInstance("https://vamzapp-5939a-default-rtdb.europe-west1.firebasedatabase.app").getReference("/activityData/" + myAuthorization.currentUser?.uid + "/${DataHolder.getDate()}")

        // Zistim pocet potomkov aby som vedel kam vkladat
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                DataHolder.setNumberOfActivity(snapshot.childrenCount)
            }

            override fun onCancelled(error: DatabaseError) { }
        })

        dbRef = FirebaseDatabase.getInstance("https://vamzapp-5939a-default-rtdb.europe-west1.firebasedatabase.app").getReference("/activityData/" + myAuthorization.currentUser?.uid + "/${DataHolder.getDate()}" + "/${DataHolder.getNumberOfActivity()}")

        var list: HashMap<String, Any> = HashMap()

        list.put("caloriesBurned", DataHolder.getCaloriesBurned())
        list.put("timeOfActivity", DataHolder.getTimeOfActivity())
        list.put("totalSteps", DataHolder.getTotalSteps())
        list.put("typeOfActivity", DataHolder.getTypeOfActivity())

        dbRef.updateChildren(list as Map<String, Any>)
    }
}