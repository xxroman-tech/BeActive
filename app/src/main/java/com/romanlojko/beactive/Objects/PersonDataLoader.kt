package com.romanlojko.beactive.Objects

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

/**
 * Trieda sluzi na nacitane profilovych dat z firebasedatabase do objectu person
 */
object PersonDataLoader {

    private lateinit var dbRef: DatabaseReference
    private var myAuthorization: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Metoda ktora nacita uzivatelske udaje do Objectu Person
     * Singleton si uchovava vahu, vysku, meno a tak
     */
    fun loadDataToPerson() {
        dbRef = FirebaseDatabase.getInstance("https://vamzapp-5939a-default-rtdb.europe-west1.firebasedatabase.app").getReference("/personData/" + myAuthorization.currentUser?.uid + "/data")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Ideme prechadzat Realtime databazu
                if(snapshot.exists()) {

                    val data: Person? = snapshot.getValue(Person::class.java)

                    if (data != null) {
                        initPerson(data)
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })
    }

    /**
     * Pridauje hodnoty z DB do atributov v objecte person
     */
    private fun initPerson(data: Person) {
        Person.setName(data.getName())
        Person.setSurname(data.getSurname())
        Person.setWeight(data.getWeight())
        Person.setHeight(data.getHeight())
        Person.setSex(data.getSex())
    }
}