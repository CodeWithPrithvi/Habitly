package com.example.habitly



import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HabitPrefs(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("HabitPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val key = "habit_list"

    fun saveHabits(habits: MutableList<Habit>) {
        val json = gson.toJson(habits)
        sharedPreferences.edit().putString(key, json).apply()
    }

    fun loadHabits(): MutableList<Habit> {
        val json = sharedPreferences.getString(key, null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<Habit>>() {}.type
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }
    }
}
