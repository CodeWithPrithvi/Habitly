package com.example.habitly

import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {
    private lateinit var habitPrefs: HabitPrefs
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CustomAdapter
    private lateinit var habitInput: TextInputEditText
    private lateinit var textInputLayout: TextInputLayout
    private val habitList = mutableListOf<Habit>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        habitPrefs = HabitPrefs(this)
        habitList.addAll(habitPrefs.loadHabits())

        adapter = CustomAdapter(habitList) { updatedHabit ->
            habitPrefs.saveHabits(habitList)
        }
        recyclerView = findViewById(R.id.recyclerView)
        habitInput = findViewById(R.id.newHabbit)
        textInputLayout = findViewById(R.id.textInputLayout)

        adapter = CustomAdapter(habitList) { habit ->
            Log.d("HabitChecked", "${habit.name}: ${habit.isChecked}")
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        textInputLayout.setEndIconOnClickListener {
            val inputText = habitInput.text.toString().trim()
            if (inputText.isNotEmpty()) {
                val newHabit = Habit(inputText, false)
                adapter.addHabit(newHabit)
                habitPrefs.saveHabits(habitList)
                habitInput.text?.clear()
            } else {
                Toast.makeText(this, "Please enter a habit", Toast.LENGTH_SHORT).show()
            }
        }
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val deletedItem = habitList[position]
                habitList.removeAt(position)
                habitPrefs.saveHabits(habitList)
                adapter.notifyItemRemoved(position)

                Snackbar.make(recyclerView, "Habit deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        habitList.add(position, deletedItem)
                        habitPrefs.saveHabits(habitList)
                        adapter.notifyItemInserted(position)
                    }
                    .setActionTextColor(ContextCompat.getColor(this@MainActivity, R.color.light))
                    .show()
            }
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView
                    val paint = Paint()

                    if (dX > 0) {
                        // Swiping to the right
                        paint.color = ContextCompat.getColor(recyclerView.context, R.color.delete_red) // or any color you want
                        c.drawRect(
                            itemView.left.toFloat(),
                            itemView.top.toFloat(),
                            itemView.left.toFloat() + dX,
                            itemView.bottom.toFloat(),
                            paint
                        )

                        // Draw your right swipe icon here
                        val doneIcon = ContextCompat.getDrawable(recyclerView.context, R.drawable.baseline_delete_24) // your done icon drawable
                        doneIcon?.let {
                            val iconMargin = (itemView.height - it.intrinsicHeight) / 2
                            val iconTop = itemView.top + iconMargin
                            val iconLeft = itemView.left + iconMargin
                            val iconRight = iconLeft + it.intrinsicWidth
                            val iconBottom = iconTop + it.intrinsicHeight

                            it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                            it.draw(c)
                        }
                    } else if (dX < 0) {
                        // Swiping to the left
                        paint.color = ContextCompat.getColor(recyclerView.context, R.color.delete_red)
                        c.drawRect(
                            itemView.right.toFloat() + dX,
                            itemView.top.toFloat(),
                            itemView.right.toFloat(),
                            itemView.bottom.toFloat(),
                            paint
                        )

                        val deleteIcon = ContextCompat.getDrawable(recyclerView.context, R.drawable.baseline_delete_24)
                        deleteIcon?.let {
                            val iconMargin = (itemView.height - it.intrinsicHeight) / 2
                            val iconTop = itemView.top + iconMargin
                            val iconRight = itemView.right - iconMargin
                            val iconLeft = iconRight - it.intrinsicWidth
                            val iconBottom = iconTop + it.intrinsicHeight

                            it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                            it.draw(c)
                        }
                    }
                }
            }


        }


        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)


    }
}