package com.mobile.app_iara.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.mobile.app_iara.R
import com.mobile.app_iara.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        setupDayButtons()
        setActiveStyle(binding.button)
        resetStyle(binding.button2)
        resetStyle(binding.button3)

        return binding.root
    }

    private fun setupDayButtons() {
        val buttons = listOf(binding.button, binding.button2, binding.button3)

        buttons.forEach { btn ->
            btn.setOnClickListener {
                buttons.forEach { resetStyle(it) }
                setActiveStyle(btn)

                when (btn.id) {
                    binding.button.id -> atualizarInformacoes(7)
                    binding.button2.id -> atualizarInformacoes(15)
                    binding.button3.id -> atualizarInformacoes(30)
                }
            }
        }
    }

    private fun setActiveStyle(button: Button) {
        button.background = ContextCompat.getDrawable(requireContext(), R.drawable.btn_dias_active)
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.branco_background))
        button.backgroundTintList = null
    }

    private fun resetStyle(button: Button) {
        button.background = ContextCompat.getDrawable(requireContext(), R.drawable.btn_dias_inactive)
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.azul_primario))
        button.backgroundTintList = null
    }

    private fun atualizarInformacoes(dias: Int) {
        // colocar logica aq
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecione uma data")
            .build()

        binding.imageButton.setOnClickListener {
            datePicker.show(parentFragmentManager, "DATE_PICKER")
        }

        datePicker.addOnPositiveButtonClickListener { selection ->
            Toast.makeText(requireContext(), "Data: $selection", Toast.LENGTH_SHORT).show()
            // colocar logica aq
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}