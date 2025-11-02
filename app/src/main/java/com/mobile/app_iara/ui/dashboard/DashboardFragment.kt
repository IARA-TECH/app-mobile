package com.mobile.app_iara.ui.dashboard

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mobile.app_iara.databinding.FragmentDashboardBinding
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.mobile.app_iara.R
import com.mobile.app_iara.ui.error.WifiErrorActivity
import com.mobile.app_iara.util.NetworkUtils

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels {
        DashboardViewModelFactory()
    }

    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!NetworkUtils.isInternetAvailable(requireContext())) {
            val intent = Intent(requireContext(), WifiErrorActivity::class.java)
            startActivity(intent)
            activity?.finish()
            return
        }

        sharedPrefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        setupClickListeners()
        observeViewModel()

        val factoryId = sharedPrefs.getInt("key_factory_id", -1)
        if (factoryId != -1) {
            viewModel.fetchData(factoryId)
        } else {
            Toast.makeText(requireContext(), "Erro: ID da fábrica não encontrado.", Toast.LENGTH_LONG).show()
        }

        viewModel.loadUserProfileData(requireContext())
    }

    private fun setupClickListeners() {
        binding.cardTechnicalFailures.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_technicalFailures)
        }

        binding.cardFarmCondemnation.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_farmCondemnation)
        }

        binding.cardShiftComparison.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_shiftComparison)
        }

        binding.cardDashboardComparison.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_dashboardComparison)
        }

        binding.included.imgPerfilToolbar.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_profileFragment)
        }

        binding.included.iconNotificationToolbar.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_notificationsFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.technicalTotal.observe(viewLifecycleOwner) { total ->
            binding.textView28.text = "Total: $total"
        }

        viewModel.farmTotal.observe(viewLifecycleOwner) { total ->
            binding.textView29.text = "Total: $total"
        }

        viewModel.currentShiftName.observe(viewLifecycleOwner) { shiftName ->
            binding.textView30.text = "Turno atual: $shiftName"
        }

        viewModel.comparisonTotal.observe(viewLifecycleOwner) { total ->
            binding.textView31.text = "Total de condenas: $total"
        }

        viewModel.userPhotoUrl.observe(viewLifecycleOwner) { photoUrl ->
            if (!photoUrl.isNullOrEmpty()) {
                Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.ic_profile_circle)
                    .error(R.drawable.ic_profile_circle)
                    .circleCrop()
                    .into(binding.included.imgPerfilToolbar)
            } else {
                binding.included.imgPerfilToolbar.setImageResource(R.drawable.ic_profile_circle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}