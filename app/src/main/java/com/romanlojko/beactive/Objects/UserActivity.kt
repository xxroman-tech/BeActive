package com.romanlojko.beactive.Objects

class UserActivity {

    private var date: String = ""
    private var caloriesBurned: Int = 0
    private var timeOfActivity: Int = 0
    private var totalSteps: Int = 0
    private var typeOfActivity: String = ""

    fun getDate(): String {
        return date
    }

    fun getCaloriesBurned(): Int {
        return caloriesBurned
    }

    fun getTimeOfActivity(): Int {
        return timeOfActivity
    }

    fun getTotalSteps(): Int {
        return totalSteps
    }

    fun getTypeOfActivity(): String {
        return typeOfActivity
    }

    fun setDate(pDate: String) {
        date = pDate
    }

    fun setCaloriesBurned(pCaloriesBurned: Int) {
        caloriesBurned = pCaloriesBurned
    }

    fun setTimeOfActivity(pTimeOfActivity: Int) {
        timeOfActivity = pTimeOfActivity
    }

    fun setTotalSteps(pTotalSteps: Int) {
        totalSteps = pTotalSteps
    }

    fun setTypeOfActivity(pTypeOfActivity: String) {
        typeOfActivity = pTypeOfActivity
    }
}