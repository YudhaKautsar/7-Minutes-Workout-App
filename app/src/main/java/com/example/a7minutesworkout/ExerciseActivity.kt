package com.example.a7minutesworkout

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7minutesworkout.databinding.ActivityExerciseBinding
import com.example.a7minutesworkout.databinding.DialogCustomBackConfirmationBinding
import java.util.*

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    companion object {
        private val TAG = ExerciseActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityExerciseBinding
    private lateinit var restTimer : CountDownTimer
    private var restProgress = 0
    private var restTimerDuration = 1

    private lateinit var exerciseTimer : CountDownTimer
    private var exerciseProgress = 0
    private var exerciseTimerDuration = 1

    private lateinit var exerciseList: ArrayList<ExerciseModel>
    private var currentExercisePosition = -1

    private lateinit var tts: TextToSpeech
    private lateinit var player: MediaPlayer

    private lateinit var exerciseStatusAdapter: ExerciseStatusAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarExercise)

        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        binding.toolbarExercise.setNavigationOnClickListener {
            customDialogForBackButton()
        }

        exerciseList = Constants.defaultExerciseList()

        tts = TextToSpeech(this, this)

//        val colors = arrayOf(
//            Color.parseColor("#FFFFFF"),
//            Color.parseColor("#000000"),
//            Color.parseColor("#FF8F00"),
//            Color.parseColor("#EF6C00"),
//            Color.parseColor("#D84315"),
//            Color.parseColor("#37474F"))

//        val randomColor = colors.random()

        binding.progressBarExercise.progressDrawable.mutate().setColorFilter(
            Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN
        )

        setupRestView()
        setupExerciseAdapter()
    }

    override fun onBackPressed() {
        customDialogForBackButton()
//        super.onBackPressed()
    }

    private fun setupExerciseAdapter(){
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.apply {
            rvExerciseStatus.layoutManager = linearLayoutManager
            exerciseStatusAdapter = ExerciseStatusAdapter(exerciseList)
            rvExerciseStatus.adapter = exerciseStatusAdapter
        }
    }

    private fun setupRestView(){

        try {
            val soundUri = Uri.parse(
                "android.resource://com.example.a7minutesworkout/" + R.raw.app_src_main_res_raw_press_start)
                player = MediaPlayer.create(applicationContext, soundUri)
                player.isLooping = false
                player.start()
        }catch (e: Exception){
            e.printStackTrace()
        }

        binding.apply {
            flRestView.visibility = VISIBLE
            tvTitle.visibility = VISIBLE
            tvExerciseName.visibility = INVISIBLE
            flExerciseView.visibility = INVISIBLE
            ivImage.visibility = INVISIBLE
            tvUpcomingLabel.visibility = VISIBLE
            tvUpcomingExerciseName.visibility = VISIBLE
            tvUpcomingExerciseName.text = exerciseList[currentExercisePosition + 1].getName()
        }
        restProgress = 0
        setRestProgressBar()
    }

    private fun setupExerciseView(){
        binding.apply {
            flRestView.visibility = INVISIBLE
            tvTitle.visibility = INVISIBLE
            tvExerciseName.visibility = VISIBLE
            flExerciseView.visibility = VISIBLE
            ivImage.visibility = VISIBLE
            tvUpcomingLabel.visibility = INVISIBLE
            tvUpcomingExerciseName.visibility = INVISIBLE
            ivImage.setImageResource(exerciseList[currentExercisePosition].getImage())
            tvExerciseName.text = exerciseList[currentExercisePosition].getName()
        }
        exerciseProgress = 0

        speakOut(exerciseList[currentExercisePosition].getName())

        setExerciseProgressBar()
    }

    private fun setRestProgressBar() {
        binding.progressBar.progress = restProgress

        restTimer = object : CountDownTimer((restTimerDuration*1000).toLong(), 1000){
            override fun onTick(p0: Long) {
                restProgress++
                binding.progressBar.progress = restTimerDuration - restProgress
                binding.tvTimer.text = (restTimerDuration - restProgress).toString()
            }

            override fun onFinish() {
                currentExercisePosition++

                exerciseList[currentExercisePosition].setIsSelected(true)
                exerciseStatusAdapter.notifyDataSetChanged()

                setupExerciseView()
            }
        }.start()
    }

    private fun setExerciseProgressBar() {
        binding.progressBarExercise.progress = exerciseProgress

        exerciseTimer = object : CountDownTimer((exerciseTimerDuration*1000).toLong(), 1000){
            override fun onTick(p0: Long) {
                exerciseProgress++
                binding.progressBarExercise.progress = exerciseTimerDuration - exerciseProgress
                binding.tvTimerExercise.text = (exerciseTimerDuration - exerciseProgress).toString()
            }

            override fun onFinish() {

                if (currentExercisePosition < exerciseList.size - 1){
                    exerciseList[currentExercisePosition].setIsSelected(false)
                    exerciseList[currentExercisePosition].setIsCompleted(true)
                    exerciseStatusAdapter.notifyDataSetChanged()
                    setupRestView()
                }else {
                    finish()
                    val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    startActivity(intent)
//                    Toast.makeText(
//                        this@ExerciseActivity,
//                        getString(R.string.congrats),
//                        Toast.LENGTH_SHORT
//                    ).show()
                }

            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        restProgress = 0
        exerciseProgress = 0

        tts.stop()
        tts.shutdown()

        player.stop()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS){
            val result = tts.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG,getString(R.string.error_not_support_message))
            }
        }else {
            Log.e(TAG, getString(R.string.error_initialization_message))
        }
    }

    private fun speakOut(text: String){
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    private fun customDialogForBackButton() {
        val customDialog = Dialog(this)
        val dialogBinding = DialogCustomBackConfirmationBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)
        dialogBinding.btnYes.setOnClickListener {
            this.finish()
            customDialog.dismiss()
        }
        dialogBinding.btnNo.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()

    }
}