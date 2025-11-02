package com.mobile.app_iara.ui.management

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.mobile.app_iara.R
import com.mobile.app_iara.databinding.FragmentManagementBinding
import com.mobile.app_iara.ui.error.InternalErrorActivity
import com.mobile.app_iara.ui.error.WifiErrorActivity
import com.mobile.app_iara.ui.management.collaborator.CollaboratorAdapter
import com.mobile.app_iara.ui.management.collaborator.CollaboratorModal
import com.mobile.app_iara.ui.status.LoadingApiFragment // NOVO: Import
import com.mobile.app_iara.util.NetworkUtils

class ManagementFragment : Fragment() {

    private var _binding: FragmentManagementBinding? = null
    private val binding get() = _binding!!

    private lateinit var collaboratorAdapter: CollaboratorAdapter
    private lateinit var viewModel: ManagementViewModel

    private var allCollaborators: List<CollaboratorModal> = emptyList()

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

        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .add(R.id.loading_container, LoadingApiFragment.newInstance())
                .commit()
        }

        viewModel = ViewModelProvider(this)[ManagementViewModel::class.java]

        viewModel.loadUserProfileData()

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        setupSearchListener()

        binding.loadingContainer.visibility = View.VISIBLE
        viewModel.loadCollaborators()
    }

    private fun setupRecyclerView() {
        if (!NetworkUtils.isInternetAvailable(requireContext())) {
            val intent = Intent(requireContext(), WifiErrorActivity::class.java)
            startActivity(intent)
            activity?.finish()
            return
        }

        collaboratorAdapter = CollaboratorAdapter { collaborator ->
            val action = ManagementFragmentDirections
                .actionManagementFragmentToEditCollaboratorFragment(collaborator)
            findNavController().navigate(action)
        }

        binding.recyclerViewEmployees.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = collaboratorAdapter
        }
    }

    private fun setupObservers() {
        viewModel.collaborators.observe(viewLifecycleOwner) { collaborators ->
            binding.loadingContainer.visibility = View.GONE
            allCollaborators = collaborators
            collaboratorAdapter.submitList(collaborators)
        }

        viewModel.userPhotoUrl.observe(viewLifecycleOwner) { photoUrl ->
            if (!photoUrl.isNullOrEmpty()) {
                Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.ic_profile_circle)
                    .error(R.drawable.ic_profile_circle)
                    .circleCrop()
                    .into(binding.included.imgPerfilToolbar)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            binding.loadingContainer.visibility = View.GONE
            val intent = Intent(requireContext(), InternalErrorActivity::class.java)
            errorActivityLauncher.launch(intent)
        }
    }

    private val errorActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        findNavController().navigateUp()
    }

    private fun setupSearchListener() {
        binding.inputSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun afterTextChanged(s: Editable?) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.trim()?.lowercase() ?: ""
                filterCollaborators(query)
            }
        })
    }

    private fun filterCollaborators(query: String) {
        if (query.isEmpty()) {
            collaboratorAdapter.submitList(allCollaborators)
        } else {
            val filtered = allCollaborators.filter { collaborator ->
                collaborator.name.lowercase().contains(query) ||
                        collaborator.email.lowercase().contains(query) ||
                        collaborator.name.lowercase().contains(query)
            }
            collaboratorAdapter.submitList(filtered)
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
