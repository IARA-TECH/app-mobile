package com.mobile.app_iara.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.mobile.app_iara.ui.inicio.LoginActivity
import com.mobile.app_iara.R
import com.mobile.app_iara.ui.profile.termsandprivacy.TermsActivity

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val btnSair = view.findViewById<MaterialCardView>(R.id.btnSair)
        btnSair.setOnClickListener {
            val prefs = requireActivity().getSharedPreferences("user_prefs", 0)
            prefs.edit().putBoolean("is_logged_in", false).apply()

            FirebaseAuth.getInstance().signOut()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        val btnTermos = view.findViewById<MaterialCardView>(R.id.btnTermsandconditions)

        btnTermos.setOnClickListener {
            val intent = Intent(requireContext(), TermsActivity::class.java)
            startActivity(intent)
        }

        val btnVoltar = view.findViewById<ImageButton>(R.id.btnVoltar2)
        btnVoltar.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        return view
    }
}
