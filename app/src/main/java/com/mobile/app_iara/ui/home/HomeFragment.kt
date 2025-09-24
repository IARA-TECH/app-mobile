package com.mobile.app_iara.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.mobile.app_iara.R
import com.mobile.app_iara.ui.camera.CameraOverlay
import com.mobile.app_iara.ui.inicio.LoginActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val btnScan = view.findViewById<FloatingActionButton>(R.id.btnScan)

        btnScan.setOnClickListener {
            val intent = Intent(requireContext(), CameraOverlay::class.java)
            startActivity(intent)
        }

        return view
    }
}