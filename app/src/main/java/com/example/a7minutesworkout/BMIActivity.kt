package com.example.a7minutesworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.a7minutesworkout.databinding.ActivityBmiBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BMIActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBmiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarBmi)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = getString(R.string.calculate_bmi)
        }

        binding.toolbarBmi.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.btnCalculateUnits.setOnClickListener {
            if (validateMetricUnits()){
                val heightValue : Float = binding.etMetricUnitHeight.text.toString().toFloat() / 100
                val weightValue : Float = binding.etMetricUnitWeight.text.toString().toFloat()

                val bmi = weightValue / (heightValue*heightValue)
                displayBMIResult(bmi)
            }else {
                Toast.makeText(this, getString(R.string.bmi_toast), Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun displayBMIResult(bmi: Float) {

        val bmiLabel : String
        val bmiDesc : String

        if (bmi.compareTo(15f) <= 0) {
            bmiLabel = resources.getStringArray(R.array.bmi_label)[0]
            bmiDesc = resources.getStringArray(R.array.bmi_desc)[0]
        } else if (bmi.compareTo(15f) > 0 && bmi.compareTo(16f) <= 0) {
            bmiLabel = resources.getStringArray(R.array.bmi_label)[1]
            bmiDesc = resources.getStringArray(R.array.bmi_desc)[0]
        } else if (bmi.compareTo(16f) > 0 && bmi.compareTo(18.5f) <= 0) {
            bmiLabel = resources.getStringArray(R.array.bmi_label)[2]
            bmiDesc = resources.getStringArray(R.array.bmi_desc)[0]
        } else if (bmi.compareTo(18.5f) > 0 && bmi.compareTo(25f) <= 0) {
            bmiLabel = resources.getStringArray(R.array.bmi_label)[3]
            bmiDesc = resources.getStringArray(R.array.bmi_desc)[1]
        } else if (bmi.compareTo(25f) > 0 && bmi.compareTo(30f) <= 0) {
            bmiLabel = resources.getStringArray(R.array.bmi_label)[4]
            bmiDesc = resources.getStringArray(R.array.bmi_desc)[2]
        } else if (bmi.compareTo(30f) > 0 && bmi.compareTo(35f) <= 0) {
            bmiLabel = resources.getStringArray(R.array.bmi_label)[5]
            bmiDesc = resources.getStringArray(R.array.bmi_desc)[2]
        } else if (bmi.compareTo(35f) > 0 && bmi.compareTo(40f) <= 0) {
            bmiLabel = resources.getStringArray(R.array.bmi_label)[6]
            bmiDesc = resources.getStringArray(R.array.bmi_desc)[3]
        } else {
            bmiLabel = resources.getStringArray(R.array.bmi_label)[7]
            bmiDesc = resources.getStringArray(R.array.bmi_desc)[3]
        }

        val bmiValue = BigDecimal(bmi.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toString()

        binding.apply {
            llDisplayBMIResult.visibility = View.VISIBLE
            tvBMIValue.text = bmiValue
            tvBMITYpe.text = bmiLabel
            tvBMIDesc.text = bmiDesc
        }

    }

    private fun validateMetricUnits(): Boolean{
        var isValid = true

        if (binding.etMetricUnitWeight.toString().isEmpty()){
            isValid = false
        } else if (binding.etMetricUnitHeight.toString().isEmpty()){
            isValid = false
        }
        return isValid
    }
}