package com.example.habitly

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox

class CustomAdapter(
    private val habitList: MutableList<Habit>,
    private val onCheckedChange: (Habit) -> Unit
) : RecyclerView.Adapter<CustomAdapter.HabitViewHolder>() {

    inner class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val habitCheckbox: MaterialCheckBox = itemView.findViewById(R.id.habbitcheckbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.habbit_layout, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habitList[position]
        holder.habitCheckbox.setOnCheckedChangeListener(null) // clear listener to avoid issues
        holder.habitCheckbox.text = habit.name
        holder.habitCheckbox.isChecked = habit.isChecked

        holder.habitCheckbox.setOnCheckedChangeListener { _, isChecked ->
            habit.isChecked = isChecked
            onCheckedChange(habit)
        }
    }

    override fun getItemCount(): Int = habitList.size

    fun addHabit(habit: Habit) {
        habitList.add(habit)
        notifyItemInserted(habitList.size - 1)
    }

    fun removeHabit(position: Int) {
        if (position in habitList.indices) {
            habitList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}

