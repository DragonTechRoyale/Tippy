package com.dtr4k.tippy

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.AbsSeekBar
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.time.temporal.TemporalAmount

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PRECENT = 15

class MainActivity : AppCompatActivity() {
    private lateinit var etBaseAmount: EditText
    private lateinit var seekBarTip: SeekBar
    private lateinit var tvTipPresent: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipDescription: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etBaseAmount = findViewById(R.id.etBaseAmount)
        seekBarTip = findViewById<SeekBar>(R.id.seekBarTip)
        tvTipPresent = findViewById(R.id.tvTipPresent)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipDescription = findViewById(R.id.tvTipDescription)

        seekBarTip.progress = INITIAL_TIP_PRECENT
        tvTipPresent.text = "${seekBarTip.progress}%"

        tvTipAmount.text = "0"
        tvTotalAmount.text = "0"
        updateTipDescription(seekBarTip.progress)

        seekBarTip.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                Log.i(TAG, "onProgressChanged ${seekBarTip.progress}")
                tvTipPresent.text = "${seekBarTip.progress}%"
                computeTipAndTotal()
                updateTipDescription(seekBarTip.progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        etBaseAmount.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                Log.i(TAG, "afterTextChanged ${etBaseAmount.text}")
                computeTipAndTotal()
            }
        })
    }

    private fun updateTipDescription(tipPrecent: Int) {
        val tipDescription = when (tipPrecent) {
            in 0..9 -> "Restorant sucks?"
            in 10..19 -> "Nice"
            in 20..29 -> "Looks like you're coming here again"
            else -> "Someone likes the waitress ( ͡° ͜ʖ ͡°)"
        }
        tvTipDescription.text = tipDescription

        val color = ArgbEvaluator().evaluate(
           tipPrecent.toFloat() / seekBarTip.max,
            ContextCompat.getColor(this, R.color.color_worst_tip),
            ContextCompat.getColor(this, R.color.color_best_tip)
        ) as Int

        tvTipDescription.setTextColor(color)
    }

    private fun computeTipAndTotal() {
        if (etBaseAmount.text.isEmpty())
        {
            tvTipAmount.text = "0"
            tvTotalAmount.text = "0"
            return
        }
        // Get: Bill Amount and Tip %
        val baseAmount = etBaseAmount.text.toString().toDouble()
        val tipPrecent  = seekBarTip.progress

        // Compute
        var tipAmount = baseAmount * tipPrecent / 100
        var totalAmount = baseAmount + tipAmount

        // Update the UI of: the tip amount and total
        tvTipAmount.text = "%.2f".format(tipAmount)
        tvTotalAmount.text = "%.2f".format(totalAmount)
    }
}