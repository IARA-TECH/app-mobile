package com.mobile.app_iara.ui.management

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mobile.app_iara.R
import com.mobile.app_iara.databinding.FragmentRegisterCollaboratorBinding
import com.mobile.app_iara.ui.management.collaborator.Role
import com.mobile.app_iara.ui.management.collaborator.RolesAdapter

class RegisterCollaboratorFragment : Fragment() {

    private var _binding: FragmentRegisterCollaboratorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterCollaboratorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dropdownRole.setOnClickListener {
            showRoleSelectionDialog()
        }

        binding.dropdownGender.setOnClickListener {
            showGenderSelectionDialog()
        }
    }

    private fun showRoleSelectionDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_role_dropdown, null)

        val rolesList = getRolesFromViewModel()

        val recyclerView = view.findViewById<RecyclerView>(R.id.roles)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = RolesAdapter(rolesList) { selectedRole ->
            binding.roleValue.text = selectedRole.name
            binding.roleValue.setTextColor(Color.BLACK)
            dialog.dismiss()
        }
        recyclerView.adapter = adapter

        dialog.setContentView(view)
        dialog.show()
    }


    private fun showGenderSelectionDialog() {
        val dialog = BottomSheetDialog(requireContext())

        val view = layoutInflater.inflate(R.layout.dialog_gender_dropdown, null)
        val radioGroup = view.findViewById<RadioGroup>(R.id.radio_group_gender)

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != -1) {
                val selectedRadioButton = group.findViewById<RadioButton>(checkedId)
                val selectedText = selectedRadioButton.text.toString()

                binding.genderValue.text = selectedText
                binding.genderValue.setTextColor(Color.BLACK)

                dialog.dismiss()
            }
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun getRolesFromViewModel(): List<Role> {
        return listOf(
            Role(1, "Supervisor SIF", isSelected = false),
            Role(2, "Colaborador", isSelected = false),
            Role(3, "Germinare", isSelected = false),
            Role(4, "Analista de Qualidade", isSelected = false)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}