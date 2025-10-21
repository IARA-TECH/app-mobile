package com.mobile.app_iara.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment 
import androidx.fragment.app.viewModels 
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.mobile.app_iara.R
import com.mobile.app_iara.databinding.FragmentProfileBinding 
import com.mobile.app_iara.ui.start.LoginActivity

class ProfileFragment : Fragment() {
    private val viewModel: ProfileViewModel by viewModels()
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.updateUserPhoto(requireContext(), it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadCurrentUserProfile()

        setupObservers()
        setupButtonClickListeners()
    }

    private fun setupObservers() {
        viewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            binding.textView13.text = userProfile.name
            binding.textView16.text = userProfile.email

            Glide.with(this)
                .load(userProfile.userPhotoUrl)
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .into(binding.fotoPerfil)
        }

        viewModel.newPhotoUrl.observe(viewLifecycleOwner) { newPhotoUrl ->
            if (newPhotoUrl != null) {
                Glide.with(this)
                    .load(newPhotoUrl)
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .into(binding.fotoPerfil)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupButtonClickListeners() {
        binding.btnTrocarFoto.setOnClickListener {
            pickImage.launch("image/*")
        }
        binding.btnConfig.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_configuracao)
        }
        binding.btnFabrica.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_fabrica)
        }
        binding.btnSair.setOnClickListener {
            confirmarSaida()
        }
        binding.btnFaq.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_faqFragment)
        }
        binding.btnTermsandconditions.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_termsFragment)
        }
        binding.btnChatbot.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_chatFragment)
        }
    }

    private fun confirmarSaida() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.dialog_exit_confirmation, null)
        builder.setView(dialogView)

        val dialog = builder.create().apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            show()
            val width = (resources.displayMetrics.widthPixels * 0.8).toInt()
            window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        dialogView.findViewById<Button>(R.id.btnSairDialog).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnCancelarDialog).setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}