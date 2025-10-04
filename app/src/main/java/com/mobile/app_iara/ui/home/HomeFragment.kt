package com.mobile.app_iara.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mobile.app_iara.R
import com.mobile.app_iara.databinding.FragmentHomeBinding
import com.mobile.app_iara.utils.NetworkUtils
import com.mobile.app_iara.ui.error.WifiErrorActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
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

        binding.cardAbacus.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_abacusList)
        }
        binding.cardChat.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_chatFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}