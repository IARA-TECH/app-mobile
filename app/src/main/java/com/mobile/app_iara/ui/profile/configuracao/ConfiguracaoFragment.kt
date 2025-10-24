package com.mobile.app_iara.ui.profile.configuracao

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
import com.mobile.app_iara.databinding.FragmentConfiguracaoBinding
import com.mobile.app_iara.ui.profile.faq.FaqActivity
import com.mobile.app_iara.ui.start.ForgotPasswordActivity

class ConfiguracaoFragment : Fragment() {

    private var _binding: FragmentConfiguracaoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ConfiguracaoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfiguracaoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()

        viewModel.fetchUserProfile()

        binding.tvPassword.setOnClickListener {
            val intent = Intent(requireContext(), ForgotPasswordActivity                                                                                                                                        ::class.java)
            startActivity(intent)
        }
    }

    private fun setupObservers() {
        viewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            if (userProfile != null) {
                binding.tvName.text = userProfile.name
                binding.tvEmail.text = userProfile.email
                binding.tvBirth.text = userProfile.dateOfBirth
                binding.tvGender.text = userProfile.genderName
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
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