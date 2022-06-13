package com.romanlojko.beactive.Objects

/**
 * Trieda predstavuje aktivitu ktora sa vlozi do listu
 * pri nacitani z DB a nasledne zobrazi v recyclerView
 * @author Roman Lojko
 */
class UserActivity {

    private var caloriesBurned: String = ""
    private var timeOfActivity: Int = 0
    private var totalSteps: Int = 0
    private var typeOfActivity: String = ""

    fun getCaloriesBurned(): String {
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

    fun setCaloriesBurned(pCaloriesBurned: String) {
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