package com.mobile.app_iara.ui.management

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
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

class EditCollaboratorFragment : Fragment() {

    private var _binding: FragmentEditCollaboratorBinding? = null
    private val binding get() = _binding!!
    private val args: EditCollaboratorFragmentArgs by navArgs()

    private val viewModel: EditCollaboratorViewModel by viewModels()

    // --- Guarde as seleções (como objetos de Response da API) ---
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

        if (!NetworkUtils.isInternetAvailable(requireContext())) {
            val intent = Intent(requireContext(), WifiErrorActivity::class.java)
            startActivity(intent)
            activity?.finish()
            return
        }

        // Carrega os dados iniciais do colaborador (vindo do Modal)
        val collaborator = args.collaborator
        binding.textViewNome.text = collaborator.name
        binding.textViewEmail.text = collaborator.email
        binding.textViewCargo.text = collaborator.roleName // <- Ajuste se o nome do campo for outro
        binding.textView272.text = collaborator.genderName
        binding.textView274.text = collaborator.dateBirth

        // Carrega as listas de Gêneros e Cargos
        viewModel.loadGenders()
        viewModel.loadUserAccessTypes()

        setupClickListeners()
        setupObservers()
    }

    private fun setupClickListeners() {
        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.imageButtonExcluir.setOnClickListener {
            // Lógica de exclusão pendente
            Toast.makeText(requireContext(), "Função de excluir não implementada.", Toast.LENGTH_SHORT).show()
        }

        binding.btnCancelar.setOnClickListener {
            findNavController().popBackStack()
        }

        // Assumindo que seu botão de salvar se chama 'btnConfirmar' no XML
        binding.btnConfirmar.setOnClickListener {
            validateAndUpdate()
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

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            // Observador para carregar os GÊNEROS
            viewModel.genders.collect { gendersList ->
                if (gendersList.isNotEmpty() && selectedGender == null) {
                    // Encontra o gênero inicial do colaborador
                    selectedGender = gendersList.find { it.name == args.collaborator.genderName }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            // Observador para carregar os CARGOS
            viewModel.roles.collect { rolesList ->
                if (rolesList.isNotEmpty() && selectedRole == null) {
                    // Encontra o cargo inicial do colaborador
                    selectedRole = rolesList.find { it.name == args.collaborator.roleName } // <- Ajuste se o nome do campo for outro
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            // Observador do UPDATE
            viewModel.updateState.collect { state ->
                // Assumindo que o botão de salvar se chama 'btnConfirmar'
                when (state) {
                    is UpdateState.Loading -> {
                        binding.btnConfirmar.isEnabled = false
                    }
                    is UpdateState.Success -> {
                        Toast.makeText(requireContext(), "Atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                        viewModel.resetUpdateState()
                        findNavController().popBackStack()
                    }
                    is UpdateState.Error -> {
                        Toast.makeText(requireContext(), "Erro: ${state.message}", Toast.LENGTH_LONG).show()
                        binding.btnConfirmar.isEnabled = true
                        viewModel.resetUpdateState()
                    }
                    is UpdateState.Idle -> {
                        binding.btnConfirmar.isEnabled = true
                    }
                }
            }
        }
    }

    private fun validateAndUpdate() {
        // Valida se as seleções foram carregadas
        if (selectedGender == null) {
            Toast.makeText(requireContext(), "Aguarde, carregando gêneros...", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedRole == null) {
            Toast.makeText(requireContext(), "Aguarde, carregando cargos...", Toast.LENGTH_SHORT).show()
            return
        }

        val oldData = args.collaborator

        // Chama o ViewModel com a assinatura correta
        viewModel.updateCollaborator(
            oldData = oldData,
            newGenderId = selectedGender!!.id,
            newRoleId = selectedRole!!.id
        )
    }

    private fun showGenderSelectionDialog() {
        // Lógica idêntica ao seu RegisterFragment
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
        // Lógica idêntica ao seu RegisterFragment
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
                        name = accessType.name ?: "Sem nome",
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