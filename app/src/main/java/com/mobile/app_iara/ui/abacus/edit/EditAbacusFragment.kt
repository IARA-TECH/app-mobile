package com.mobile.app_iara.ui.abacus.edit

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.app_iara.R
import com.mobile.app_iara.data.model.AbacusData
import com.mobile.app_iara.data.model.request.AbacusCreateRequest
import com.mobile.app_iara.data.model.request.ColumnCreateRequest
import com.mobile.app_iara.data.model.request.LineCreateRequest
import com.mobile.app_iara.data.model.request.LineTypeCreateRequest
import com.mobile.app_iara.data.repository.AbacusRepository
import com.mobile.app_iara.databinding.DialogAddColumnBinding
import com.mobile.app_iara.databinding.FragmentEditAbacusBinding
import com.mobile.app_iara.ui.abacus.register.ColumnsAdapter
import com.mobile.app_iara.ui.abacus.register.LinesAdapter
import com.mobile.app_iara.util.DataUtil
import kotlinx.coroutines.launch

class EditAbacusFragment : Fragment() {

    private var _binding: FragmentEditAbacusBinding? = null
    private val binding get() = _binding!!

    private val args: EditAbacusFragmentArgs by navArgs()

    private val repository = AbacusRepository()

    private lateinit var linesAdapter: LinesAdapter
    private lateinit var columnsAdapter: ColumnsAdapter

    private val lineList = mutableListOf<LineCreateRequest>()
    private val columnList = mutableListOf<ColumnCreateRequest>()

    private var currentFactoryId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditAbacusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        currentFactoryId = prefs.getInt("key_factory_id", -1)

        if (currentFactoryId == -1) {
            Toast.makeText(requireContext(), "Erro: ID da fábrica não encontrado. Tente logar novamente.", Toast.LENGTH_LONG).show()
            findNavController().navigateUp()
            return
        }

        setupRecyclerViews()
        setupListeners()
        loadAbacusData()

