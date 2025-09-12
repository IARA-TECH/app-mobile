package com.mobile.app_iara.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.mobile.app_iara.ui.inicio.LoginActivity
import com.mobile.app_iara.R
import com.mobile.app_iara.ui.profile.faq.FaqActivity
import com.mobile.app_iara.ui.profile.termsandprivacy.TermsActivity

class ProfileFragment : Fragment() {

    private lateinit var fotoPerfil: ImageView
    private lateinit var btnTrocarFoto: ImageButton

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            Glide.with(this)
                .load(it)
                .into(fotoPerfil)
        }
    }

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
        val btnFaq = view.findViewById<MaterialCardView>(R.id.btnFaq)

        btnFaq.setOnClickListener {
            val intent = Intent(requireContext(), FaqActivity::class.java)
            startActivity(intent)
        }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fotoPerfil = view.findViewById(R.id.fotoPerfil)
        btnTrocarFoto = view.findViewById(R.id.btnTrocarFoto)

        btnTrocarFoto.setOnClickListener {
            pickImage.launch("image/*")
        }
    }
}
