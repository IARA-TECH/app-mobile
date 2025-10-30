package com.mobile.app_iara.ui.management

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mobile.app_iara.R
import com.mobile.app_iara.databinding.FragmentEditCollaboratorBinding
import com.mobile.app_iara.ui.management.collaborator.Role
import com.mobile.app_iara.ui.management.collaborator.RolesAdapter
import androidx.navigation.fragment.navArgs
import com.mobile.app_iara.ui.error.WifiErrorActivity
import com.mobile.app_iara.util.NetworkUtils

class EditCollaboratorFragment : Fragment() {

    private var _binding: FragmentEditCollaboratorBinding? = null
    private val binding get() = _binding!!
    private val args: EditCollaboratorFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditCollaboratorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val collaborator = args.collaborator

        binding.textViewNome.text = collaborator.name
        binding.textViewEmail.text = collaborator.email
        binding.textViewCargo.text = collaborator.role
        binding.textView272.text = collaborator.gender
        binding.textView274.text = collaborator.dateBirth


        if (!NetworkUtils.isInternetAvailable(requireContext())) {
            val intent = Intent(requireContext(), WifiErrorActivity::class.java)
            startActivity(intent)
            activity?.finish()
            return
        }

        setupClickListeners()

    }

    private fun setupClickListeners() {
        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.imageButtonExcluir.setOnClickListener {
            showConfirmationDialog()
        }

        binding.btnCancelar.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.sectionEmail.setOnClickListener {
            findNavController().navigate(R.id.action_editCollaboratorFragment_to_emailCollaboratorFragment)
        }

        binding.sectionGender.setOnClickListener {
            showGenderSelectionDialog()
        }

        binding.sectionRole.setOnClickListener {
            showRoleSelectionDialog()
        }
    }

    private fun showGenderSelectionDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_gender_dropdown, null)
        val radioGroup = view.findViewById<RadioGroup>(R.id.radio_group_gender)

        val currentGender = binding.textView272.text.toString()
        for (i in 0 until radioGroup.childCount) {
            val radioButton = radioGroup.getChildAt(i) as? RadioButton
            if (radioButton != null && radioButton.text.toString() == currentGender) {
                radioGroup.check(radioButton.id)
                break
            }
        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != -1) {
                val selectedRadioButton = group.findViewById<RadioButton>(checkedId)
                binding.textView272.text = selectedRadioButton.text.toString()
                binding.textView272.setTextColor(Color.BLACK)
                dialog.dismiss()
            }
        }

        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                it.setBackgroundColor(Color.TRANSPARENT)
            }
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.dialog_confirmation_management, null)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val width = (resources.displayMetrics.widthPixels * 0.8).toInt()
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        val btnConfirmar = view.findViewById<Button>(R.id.btnConfirmarExclusaoDialog)
        val btnCancelar = view.findViewById<Button>(R.id.btnCancelarExclusaoDialog)

        btnConfirmar.setOnClickListener {
            dialog.dismiss()
        }

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun showRoleSelectionDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_role_dropdown, null)
        val recyclerView = view.findViewById<RecyclerView>(R.id.roles)

        val currentRoleName = binding.textViewCargo.text.toString()

        val rolesList = mutableListOf(
            Role(id = 1, name = "Gerente de Produção"),
            Role(id = 2, name = "Colaborador"),
            Role(id = 3, name = "Médica Veterinária"),
            Role(id = 4, name = "Analista de Qualidade"),
            Role(id = 5, name = "Líder de Setor")
        )

        rolesList.find { it.name == currentRoleName }?.isSelected = true

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = RolesAdapter(rolesList) { selectedRole ->
            binding.textViewCargo.text = selectedRole.name
            binding.textViewCargo.setTextColor(Color.BLACK)
            dialog.dismiss()
        }
        recyclerView.adapter = adapter

        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                it.setBackgroundColor(Color.TRANSPARENT)
            }
        }

        dialog.setContentView(view)
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}