        binding.includedEdit.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.includedEdit.iconNotificationToolbar.setOnClickListener {
            findNavController().navigate(R.id.action_editAbacusFragment_to_notificationsFragment)
        }
    }

    private fun setupRecyclerViews() {
        linesAdapter = LinesAdapter(lineList) { position ->
            lineList.removeAt(position)
            linesAdapter.notifyItemRemoved(position)
        }
        binding.rvLinesEdit.apply {
            adapter = linesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        columnsAdapter = ColumnsAdapter(columnList) { position ->
            columnList.removeAt(position)
            columnsAdapter.notifyItemRemoved(position)
        }
        binding.rvColumnsEdit.apply {
            adapter = columnsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupListeners() {
        binding.btnCancelEdit.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSaveEdit.setOnClickListener {
            saveChanges()
        }

        binding.tilLineEdit.setEndIconOnClickListener {
            val lineName = binding.etLineEdit.text.toString().trim()

            val selectedTypeName = when (binding.rgLineTypeEdit.checkedRadioButtonId) {
                binding.rbTypeGranjaEdit.id -> DataUtil.TYPE_CONDENA_GRANJA_NAME
                binding.rbTypeTecnicaEdit.id -> DataUtil.TYPE_FALHA_TECNICA_NAME
                else -> null
            }

            if (lineName.isEmpty()) {
                binding.etLineEdit.error = "Nome não pode ser vazio"
                return@setEndIconOnClickListener
            }

            if (selectedTypeName == null) {
                Toast.makeText(requireContext(), "Selecione um tipo para a linha.", Toast.LENGTH_SHORT).show()
                return@setEndIconOnClickListener
            }

            val newTypeObject = LineTypeCreateRequest(
                id = DataUtil.generateUuid(),
                name = selectedTypeName,
                createdAt = DataUtil.getCurrentIsoTimestamp()
            )

            val newLine = LineCreateRequest(name = lineName, lineType = newTypeObject)
            lineList.add(newLine)

            linesAdapter.notifyItemInserted(lineList.size - 1)
            binding.rvLinesEdit.scrollToPosition(lineList.size - 1)

            binding.etLineEdit.text = null
            binding.rgLineTypeEdit.clearCheck()
        }

        binding.btnAddColumnEdit.setOnClickListener {
            showAddColumnDialog()
        }
    }

    private fun showAddColumnDialog() {
        val dialogBinding = DialogAddColumnBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.btnDialogSave.setOnClickListener {
            val columnName = dialogBinding.etDialogColumnName.text.toString().trim()
            val color = dialogBinding.etSubColumnColor.text.toString().trim()
            val valueStr = dialogBinding.etSubColumnValue.text.toString().trim()

            if (columnName.isNotEmpty() && color.isNotEmpty() && valueStr.isNotEmpty()) {
                try {
                    val value = valueStr.toInt()
                    val newColumn = ColumnCreateRequest(name = columnName, color = color, value = value)

                    columnList.add(newColumn)
                    columnsAdapter.notifyItemInserted(columnList.size - 1)
                    binding.rvColumnsEdit.scrollToPosition(columnList.size - 1)
                    dialog.dismiss()

                } catch (e: NumberFormatException) {
                    Toast.makeText(
                        requireContext(),
                        "O valor deve ser um número.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Preencha o nome, cor e valor.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        dialogBinding.btnDialogCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun loadAbacusData() {
        lifecycleScope.launch {
            val result = repository.getAbacusesByFactory(currentFactoryId)

            result.onSuccess { abacusList ->
                val abacusToEdit = abacusList.find { it.id == args.abacusId }

                if (abacusToEdit != null) {
                    populateUi(abacusToEdit)
                } else {
                    Toast.makeText(requireContext(), "Ábaco não encontrado.", Toast.LENGTH_LONG).show()
                    findNavController().navigateUp()
                }
            }

            result.onFailure {
                Toast.makeText(requireContext(), "Erro ao carregar dados: ${it.message}", Toast.LENGTH_LONG).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun populateUi(abacus: AbacusData) {
        binding.etAbacusNameEdit.setText(abacus.name)
        binding.etAbacusDescriptionEdit.setText(abacus.description)

        val linesToDisplay = abacus.lines.map { lineApi ->
            val typeApi = lineApi.lineType
            val typeRequest = LineTypeCreateRequest(
                id = typeApi.id,
                name = typeApi.name,
                createdAt = typeApi.createdAt
            )
            LineCreateRequest(name = lineApi.name, lineType = typeRequest)
        }

        val columnsToDisplay =
            abacus.columns.map { ColumnCreateRequest(it.name, it.color, it.value) }

        lineList.clear()
        lineList.addAll(linesToDisplay)
        linesAdapter.notifyDataSetChanged()

        columnList.clear()
        columnList.addAll(columnsToDisplay)
        columnsAdapter.notifyDataSetChanged()
    }

    private fun saveChanges() {
        val name = binding.etAbacusNameEdit.text.toString().trim()
        val description = binding.etAbacusDescriptionEdit.text.toString().trim()

        if (name.isEmpty()) {
            binding.etAbacusNameEdit.error = "Nome é obrigatório"
            return
        }
        if (lineList.isEmpty()) {
            Toast.makeText(requireContext(), "Adicione pelo menos uma linha.", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (columnList.isEmpty()) {
            Toast.makeText(requireContext(), "Adicione pelo menos uma coluna.", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val updateRequest = AbacusCreateRequest(
            factoryId = currentFactoryId,
            name = name,
            description = description,
            lines = lineList,
            columns = columnList
        )

        lifecycleScope.launch {
            val result = repository.updateAbacus(args.abacusId, updateRequest)

            result.onSuccess {
                Toast.makeText(requireContext(), "Ábaco atualizado com sucesso!", Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigateUp()
            }

            result.onFailure {
                Toast.makeText(
                    requireContext(),
                    "Erro ao salvar: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}