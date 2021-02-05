package com.heycode.mlapptwo

import android.os.Bundle
import android.os.Handler
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.FileChannel


class MainActivity : AppCompatActivity() {

    var mean = floatArrayOf(
        5.477707f,
        195.318471f,
        104.869427f,
        2990.251592f,
        15.559236f,
        75.898089f,
        0.624204f,
        0.178344f,
        0.197452f
    )
    var std = floatArrayOf(
        1.699788f,
        104.331589f,
        38.096214f,
        843.898596f,
        2.789230f,
        3.675642f,
        0.485101f,
        0.383413f,
        0.398712f
    )


    private lateinit var interpreter: Interpreter

    private lateinit var origin: MaterialAutoCompleteTextView
    private lateinit var noOfCylinders: TextInputEditText
    private lateinit var displacement: TextInputEditText
    private lateinit var horsePower: TextInputEditText
    private lateinit var weight: TextInputEditText
    private lateinit var acceleration: TextInputEditText
    private lateinit var modelYear: TextInputEditText
    private lateinit var progressBar: ProgressBar
    private lateinit var result: TextView

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
        progressBar = findViewById(R.id.progress_bar)
        result = findViewById(R.id.result_text_view)


        try {
            interpreter = Interpreter(loadModelFile(), null)

        } catch (e: Exception) {
            println(e.stackTrace)
        }

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
            progressBar.visibility = ProgressBar.VISIBLE
            val floats = Array(1) {
                FloatArray(
                    9
                )
            }
            floats[0][0] = (noOfCylinders.text.toString().toFloat() - mean[0]) / std[0]
            floats[0][1] = (displacement.text.toString().toFloat() - mean[1]) / std[1]
            floats[0][2] = (horsePower.text.toString().toFloat() - mean[2]) / std[2]
            floats[0][3] = (weight.text.toString().toFloat() - mean[3]) / std[3]
            floats[0][4] = (acceleration.text.toString().toFloat() - mean[4]) / std[4]
            floats[0][5] = (modelYear.text.toString().toFloat() - mean[5]) / std[5]

            when (autoCompleteTextAdapter.getPosition(origin.text.toString())) {
                0 -> {
                    floats[0][6] = (1 - mean[6]) / std[6]
                    floats[0][7] = (0 - mean[7]) / std[7]
                    floats[0][8] = (0 - mean[8]) / std[8]
                }
                1 -> {
                    floats[0][6] = (0 - mean[6]) / std[6]
                    floats[0][7] = (1 - mean[7]) / std[7]
                    floats[0][8] = (0 - mean[8]) / std[8]

                }
                2 -> {
                    floats[0][6] = (0 - mean[6]) / std[6]
                    floats[0][7] = (0 - mean[7]) / std[7]
                    floats[0][8] = (1 - mean[8]) / std[8]
                }
            }
            val handler = Handler()
            handler.postDelayed(Runnable {
                progressBar.visibility = ProgressBar.GONE
                val res = "Result: " + doInference(floats)
                result.text = res
            }, 3000)

//            result.text = "${origin.text} ${noOfCylinders.text}\n${displacement.text} ${horsePower.text}\n${weight.text} ${acceleration.text}\n${modelYear.text}"
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

    private fun doInference(input: Array<FloatArray>): Float {
        val output = Array(1) {
            FloatArray(1)
        }
        interpreter.run(input, output)
        return output[0][0]
    }


    @Throws(IOException::class)
    private fun loadModelFile(): ByteBuffer {
        val assetFileDescriptor = this.assets.openFd("automobile.tflite")
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = fileInputStream.getChannel()
        val startOffset = assetFileDescriptor.startOffset
        val length = assetFileDescriptor.length
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, length)
    }
}