package com.mobile.app_iara.ui.abacus.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.app_iara.databinding.DialogAddColumnBinding
import com.mobile.app_iara.databinding.FragmentRegisterAbacusBinding
import com.mobile.app_iara.ui.error.WifiErrorActivity
import com.mobile.app_iara.util.NetworkUtils


class RegisterAbacusFragment : Fragment() {

    private var _binding: FragmentRegisterAbacusBinding? = null
    private val binding get() = _binding!!

    private val columnsList = mutableListOf<AbacusColumn>()
    private val linesList = mutableListOf<String>()

    private lateinit var columnsAdapter: ColumnsAdapter
    private lateinit var linesAdapter: LinesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterAbacusBinding.inflate(inflater, container, false)
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

        setupRecyclerViews()
        setupClickListeners()

        binding.included.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerViews() {
        columnsAdapter = ColumnsAdapter(columnsList) { position ->
            columnsList.removeAt(position)
            columnsAdapter.notifyItemRemoved(position)
            columnsAdapter.notifyItemRangeChanged(position, columnsList.size)
        }
        binding.rvColumns.layoutManager = LinearLayoutManager(requireContext())
        binding.rvColumns.adapter = columnsAdapter

        linesAdapter = LinesAdapter(linesList) { position ->
            linesList.removeAt(position)
            linesAdapter.notifyItemRemoved(position)
            linesAdapter.notifyItemRangeChanged(position, linesList.size)
        }
        binding.rvLines.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLines.adapter = linesAdapter
    }

    private fun setupClickListeners() {
        binding.btnAddColumn.setOnClickListener {
            showAddColumnDialog()
        }

        binding.tilLine.setEndIconOnClickListener {
            addLine()
        }

        binding.btnConfirm.setOnClickListener {
            collectDataAndRegister()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun addLine() {
        val lineName = binding.etLine.text.toString().trim()
        if (lineName.isNotEmpty()) {
            linesList.add(lineName)
            linesAdapter.notifyItemInserted(linesList.size - 1)
            binding.rvLines.scrollToPosition(linesList.size - 1)
            binding.etLine.text?.clear()
        } else {
            Toast.makeText(requireContext(), "O nome da linha não pode ser vazio.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAddColumnDialog() {
        val dialogBinding = DialogAddColumnBinding.inflate(LayoutInflater.from(requireContext()))
        val subColumnsInDialog = mutableListOf<SubColumn>()

        val subColumnsAdapter = SubColumnsAdapter(subColumnsInDialog) { position ->
            subColumnsInDialog.removeAt(position)
            dialogBinding.rvSubColumns.adapter?.notifyItemRemoved(position)
            dialogBinding.rvSubColumns.adapter?.notifyItemRangeChanged(position, subColumnsInDialog.size)
        }
        dialogBinding.rvSubColumns.layoutManager = LinearLayoutManager(requireContext())
        dialogBinding.rvSubColumns.adapter = subColumnsAdapter

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))

        dialogBinding.btnAddSubColumn.setOnClickListener {
            val color = dialogBinding.etSubColumnColor.text.toString().trim()
            val valueStr = dialogBinding.etSubColumnValue.text.toString().trim()

            if (color.isNotEmpty() && valueStr.isNotEmpty()) {
                val subColumn = SubColumn(color, valueStr.toInt())
                subColumnsInDialog.add(subColumn)
                subColumnsAdapter.notifyItemInserted(subColumnsInDialog.size - 1)
                dialogBinding.etSubColumnColor.text?.clear()
                dialogBinding.etSubColumnValue.text?.clear()
            } else {
                Toast.makeText(requireContext(), "Preencha cor e valor da sub-coluna.", Toast.LENGTH_SHORT).show()
            }
        }

        dialogBinding.btnDialogSave.setOnClickListener {
            val columnName = dialogBinding.etDialogColumnName.text.toString().trim()
            if (columnName.isNotEmpty() && subColumnsInDialog.isNotEmpty()) {
                val newColumn = AbacusColumn(columnName, subColumnsInDialog)
                columnsList.add(newColumn)
                columnsAdapter.notifyItemInserted(columnsList.size - 1)
                binding.rvColumns.scrollToPosition(columnsList.size - 1)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Preencha o nome e adicione ao menos uma sub-coluna.", Toast.LENGTH_SHORT).show()
            }
        }

        dialogBinding.btnDialogCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun collectDataAndRegister() {
        val abacusName = binding.etAbacusName.text.toString().trim()
        val abacusDescription = binding.etAbacusDescription.text.toString().trim()

        if (abacusName.isEmpty() || abacusDescription.isEmpty()) {
            binding.tilAbacusName.error = if (abacusName.isEmpty()) "Campo obrigatório" else null
            binding.tilAbacusDescription.error = if (abacusDescription.isEmpty()) "Campo obrigatório" else null
            Toast.makeText(requireContext(), "Preencha o nome e a descrição.", Toast.LENGTH_SHORT).show()
            return
        }

        if (columnsList.isEmpty() || linesList.isEmpty()) {
            Toast.makeText(requireContext(), "Adicione pelo menos uma coluna e uma linha.", Toast.LENGTH_SHORT).show()
            return
        }

        val newAbacus = Abacus(
            name = abacusName,
            description = abacusDescription,
            lines = linesList,
            columns = columnsList
        )

        Log.d("RegisterAbacus", "--- DADOS FINAIS DO ÁBACO ---")
        Log.d("RegisterAbacus", "Objeto Completo: $newAbacus")

        Toast.makeText(requireContext(), "Ábaco '${newAbacus.name}' pronto para ser enviado!", Toast.LENGTH_LONG).show()

        // TODO: Enviar o objeto 'newAbacus' para a API através do seu ViewModel

        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}