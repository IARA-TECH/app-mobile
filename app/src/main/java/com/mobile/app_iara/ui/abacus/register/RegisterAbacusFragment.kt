//package com.mobile.app_iara.ui.abacus.register
//
//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.appcompat.app.AlertDialog
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.navigation.fragment.findNavController
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.mobile.app_iara.data.model.ColumnData
//import com.mobile.app_iara.data.model.request.LineCreateRequest
//import com.mobile.app_iara.data.repository.AbacusRepository
//import com.mobile.app_iara.databinding.DialogAddColumnBinding
//import com.mobile.app_iara.databinding.FragmentRegisterAbacusBinding
//import com.mobile.app_iara.ui.error.WifiErrorActivity
//import com.mobile.app_iara.util.NetworkUtils
//
//
//class RegisterAbacusFragment : Fragment() {
//
//    private var _binding: FragmentRegisterAbacusBinding? = null
//    private val binding get() = _binding!!
//
//    private val columnsList = mutableListOf<ColumnData>()
//    private val linesList = mutableListOf<LineCreateRequest>()
//
//    private lateinit var columnsAdapter: ColumnsAdapter
//    private lateinit var linesAdapter: LinesAdapter
//
//    private val repository = AbacusRepository()
//    private val viewModel: RegisterAbacusViewModel by viewModels {
//        RegisterAbacusViewModelFactory(repository)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentRegisterAbacusBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        if (!NetworkUtils.isInternetAvailable(requireContext())) {
//            val intent = Intent(requireContext(), WifiErrorActivity::class.java)
//            startActivity(intent)
//            activity?.finish()
//            return
//        }
//
//        setupRecyclerViews()
//        setupClickListeners()
//
//        binding.included.imgBack.setOnClickListener {
//            findNavController().navigateUp()
//        }
//    }
//
//    private fun setupRecyclerViews() {
//        columnsAdapter = ColumnsAdapter(columnsList) { position ->
//            columnsList.removeAt(position)
//            columnsAdapter.notifyItemRemoved(position)
//        }
//        binding.rvColumns.layoutManager = LinearLayoutManager(requireContext())
//        binding.rvColumns.adapter = columnsAdapter
//
//        linesAdapter = LinesAdapter(linesList) { position ->
//            linesList.removeAt(position)
//            linesAdapter.notifyItemRemoved(position)
//        }
//        binding.rvLines.layoutManager = LinearLayoutManager(requireContext())
//        binding.rvLines.adapter = linesAdapter
//    }
//
//    private fun setupClickListeners() {
//        binding.btnAddColumn.setOnClickListener {
//            showAddColumnDialog(
//        }
//
//        binding.tilLine.setEndIconOnClickListener {
//            addLine()
//        }
//
//        binding.btnConfirm.setOnClickListener {
//            collectDataAndRegister()
//        }
//
//        binding.btnCancel.setOnClickListener {
//            findNavController().popBackStack()
//        }
//    }
//
//    private fun addLine() {
//        val lineName = binding.etLine.text.toString().trim()
//        if (lineName.isNotEmpty()) {
//            val newLine = LineCreateRequest(name = lineName)
//            linesList.add(newLine)
//
//            linesAdapter.notifyItemInserted(linesList.size - 1)
//            binding.rvLines.scrollToPosition(linesList.size - 1)
//            binding.etLine.text?.clear()
//        } else {
//            Toast.makeText(requireContext(), "O nome da linha não pode ser vazio.", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun showAddColumnDialog() {
//        val dialogBinding = DialogAddColumnBinding.inflate(LayoutInflater.from(requireContext()))
//        val subColumnsInDialog = mutableListOf<SubColumn>()
//
//        val subColumnsAdapter = SubColumnsAdapter(subColumnsInDialog) { position ->
//            subColumnsInDialog.removeAt(position)
//            dialogBinding.rvSubColumns.adapter?.notifyItemRemoved(position)
//            dialogBinding.rvSubColumns.adapter?.notifyItemRangeChanged(position, subColumnsInDialog.size)
//        }
//        dialogBinding.rvSubColumns.layoutManager = LinearLayoutManager(requireContext())
//        dialogBinding.rvSubColumns.adapter = subColumnsAdapter
//
//        val dialog = AlertDialog.Builder(requireContext())
//            .setView(dialogBinding.root)
//            .setCancelable(false)
//            .create()
//
//        dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
//
//        dialogBinding.btnAddSubColumn.setOnClickListener {
//            val color = dialogBinding.etSubColumnColor.text.toString().trim()
//            val valueStr = dialogBinding.etSubColumnValue.text.toString().trim()
//
//            if (color.isNotEmpty() && valueStr.isNotEmpty()) {
//                val subColumn = SubColumn(color, valueStr.toInt())
//                subColumnsInDialog.add(subColumn)
//                subColumnsAdapter.notifyItemInserted(subColumnsInDialog.size - 1)
//                dialogBinding.etSubColumnColor.text?.clear()
//                dialogBinding.etSubColumnValue.text?.clear()
//            } else {
//                Toast.makeText(requireContext(), "Preencha cor e valor da sub-coluna.", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        dialogBinding.btnDialogSave.setOnClickListener {
//            val columnName = dialogBinding.etDialogColumnName.text.toString().trim()
//            if (columnName.isNotEmpty() && subColumnsInDialog.isNotEmpty()) {
//                val newColumn = AbacusColumn(columnName, subColumnsInDialog)
//                columnsList.add(newColumn)
//                columnsAdapter.notifyItemInserted(columnsList.size - 1)
//                binding.rvColumns.scrollToPosition(columnsList.size - 1)
//                dialog.dismiss()
//            } else {
//                Toast.makeText(requireContext(), "Preencha o nome e adicione ao menos uma sub-coluna.", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        dialogBinding.btnDialogCancel.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        dialog.show()
//    }
//
//    private fun collectDataAndRegister() {
//        val abacusName = binding.etAbacusName.text.toString().trim()
//        val abacusDescription = binding.etAbacusDescription.text.toString().trim()
//
//        if (abacusName.isEmpty() || abacusDescription.isEmpty()) {
//            binding.tilAbacusName.error = if (abacusName.isEmpty()) "Campo obrigatório" else null
//            binding.tilAbacusDescription.error = if (abacusDescription.isEmpty()) "Campo obrigatório" else null
//            Toast.makeText(requireContext(), "Preencha o nome e a descrição.", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        if (columnsList.isEmpty() || linesList.isEmpty()) {
//            Toast.makeText(requireContext(), "Adicione pelo menos uma coluna e uma linha.", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
//        val factoryId = prefs.getInt("key_factory_id", -1)
//
//        if (factoryId == -1) {
//            Toast.makeText(requireContext(), "Erro: ID da fábrica não encontrado. Tente logar novamente.", Toast.LENGTH_LONG).show()
//            return
//        }
//
//        val newAbacus = Abacus(
//            name = abacusName,
//            description = abacusDescription,
//            lines = linesList,
//            columns = columnsList,
//            factoryId = factoryId
//        )
//
//        binding.tilAbacusName.error = null
//        binding.tilAbacusDescription.error = null
//
//        Toast.makeText(requireContext(), "Enviando ábaco...", Toast.LENGTH_SHORT).show()
//
//        viewModel.registerAbacus(
//            abacus = newAbacus,
//            onSuccess = {
//                Toast.makeText(requireContext(), "Ábaco '${it.name}' criado com sucesso!", Toast.LENGTH_LONG).show()
//                findNavController().popBackStack()
//            },
//            onFailure = { error ->
//                Log.e("RegisterAbacus", "Erro ao registrar ábaco", error)
//                Toast.makeText(requireContext(), "Erro ao registrar: ${error.message}", Toast.LENGTH_LONG).show()
//            }
//        )
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}