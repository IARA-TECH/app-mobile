package com.mobile.app_iara.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mobile.app_iara.databinding.FragmentDashboardBinding
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.mobile.app_iara.R

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    private val binding get() = _binding!!

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

        binding.cardTechnicalFailures.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_technicalFailures)
        }

        binding.cardShiftComparison.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_shiftComparison)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}