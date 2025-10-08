package com.mobile.app_iara.ui.camera

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.mobile.app_iara.MainActivity
import com.mobile.app_iara.R

class ProcessingActivity : AppCompatActivity() {

    private lateinit var rootLayout: ConstraintLayout
    private lateinit var blueBackground: View
    private lateinit var successContent: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_processing)

        rootLayout = findViewById(R.id.root_layout)
        blueBackground = findViewById(R.id.blue_background)
        successContent = findViewById(R.id.success_content)

        startProcessing()
    }

    private fun startProcessing() {
        Handler(Looper.getMainLooper()).postDelayed({
            showSuccessAnimation()
        }, 2000)
    }

    private fun showSuccessAnimation() {
        val transition = TransitionSet().apply {
            addTransition(ChangeBounds())
            addTransition(Fade(Fade.IN).addTarget(successContent))
            duration = 200 // Duração da animação em milissegundos
        }

        TransitionManager.beginDelayedTransition(rootLayout, transition)

        successContent.visibility = View.VISIBLE

        val params = blueBackground.layoutParams
        params.height = ConstraintLayout.LayoutParams.MATCH_PARENT
        blueBackground.layoutParams = params

        scheduleReturnToHome()
    }

    private fun scheduleReturnToHome() {
        Handler(Looper.getMainLooper()).postDelayed({
             val intent = Intent(this, MainActivity::class.java)
             intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
             startActivity(intent)
            finish()         }, 2000)
    }
}