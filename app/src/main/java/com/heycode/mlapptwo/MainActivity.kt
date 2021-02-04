package com.heycode.mlapptwo

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText


class MainActivity : AppCompatActivity() {
    private lateinit var origin: MaterialAutoCompleteTextView
    private lateinit var noOfCylinders: TextInputEditText
    private lateinit var displacement: TextInputEditText
    private lateinit var horsePower: TextInputEditText
    private lateinit var weight: TextInputEditText
    private lateinit var acceleration: TextInputEditText
    private lateinit var modelYear: TextInputEditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        origin = findViewById(R.id.origin)
        noOfCylinders = findViewById(R.id.no_of_cylinders)
        displacement = findViewById(R.id.displacement)
        horsePower = findViewById(R.id.horse_power)
        weight = findViewById(R.id.weight)
        acceleration = findViewById(R.id.acceleration)
        modelYear = findViewById(R.id.model_year)


        val autoCompleteTextAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.origin)
        )
        origin.setAdapter(autoCompleteTextAdapter)

        //clearing the values inside edit text
        findViewById<Button>(R.id.clear_value_button).setOnClickListener {
            noOfCylinders.setText("")
            displacement.setText("")
            horsePower.setText("")
            weight.setText("")
            acceleration.setText("")
            modelYear.setText("")
        }

        findViewById<Button>(R.id.predict_efficiency_button).setOnClickListener {
            if (showError()) {
                return@setOnClickListener
            }
            Snackbar.make(
                findViewById(R.id.rel_lay),
                "${origin.text}\n${noOfCylinders.text}\n${displacement.text}\n${horsePower.text}\n${weight.text}\n${acceleration.text}\n${modelYear.text}",
                Snackbar.LENGTH_LONG
            ).show()

        }
    }

    private fun showError(): Boolean {
        if (noOfCylinders.text.isNullOrEmpty()) {
            noOfCylinders.error = "Required field !"
            return true
        }
        if (displacement.text.isNullOrEmpty()) {
            displacement.error = "Required field !"
            return true
        }
        if (horsePower.text.isNullOrEmpty()) {
            horsePower.error = "Required field !"
            return true
        }
        if (weight.text.isNullOrEmpty()) {
            weight.error = "Required field !"
            return true
        }
        if (acceleration.text.isNullOrEmpty()) {
            acceleration.error = "Required field !"
            return true
        }
        if (modelYear.text.isNullOrEmpty()) {
            modelYear.error = "Required field !"
            return true
        }
        if (origin.text.isNullOrEmpty()) {
            origin.error = "Required field !"
            return true
        }
        return false
    }
}