package com.mobile.app_iara.ui.profile.factory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mobile.app_iara.R
import com.mobile.app_iara.databinding.FragmentFactoryBinding
import com.mobile.app_iara.ui.status.LoadingApiFragment
import com.mobile.app_iara.util.formatCnpj

class FactoryFragment : Fragment() {

    private var _binding: FragmentFactoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FactoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFactoryBinding.inflate(inflater, container, false)
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

        binding.included.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.loadingContainer.visibility = View.VISIBLE
        viewModel.fetchFactoryData()
    }

    private fun setupObservers() {
        viewModel.factoryDetails.observe(viewLifecycleOwner) { factory ->
            binding.loadingContainer.visibility = View.GONE
            binding.tvEmpresa.text = factory.name
            binding.tvDominio.text = factory.domain
            binding.tvCnpj.text = factory.cnpj.formatCnpj()
            binding.tvDescricao.text = factory.description
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            binding.loadingContainer.visibility = View.GONE
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
