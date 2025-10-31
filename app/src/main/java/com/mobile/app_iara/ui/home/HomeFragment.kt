package com.mobile.app_iara.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.mobile.app_iara.databinding.FragmentHomeBinding
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.mobile.app_iara.R
import com.mobile.app_iara.ui.error.WifiErrorActivity
import com.mobile.app_iara.util.NetworkUtils

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        viewModel.loadUserProfileData()

        if (!NetworkUtils.isInternetAvailable(requireContext())) {
            val intent = Intent(requireContext(), WifiErrorActivity::class.java)
            startActivity(intent)
            activity?.finish()
            return
        }

        binding.cardSpreadSheets.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_spreadSheets)
        }
        
        binding.cardHistory.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_history)
        }

        binding.cardAbacus.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_abacusList)
        }
        binding.cardChat.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_chatFragment)
        }
        binding.included.imgPerfilToolbar.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
        binding.included.iconNotificationToolbar.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_notificationsFragment)
        }
    }

    private fun setupObservers() {
        viewModel.userPhotoUrl.observe(viewLifecycleOwner) { photoUrl ->
            if (!photoUrl.isNullOrEmpty()) {
                Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.ic_profile_circle)
                    .error(R.drawable.ic_profile_circle)
                    .circleCrop()
                    .into(binding.included.imgPerfilToolbar)
            }
        }

        viewModel.userName.observe(viewLifecycleOwner) { name ->
            if (!name.isNullOrEmpty()) {
                binding.txtSaudacao.text = "Olá, $name"
            } else {
                binding.txtSaudacao.text = "Olá!"
            }
        }
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
