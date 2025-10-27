package com.mobile.app_iara.ui.management

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.app_iara.R
import com.mobile.app_iara.databinding.FragmentManagementBinding
import com.mobile.app_iara.ui.management.collaborator.CollaboratorAdapter

class ManagementFragment : Fragment() {

    private var _binding: FragmentManagementBinding? = null
    private val binding get() = _binding!!

    private lateinit var collaboratorAdapter: CollaboratorAdapter
    private lateinit var viewModel: ManagementViewModel

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

        viewModel = ViewModelProvider(this)[ManagementViewModel::class.java]

        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        viewModel.loadCollaborators()
    }

    private fun setupRecyclerView() {
        collaboratorAdapter = CollaboratorAdapter { collaborator ->
            val action = ManagementFragmentDirections
                .actionManagementFragmentToEditCollaboratorFragment(collaborator.id)

            findNavController().navigate(action)
        }

        binding.recyclerViewEmployees.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = collaboratorAdapter
        }
    }

    private fun setupObservers() {
        viewModel.collaborators.observe(viewLifecycleOwner) { collaborators ->
            collaboratorAdapter.submitList(collaborators)
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupClickListeners() {
        binding.imageButtonAddCollaborator.setOnClickListener {
            findNavController().navigate(R.id.action_managementFragment_to_registerCollaboratorFragment)
        }

        binding.included.imgPerfilToolbar.setOnClickListener {
            findNavController().navigate(R.id.action_managementFragment_to_profileFragment)
        }

        binding.included.iconNotificationToolbar.setOnClickListener {
            findNavController().navigate(R.id.action_managementFragment_to_notificationsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}