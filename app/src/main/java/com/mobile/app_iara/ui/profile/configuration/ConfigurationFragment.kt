package com.mobile.app_iara.ui.profile.configuration

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mobile.app_iara.R
import com.mobile.app_iara.databinding.FragmentConfigurationBinding
import com.mobile.app_iara.ui.start.ForgotPasswordActivity
import com.mobile.app_iara.ui.status.LoadingApiFragment

class ConfigurationFragment : Fragment() {

    private var _binding: FragmentConfigurationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ConfigurationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfigurationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .add(R.id.loading_container, LoadingApiFragment.newInstance())
                .commit()
        }

        setupObservers()

        binding.loadingContainer.visibility = View.VISIBLE
        viewModel.fetchUserProfile()

        binding.tvPassword.setOnClickListener {
            val intent = Intent(requireContext(), ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.btnConfirm.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.included.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupObservers() {
        viewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            binding.loadingContainer.visibility = View.GONE
            if (userProfile != null) {
                binding.tvName.text = userProfile.name
                binding.tvEmail.text = userProfile.email
                binding.tvBirth.text = userProfile.dateOfBirth
                binding.tvGender.text = userProfile.genderName
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            binding.loadingContainer.visibility = View.GONE
            if (errorMessage != null) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

