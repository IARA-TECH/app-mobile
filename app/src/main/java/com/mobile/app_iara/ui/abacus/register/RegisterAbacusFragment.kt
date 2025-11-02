package com.mobile.app_iara.ui.abacus.register

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.app_iara.R
import com.mobile.app_iara.data.model.request.AbacusCreateRequest
import com.mobile.app_iara.data.model.request.ColumnCreateRequest
import com.mobile.app_iara.data.model.request.LineCreateRequest
import com.mobile.app_iara.data.model.request.LineTypeCreateRequest
import com.mobile.app_iara.data.repository.AbacusRepository
import com.mobile.app_iara.databinding.DialogAddColumnBinding
import com.mobile.app_iara.databinding.FragmentRegisterAbacusBinding
import com.mobile.app_iara.ui.error.WifiErrorActivity
import com.mobile.app_iara.util.DataUtil
import com.mobile.app_iara.util.NetworkUtils


class RegisterAbacusFragment : Fragment() {

    private var _binding: FragmentRegisterAbacusBinding? = null
    private val binding get() = _binding!!

    private val columnsList = mutableListOf<ColumnCreateRequest>()
    private val linesList = mutableListOf<LineCreateRequest>()

    private lateinit var columnsAdapter: ColumnsAdapter
    private lateinit var linesAdapter: LinesAdapter

    private val repository = AbacusRepository()
    private val viewModel: RegisterAbacusViewModel by viewModels {
        RegisterAbacusViewModelFactory(repository)
    }

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

        binding.included.iconNotificationToolbar.setOnClickListener {
            findNavController().navigate(R.id.action_registerAbacusFragment_to_notificationsFragment)
        }
    }

    private fun setupRecyclerViews() {
        columnsAdapter = ColumnsAdapter(columnsList) { position ->
            columnsList.removeAt(position)
            columnsAdapter.notifyItemRemoved(position)
        }
        binding.rvColumns.layoutManager = LinearLayoutManager(requireContext())
        binding.rvColumns.adapter = columnsAdapter

        linesAdapter = LinesAdapter(linesList) { position ->
            linesList.removeAt(position)
            linesAdapter.notifyItemRemoved(position)
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
            binding.mensagemCredenciais.visibility = View.GONE
            findNavController().popBackStack()
        }
    }

    private fun addLine() {
        val lineName = binding.etLine.text.toString().trim()

        val selectedTypeName = when (binding.rgLineType.checkedRadioButtonId) {
            binding.rbTypeGranja.id -> DataUtil.TYPE_CONDENA_GRANJA_NAME
            binding.rbTypeTecnica.id -> DataUtil.TYPE_FALHA_TECNICA_NAME
            else -> null
        }

        if (lineName.isEmpty()) {
            Toast.makeText(requireContext(), "O nome da linha não pode ser vazio.", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedTypeName == null) {
            Toast.makeText(requireContext(), "Selecione um tipo para a linha.", Toast.LENGTH_SHORT).show()
            return
        }

        val newTypeObject = LineTypeCreateRequest(
            id = DataUtil.generateUuid(),
            name = selectedTypeName,
            createdAt = DataUtil.getCurrentIsoTimestamp()
        )

        val newLine = LineCreateRequest(name = lineName, lineType = newTypeObject)
        linesList.add(newLine)

        linesAdapter.notifyItemInserted(linesList.size - 1)
        binding.rvLines.scrollToPosition(linesList.size - 1)

        binding.etLine.text?.clear()
        binding.rgLineType.clearCheck()
    }

    private fun showAddColumnDialog() {
        val dialogBinding = DialogAddColumnBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))

        dialogBinding.btnDialogSave.setOnClickListener {
            val columnName = dialogBinding.etDialogColumnName.text.toString().trim()
            val color = dialogBinding.etSubColumnColor.text.toString().trim()
            val valueStr = dialogBinding.etSubColumnValue.text.toString().trim()

            if (columnName.isNotEmpty() && color.isNotEmpty() && valueStr.isNotEmpty()) {
                try {
                    val value = valueStr.toInt()
                    val newColumn = ColumnCreateRequest(name = columnName, color = color, value = value)

                    columnsList.add(newColumn)
                    columnsAdapter.notifyItemInserted(columnsList.size - 1)
                    binding.rvColumns.scrollToPosition(columnsList.size - 1)
                    dialog.dismiss()

                } catch (e: NumberFormatException) {
                    Toast.makeText(requireContext(), "O valor deve ser um número.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Preencha o nome, cor e valor.", Toast.LENGTH_SHORT).show()
            }
        }

        dialogBinding.btnDialogCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun collectDataAndRegister() {
        binding.mensagemCredenciais.visibility = View.GONE
        val abacusName = binding.etAbacusName.text.toString().trim()
        val abacusDescription = binding.etAbacusDescription.text.toString().trim()

        if (abacusName.isEmpty() || abacusDescription.isEmpty()) {
            binding.mensagemCredenciais.visibility = View.VISIBLE
            return
        }

        if (columnsList.isEmpty() || linesList.isEmpty()) {
            binding.mensagemCredenciais.visibility = View.VISIBLE
            return
        }

        val prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val factoryId = prefs.getInt("key_factory_id", -1)

        if (factoryId == -1) {
            Toast.makeText(requireContext(), "Erro: ID da fábrica não encontrado. Tente logar novamente.", Toast.LENGTH_LONG).show()
            return
        }

        val newAbacus = AbacusCreateRequest(
            name = abacusName,
            description = abacusDescription,
            lines = linesList,
            columns = columnsList,
            factoryId = factoryId
        )

        binding.tilAbacusName.error = null
        binding.tilAbacusDescription.error = null

        viewModel.registerAbacus(
            abacus = newAbacus,
            onSuccess = { abacusData ->
                showSuccessDialog()
            },
            onFailure = { error ->
                showErrorDialog()
            }
        )
    }

    private fun showSuccessDialog() {
        val successSheet = RegisterAbacusSuccess {
            findNavController().popBackStack()
        }
        successSheet.isCancelable = false
        successSheet.show(childFragmentManager, "RegisterSuccessSheet")
    }

    private fun showErrorDialog() {
        val errorSheet = RegisterAbacusError()
        errorSheet.show(childFragmentManager, "RegisterErrorSheet")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}