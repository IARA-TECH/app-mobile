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

class ErrorProcessingActivity : AppCompatActivity() {

    private lateinit var rootLayout: ConstraintLayout
    private lateinit var redBackground: View
    private lateinit var errorContent: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error_processing)

        rootLayout = findViewById(R.id.root_layout)
        redBackground = findViewById(R.id.red_background)
        errorContent = findViewById(R.id.error_content)

        startErrorProcess()
    }

    private fun startErrorProcess() {
        Handler(Looper.getMainLooper()).postDelayed({
            showErrorAnimation()
        }, 1000)
    }

    private fun showErrorAnimation() {
        val transition = TransitionSet().apply {
            addTransition(ChangeBounds())
            addTransition(Fade(Fade.IN).addTarget(errorContent))
            duration = 200
        }

        TransitionManager.beginDelayedTransition(rootLayout, transition)
        errorContent.visibility = View.VISIBLE

        val params = redBackground.layoutParams
        params.height = ConstraintLayout.LayoutParams.MATCH_PARENT
        redBackground.layoutParams = params

        scheduleReturnToHome()
    }

    private fun scheduleReturnToHome() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }, 3000)
    }
}
