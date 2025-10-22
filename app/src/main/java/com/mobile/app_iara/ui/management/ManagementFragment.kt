package com.mobile.app_iara.ui.management

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.app_iara.R
import com.mobile.app_iara.databinding.FragmentManagementBinding
import com.mobile.app_iara.ui.management.collaborator.CollaboratorAdapter
import com.mobile.app_iara.ui.management.collaborator.CollaboratorModal
import java.text.Normalizer

class ManagementFragment : Fragment() {

    private var _binding: FragmentManagementBinding? = null
    private val binding get() = _binding!!

    private lateinit var collaboratorAdapter: CollaboratorAdapter

    private val listaOriginal = listOf(
        CollaboratorModal(id="1", name="Mariana Costa", email="mariana.costa@seara.com", role="Gerente de Produção", urlPhoto=null),
        CollaboratorModal(id="2", name="Carlos Emanuel", email="carlos.emanuel@seara.com", role="Colaborador", urlPhoto=null),
        CollaboratorModal(id="3", name="Lucas Silva", email="lucas.silva@seara.com", role="Colaborador", urlPhoto=null),
        CollaboratorModal(id="4", name="Luiza Mariano", email="luiza.mariano@seara.com", role="Médica Veterinária", urlPhoto=null),
        CollaboratorModal(id="5", name="Fernanda Souza", email="fernanda.souza@seara.com", role="Colaborador", urlPhoto=null),
        CollaboratorModal(id="6", name="Matheus Cardoso", email="matheus.cardoso@seara.com", role="Colaborador", urlPhoto=null),
        CollaboratorModal(id="7", name="Amanda Oliveira", email="amanda.oliveira@seara.com", role="Colaborador", urlPhoto=null)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collaboratorAdapter = CollaboratorAdapter { collaborator ->

            val action = ManagementFragmentDirections
                .actionManagementFragmentToEditCollaboratorFragment(collaborator.id)

            findNavController().navigate(action)
        }

        binding.recyclerViewEmployees.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = collaboratorAdapter
        }

        binding.inputSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().removeAccents().lowercase()

                if (query.isNotBlank()) {
                    val filteredList = listaOriginal.filter { collaborator ->
                        collaborator.name.removeAccents().lowercase().contains(query)
                    }
                    collaboratorAdapter.submitList(filteredList)
                } else {
                    collaboratorAdapter.submitList(listaOriginal)
                }
            }
        })

        binding.imageButtonAddCollaborator.setOnClickListener {
            findNavController().navigate(R.id.action_managementFragment_to_registerCollaboratorFragment)
        }

        binding.included.imgPerfilToolbar.setOnClickListener {
            findNavController().navigate(R.id.action_managementFragment_to_profileFragment)
        }

        binding.included.iconNotificationToolbar.setOnClickListener {
            findNavController().navigate(R.id.action_managementFragment_to_notificationsFragment)
        }

        collaboratorAdapter.submitList(listaOriginal)
    }

    fun String.removeAccents(): String {
        val normalizedString = Normalizer.normalize(this, Normalizer.Form.NFD)
        val regex = "\\p{InCombiningDiacriticalMarks}+".toRegex()
        return regex.replace(normalizedString, "")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}