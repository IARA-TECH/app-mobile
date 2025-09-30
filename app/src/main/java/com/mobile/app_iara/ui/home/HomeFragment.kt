package com.mobile.app_iara.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobile.app_iara.databinding.FragmentHomeBinding
import com.mobile.app_iara.utils.NetworkUtils
import com.mobile.app_iara.ui.erros.ErroWifiActivity
import com.mobile.app_iara.ui.camera.CameraOverlay
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla o layout primeiro
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root = binding.root

        // Verifica conexão de internet
        if (!NetworkUtils.isInternetAvailable(requireContext())) {
            val intent = Intent(requireContext(), ErroWifiActivity::class.java)
            startActivity(intent)
            activity?.finish()
            return root
        }

        // Botão de Scan
        binding.btnScan.setOnClickListener {
            val intent = Intent(requireContext(), CameraOverlay::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}