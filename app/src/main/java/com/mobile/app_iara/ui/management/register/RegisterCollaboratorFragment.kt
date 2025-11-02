package com.mobile.app_iara.ui.management.register

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.mobile.app_iara.R
import com.mobile.app_iara.data.model.response.AccessTypeResponse
import com.mobile.app_iara.databinding.FragmentRegisterCollaboratorBinding
import com.mobile.app_iara.data.model.response.GenderResponse
import com.mobile.app_iara.ui.error.WifiErrorActivity
import com.mobile.app_iara.ui.management.collaborator.Role
import com.mobile.app_iara.ui.management.collaborator.RolesAdapter
import com.mobile.app_iara.util.NetworkUtils
import kotlinx.coroutines.launch

class RegisterCollaboratorFragment : Fragment() {

    private var _binding: FragmentRegisterCollaboratorBinding? = null
    private val binding get() = _binding!!
    private var selectedGender: GenderResponse? = null
    private var selectedRole: AccessTypeResponse? = null
    private lateinit var auth: FirebaseAuth

    private val viewModel: RegisterCollaboratorViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterCollaboratorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadCurrentUserData()
        viewModel.loadGenders()

        auth = Firebase.auth
        setupObservers()
        setupClickListeners()

        binding.editTextDataNascimentoColaboradorCadastro.addTextChangedListener(object : android.text.TextWatcher {
            private var isUpdating = false
            private val mask = "####-##-##"

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) return

                val str = s.toString().replace(Regex("[^\\d]"), "")
                val formatted = StringBuilder()
                var i = 0

                for (m in mask.toCharArray()) {
                    if (m == '#') {
                        if (i >= str.length) break
                        formatted.append(str[i])
                        i++
                    } else {
                        if (i < str.length) formatted.append(m)
                    }
                }

                isUpdating = true
                binding.editTextDataNascimentoColaboradorCadastro.setText(formatted.toString())
                binding.editTextDataNascimentoColaboradorCadastro.setSelection(formatted.length)
                isUpdating = false
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })


    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.registerState.collect { state ->
                when (state) {
                    is RegisterState.Idle -> {
                    }
                    is RegisterState.Success -> {
                        Toast.makeText(
                            requireContext(),
                            "Colaborador registrado com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().popBackStack()
                    }
                    is RegisterState.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "Erro: ${state.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        if (!NetworkUtils.isInternetAvailable(requireContext())) {
            val intent = Intent(requireContext(), WifiErrorActivity::class.java)
            startActivity(intent)
            activity?.finish()
            return
        }

        binding.dropdownRole.setOnClickListener {
            showRoleSelectionDialog()
        }

        binding.dropdownGender.setOnClickListener {
            showGenderSelectionDialog()
        }

        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnCancelar.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnConfirmar.setOnClickListener {
            validateAndRegister()
        }
    }

    private fun validateAndRegister() {
        val name = binding.editTextNomeCadastroColaborador.text.toString().trim()
        val email = binding.editTextEmailColaboradorCadastro.text.toString().trim()
        val dateOfBirth = binding.editTextDataNascimentoColaboradorCadastro.text.toString().trim()

        when {
            name.isEmpty() -> {
                Toast.makeText(requireContext(), "Preencha o nome", Toast.LENGTH_SHORT).show()
                return
            }
            email.isEmpty() -> {
                Toast.makeText(requireContext(), "Preencha o email", Toast.LENGTH_SHORT).show()
                return
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(requireContext(), "Email inválido", Toast.LENGTH_SHORT).show()
                return
            }
            dateOfBirth.isEmpty() -> {
                Toast.makeText(requireContext(), "Preencha a data de nascimento", Toast.LENGTH_SHORT).show()
                return
            }
            selectedGender == null -> {
                Toast.makeText(requireContext(), "Selecione o gênero", Toast.LENGTH_SHORT).show()
                return
            }
            selectedRole == null -> {
                Toast.makeText(requireContext(), "Selecione o cargo", Toast.LENGTH_SHORT).show()
                return
            }
        }

        auth.createUserWithEmailAndPassword(email, "12345a")

        viewModel.registerCollaborator(
            name = name,
            email = email,
            password = "12345a",
            dateOfBirth = dateOfBirth,
            genderId = selectedGender!!.id,
            roleId = selectedRole!!.id
        )
    }

    private fun showRoleSelectionDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_role_dropdown, null)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.roles.collect { rolesList ->
                if (rolesList.isEmpty()) {
                    Toast.makeText(requireContext(), "Nenhum cargo disponível", Toast.LENGTH_SHORT).show()
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

                val recyclerView = view.findViewById<RecyclerView>(R.id.roles)
                recyclerView.layoutManager = LinearLayoutManager(requireContext())

                val adapter = RolesAdapter(roles) { role ->
                    selectedRole = rolesList.find { it.id == role.id }
                    binding.roleValue.text = role.name
                    binding.roleValue.setTextColor(Color.BLACK)

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

    private fun showGenderSelectionDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_gender_dropdown, null)
        val radioGroup = view.findViewById<RadioGroup>(R.id.radio_group_gender)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.genders.collect { gendersList ->
                if (gendersList.isEmpty()) {
                    Toast.makeText(requireContext(), "Nenhum gênero disponível", Toast.LENGTH_SHORT).show()
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
                        binding.genderValue.text = selectedRadioButton.text.toString()
                        binding.genderValue.setTextColor(Color.BLACK)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}