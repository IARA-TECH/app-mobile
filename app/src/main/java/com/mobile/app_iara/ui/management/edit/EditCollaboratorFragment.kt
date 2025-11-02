package com.mobile.app_iara.ui.management.edit

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mobile.app_iara.databinding.FragmentEditCollaboratorBinding
import com.mobile.app_iara.data.model.response.AccessTypeResponse
import com.mobile.app_iara.data.model.response.GenderResponse
import com.mobile.app_iara.ui.management.collaborator.Role
import com.mobile.app_iara.ui.management.collaborator.RolesAdapter
import androidx.navigation.fragment.navArgs
import com.mobile.app_iara.ui.error.WifiErrorActivity
import com.mobile.app_iara.util.NetworkUtils
import kotlinx.coroutines.launch
import com.mobile.app_iara.R
import com.mobile.app_iara.ui.status.LoadingApiFragment

class EditCollaboratorFragment : Fragment() {

    private var _binding: FragmentEditCollaboratorBinding? = null
    private val binding get() = _binding!!
    private val args: EditCollaboratorFragmentArgs by navArgs()

    private val viewModel: EditCollaboratorViewModel by viewModels()

    private var selectedGender: GenderResponse? = null
    private var selectedRole: AccessTypeResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditCollaboratorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .add(R.id.loading_container, LoadingApiFragment.newInstance())
                .commit()
        }

        if (!NetworkUtils.isInternetAvailable(requireContext())) {
            val intent = Intent(requireContext(), WifiErrorActivity::class.java)
            startActivity(intent)
            activity?.finish()
            return
        }

        val collaborator = args.collaborator
        binding.textViewNome.text = collaborator.name
        binding.textViewEmail.text = collaborator.email
        binding.textViewCargo.text = collaborator.roleName
        binding.textView272.text = collaborator.genderName
        binding.textView274.text = collaborator.dateBirth

        setupClickListeners()
        setupObservers()

        binding.loadingContainer.visibility = View.VISIBLE
        viewModel.loadGenders()
        viewModel.loadUserAccessTypes()
    }

    private fun setupClickListeners() {
        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.imageButtonExcluir.setOnClickListener {
            showDeactivationConfirmationDialog()
        }

        binding.btnCancelar.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnConfirmar.setOnClickListener {
            validateAndUpdate()
        }

        binding.sectionGender.setOnClickListener {
            showGenderSelectionDialog()
        }

        binding.sectionRole.setOnClickListener {
            showRoleSelectionDialog()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.genders.collect { gendersList ->
                binding.loadingContainer.visibility = View.GONE
                if (gendersList.isNotEmpty() && selectedGender == null) {
                    selectedGender = gendersList.find { it.name == args.collaborator.genderName }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.roles.collect { rolesList ->
                binding.loadingContainer.visibility = View.GONE
                if (rolesList.isNotEmpty() && selectedRole == null) {
                    selectedRole = rolesList.find { it.name == args.collaborator.roleName }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateState.collect { state ->
                when (state) {
                    is UpdateState.Loading -> {
                        binding.loadingContainer.visibility = View.VISIBLE
                        binding.btnConfirmar.isEnabled = false
                    }

                    is UpdateState.Success -> {
                        binding.loadingContainer.visibility = View.GONE
                        showSuccessDialog()
                        viewModel.resetUpdateState()
                    }

                    is UpdateState.Error -> {
                        binding.loadingContainer.visibility = View.GONE
                        binding.btnConfirmar.isEnabled = true
                        showErrorDialog()
                        viewModel.resetUpdateState()
                    }

                    is UpdateState.Idle -> {
                        binding.btnConfirmar.isEnabled = true
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.deactivationState.collect { state ->
                when (state) {
                    is DeactivationState.Loading -> {
                        binding.loadingContainer.visibility = View.VISIBLE
                        binding.btnConfirmar.isEnabled = false
                        binding.btnCancelar.isEnabled = false
                        binding.imageButtonExcluir.isEnabled = false
                    }

                    is DeactivationState.Success -> {
                        binding.loadingContainer.visibility = View.GONE
                        findNavController().popBackStack()
                        viewModel.resetDeactivationState()
                    }

                    is DeactivationState.Error -> {
                        binding.loadingContainer.visibility = View.GONE
                        binding.btnConfirmar.isEnabled = true
                        binding.btnCancelar.isEnabled = true
                        binding.imageButtonExcluir.isEnabled = true
                        showErrorDialog()
                        viewModel.resetDeactivationState()
                    }

                    is DeactivationState.Idle -> {
                        binding.btnConfirmar.isEnabled = true
                        binding.btnCancelar.isEnabled = true
                        binding.imageButtonExcluir.isEnabled = true
                    }
                }
            }
        }
    }

    private fun showDeactivationConfirmationDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_delete_confirmation, null)

        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)

        val dialog = builder.create()

        val title = dialogView.findViewById<TextView>(R.id.tituloDialogDelete)
        val message = dialogView.findViewById<TextView>(R.id.avisoDialogDelete)
        val deleteButton = dialogView.findViewById<Button>(R.id.btnDeletarDialogDelete)
        val cancelButton = dialogView.findViewById<Button>(R.id.btnCancelarDialogDelete)
        title.text = "Confirmar Desativação"
        message.text = "Tem certeza que deseja desativar este colaborador? Esta ação não pode ser desfeita."
        deleteButton.text = "Desativar"

        deleteButton.setOnClickListener {
            viewModel.deactivateCollaborator(args.collaborator.id)
            dialog.dismiss()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.show()
    }

    private fun showSuccessDialog() {
        val successSheet = EditCollaboratorSuccess {
            findNavController().popBackStack()
        }
        successSheet.isCancelable = false
        successSheet.show(childFragmentManager, "EditSuccessSheet")
    }

    private fun showErrorDialog() {
        val errorSheet = EditCollaboratorError()
        errorSheet.isCancelable = true
        errorSheet.show(childFragmentManager, "EditErrorSheet")
    }

    private fun validateAndUpdate() {
        if (selectedGender == null) {
            Toast.makeText(requireContext(), "Aguarde, carregando gêneros...", Toast.LENGTH_SHORT).show()
            viewModel.loadGenders()
            return
        }
        if (selectedRole == null) {
            Toast.makeText(requireContext(), "Aguarde, carregando cargos...", Toast.LENGTH_SHORT).show()
            viewModel.loadUserAccessTypes()
            return
        }

        val oldData = args.collaborator

        viewModel.updateCollaborator(
            oldData = oldData,
            newGenderId = selectedGender!!.id,
            newRoleId = selectedRole!!.id
        )
    }

    private fun showGenderSelectionDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_gender_dropdown, null)
        val radioGroup = view.findViewById<RadioGroup>(R.id.radio_group_gender)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.genders.collect { gendersList ->
                if (gendersList.isEmpty()) {
                    dialog.dismiss()
                    return@collect
                }

                radioGroup.removeAllViews()
                gendersList.forEach { gender ->
                    val radioButton = RadioButton(requireContext()).apply {
                        id = View.generateViewId()
                        text = gender.name
                        textSize = 16f
                        setPadding(16, 16, 16, 16)
                        tag = gender.id
                    }
                    radioGroup.addView(radioButton)

                    if (selectedGender?.id == gender.id) {
                        radioGroup.check(radioButton.id)
                    }
                }

                radioGroup.setOnCheckedChangeListener { group, checkedId ->
                    if (checkedId != -1) {
                        val selectedRadioButton = group.findViewById<RadioButton>(checkedId)
                        val genderId = selectedRadioButton.tag as Int

                        selectedGender = gendersList.find { it.id == genderId }
                        binding.textView272.text = selectedRadioButton.text.toString()
                        binding.textView272.setTextColor(Color.BLACK)

                        dialog.dismiss()
                    }
                }
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

    private fun showRoleSelectionDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_role_dropdown, null)
        val recyclerView = view.findViewById<RecyclerView>(R.id.roles)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.roles.collect { rolesList ->
                if (rolesList.isEmpty()) {
                    dialog.dismiss()
                    return@collect
                }

                val roles = rolesList.map { accessType ->
                    Role(
                        id = accessType.id,
                        name = accessType.name,
                        isSelected = selectedRole?.id == accessType.id
                    )
                }

                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                val adapter = RolesAdapter(roles) { role ->
                    selectedRole = rolesList.find { it.id == role.id }
                    binding.textViewCargo.text = role.name
                    binding.textViewCargo.setTextColor(Color.BLACK)

                    dialog.dismiss()
                }
                recyclerView.adapter = adapter
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}