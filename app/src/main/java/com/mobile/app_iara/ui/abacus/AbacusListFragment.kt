package com.mobile.app_iara.ui.abacus

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.app_iara.R
import com.mobile.app_iara.data.repository.AbacusRepository
import com.mobile.app_iara.databinding.DialogDeleteConfirmationBinding
import com.mobile.app_iara.databinding.FragmentAbacusListBinding
import com.mobile.app_iara.ui.error.WifiErrorActivity
import com.mobile.app_iara.util.AbacusMapper
import com.mobile.app_iara.util.NetworkUtils
import kotlinx.coroutines.launch

class AbacusListFragment : Fragment() {

    private var _binding: FragmentAbacusListBinding? = null
    private val binding get() = _binding!!

    private lateinit var abacusAdapter: AbacusAdapter

    private val repository = AbacusRepository()
    private val args: AbacusListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAbacusListBinding.inflate(inflater, container, false)
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
        setupRecyclerView()

        val factoryId = args.factoryId
        loadAbacusData(factoryId)

        binding.included.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.included.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.ivAdd.setOnClickListener {
            findNavController().navigate(R.id.action_abacusListFragment_to_registerAbacusFragment)
        }

        binding.included.iconNotificationToolbar.setOnClickListener {
            findNavController().navigate(R.id.action_abacusListFragment_to_notificationsFragment)
        }
    }

    private fun setupRecyclerView() {
        binding.rvAbacusList.layoutManager = LinearLayoutManager(requireContext())

        abacusAdapter = AbacusAdapter(
            emptyList(),
            onDeleteClick = { abacusToDelete ->
                showDeleteConfirmationDialog(abacusToDelete)
            },
            onEditClick = { abacusToEdit ->
                val action = AbacusListFragmentDirections
                    .actionAbacusListFragmentToEditAbacusFragment(abacusToEdit.id)
                findNavController().navigate(action)
            }
        )
        binding.rvAbacusList.adapter = abacusAdapter
    }

    private fun showDeleteConfirmationDialog(abacus: Abacus) {
        val dialogBinding = DialogDeleteConfirmationBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.btnDeletarDialogDelete.setOnClickListener {
            performAbacusDeletion(abacus)
            dialog.dismiss()
        }

        dialogBinding.btnCancelarDialogDelete.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun performAbacusDeletion(abacus: Abacus) {
        lifecycleScope.launch {
            Toast.makeText(requireContext(), "Deletando ${abacus.title}...", Toast.LENGTH_SHORT).show()

            val result = repository.deleteAbacus(abacus.id)

            result.onSuccess {
                Toast.makeText(requireContext(), "${abacus.title} deletado com sucesso!", Toast.LENGTH_SHORT).show()
                loadAbacusData(args.factoryId)
            }

            result.onFailure { error ->
                Toast.makeText(requireContext(), "Erro ao deletar ${abacus.title}: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadAbacusData(factoryId: Int) {
        binding.rvAbacusList.visibility = View.GONE
        binding.textView36.visibility = View.GONE
        lifecycleScope.launch {
            val result = repository.getAbacusesByFactory(factoryId)

            result.onSuccess { abacusDataList ->
                val abacusUiList = AbacusMapper.mapApiListToUiList(abacusDataList)
                abacusAdapter.updateData(abacusUiList)

                if (abacusUiList.isEmpty()) {
                    binding.textView36.text = "Nenhum ábaco cadastrado."
                    binding.textView36.visibility = View.VISIBLE
                    binding.rvAbacusList.visibility = View.GONE
                } else {
                    binding.textView36.visibility = View.GONE
                    binding.rvAbacusList.visibility = View.VISIBLE
                }
            }

            result.onFailure { error ->
                Toast.makeText(requireContext(), "Erro ao carregar ábacos: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}