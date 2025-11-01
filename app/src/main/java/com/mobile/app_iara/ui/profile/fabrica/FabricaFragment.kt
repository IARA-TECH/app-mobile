package com.mobile.app_iara.ui.profile.fabrica

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mobile.app_iara.databinding.FragmentFabricaBinding
import com.mobile.app_iara.util.formatCnpj

class FabricaFragment : Fragment() {

    private var _binding: FragmentFabricaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FabricaViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFabricaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        viewModel.fetchFactoryData()
    }

    private fun setupObservers() {
        viewModel.factoryDetails.observe(viewLifecycleOwner) { factory ->
            binding.tvEmpresa.text = factory.name
            binding.tvDominio.text = factory.domain
            binding.tvCnpj.text = factory.cnpj.formatCnpj()
            binding.tvDescricao.text = factory.description
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