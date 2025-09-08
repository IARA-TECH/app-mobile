package com.mobile.app_iara.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.mobile.app_iara.R
import com.mobile.app_iara.ui.inicio.LoginActivity
import com.mobile.app_iara.ui.profile.faq.FaqActivity
import com.mobile.app_iara.ui.profile.termsandprivacy.TermsActivity

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Referências de UI
        val imageProfile = view.findViewById<ShapeableImageView>(R.id.imageView3)
        val userName = view.findViewById<TextView>(R.id.textView13) // Nome
        val userCargo = view.findViewById<TextView>(R.id.textView16) // Cargo / Email

        val btnSair = view.findViewById<MaterialCardView>(R.id.btnSair)
        val btnTermos = view.findViewById<MaterialCardView>(R.id.btnTermsandconditions)
        val btnFaq = view.findViewById<MaterialCardView>(R.id.btnFaq)
        val btnVoltar = view.findViewById<ImageButton>(R.id.btnVoltar2)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            userName.text = user.displayName ?: "Usuário"

            userCargo.text = user.email ?: "Cargo não definido"

            // Foto do Google
            val photoUrl = user.photoUrl
            if (photoUrl != null) {
                Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.user)
                    .into(imageProfile)
            }
        }

        btnSair.setOnClickListener {
            val prefs = requireActivity().getSharedPreferences("user_prefs", 0)
            prefs.edit().putBoolean("is_logged_in", false).apply()

            FirebaseAuth.getInstance().signOut()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        // Botão FAQ
        btnFaq.setOnClickListener {
            val intent = Intent(requireContext(), FaqActivity::class.java)
            startActivity(intent)
        }

        // Botão Termos
        btnTermos.setOnClickListener {
            val intent = Intent(requireContext(), TermsActivity::class.java)
            startActivity(intent)
        }

        // Botão voltar
        btnVoltar.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return view
    }
}
