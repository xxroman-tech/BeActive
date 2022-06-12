package com.romanlojko.beactive.Objects

/**
 * DataHolder object sa pouziva pri zaznamenavani novej aktivity
 */
object DataHolder {
    private var date: String = ""
    private var caloriesBurned: String = ""
    private var timeOfActivity: Int = 0
    private var totalSteps: Int = 0
    private var typeOfActivity: String = ""
    private var numberOfActivity: Int = 0

    fun getDate(): String {
        return date
    }

    fun getNumberOfActivity(): Int {
        return numberOfActivity
    }

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

    fun setDate(pDate: String) {
        date = pDate
    }

    fun incNumberOfActivity() {
        numberOfActivity ++
    }

    fun resetNumberOfActivity() {
        numberOfActivity = 0
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

    /**
     * Zmaze data v objecte
     */
    fun deleteData() {
        date = ""
        totalSteps = 0
        caloriesBurned = ""
        timeOfActivity = 0
        typeOfActivity = ""
        numberOfActivity = 0
    }
}