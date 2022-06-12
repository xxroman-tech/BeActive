package com.romanlojko.beactive.Objects

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * Object predstavuje osobu ktora je prihlasena v aplikacii a docasne si uchovava
 * data nacitane z firebase o uzivatelovi ako, meno, sex, age...
 */
object Person {

    private var name: String = ""
    private var surname: String = ""
    private var height: Double = 0.0
    private var weight: Double = 0.0
    private var sex: String = ""
    private var age: Int = 0

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

    fun getSex(): String {
        return sex
    }

    fun getAge(): Int {
        return age
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

    fun setSex(pSex: String) {
//        if (pSex.equals("male")) {
//            sex = Sex.MALE
//        } else if (pSex.equals("female")) {
//            sex = Sex.FEMALE
//        }
        sex = pSex
    }

    fun setAge(pVek: Int) {
        age = pVek
    }
}