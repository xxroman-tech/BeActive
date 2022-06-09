package com.romanlojko.beactive.Objects

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object Person {

    private var myAuthorization: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var userId: FirebaseUser
    private lateinit var name: String
    private lateinit var surname: String
    private var height: Double = 0.0
    private var weight: Double = 0.0
    private lateinit var sex: Sex
    private lateinit var listOfActivities: ArrayList<UserActivity>

    init {
        if (myAuthorization.currentUser != null) {
            userId = myAuthorization.currentUser!!
        }
    }

    fun getActivites(): ArrayList<UserActivity> {
        return listOfActivities
    }

    fun getName(): String {
        return name
    }

    fun getSurname(): String {
        return surname
    }

    fun getHeight(): Double {
        return height
    }

    fun getWeight(): Double {
        return weight
    }

    fun getSex(): Sex {
        return sex
    }

    fun setName(pName: String) {
        name = pName
    }

    fun setSurname(pSurname: String) {
        surname = pSurname
    }

    fun setHeight(pHeight: Double) {
        height = pHeight
    }

    fun setWeight(pWeight: Double) {
        weight = pWeight
    }

    fun setSex(pSex: Sex) {
        sex = pSex
    }
}