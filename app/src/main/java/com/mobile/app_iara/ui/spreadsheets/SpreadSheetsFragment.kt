package com.mobile.app_iara.ui.spreadsheets

import android.content.Intent
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
import com.mobile.app_iara.databinding.FragmentSpreadSheetsBinding
import java.text.Normalizer
import com.mobile.app_iara.ui.error.WifiErrorActivity
import com.mobile.app_iara.util.NetworkUtils

class SpreadSheetsFragment : Fragment() {

    private var _binding: FragmentSpreadSheetsBinding? = null
    private val binding get() = _binding!!

    private lateinit var spreadSheetsAdapter: SpreadSheetsAdapter
    private val listaOriginalPlanilhas = createSpreadSheetsDummyData()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpreadSheetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        
        if (!NetworkUtils.isInternetAvailable(requireContext())) {
            val intent = Intent(requireContext(), WifiErrorActivity::class.java)
            startActivity(intent)
            activity?.finish()
            return
        }

        binding.inputSearchSpreadsheet.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().removeAccents().lowercase()

                val filteredList = listaOriginalPlanilhas.filter { spreadsheet ->
                    spreadsheet.title.removeAccents().lowercase().contains(query)
                }

                spreadSheetsAdapter.submitList(filteredList)
            }
        })

        setupClickListeners()
        spreadSheetsAdapter.submitList(listaOriginalPlanilhas)
    }

    fun String.removeAccents(): String {
        val normalizedString = Normalizer.normalize(this, Normalizer.Form.NFD)

        val regex = "\\p{InCombiningDiacriticalMarks}+".toRegex()

        return regex.replace(normalizedString, "")
    }


    private fun setupRecyclerView() {
        spreadSheetsAdapter = SpreadSheetsAdapter()
        binding.spreadSheetsRecyclerView.apply {
            adapter = spreadSheetsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupClickListeners() {
        binding.included.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.included.iconNotificationToolbar.setOnClickListener {
            findNavController().navigate(R.id.action_spreadSheetsFragment_to_notificationsFragment)
        }
    }

    private fun createSpreadSheetsDummyData(): List<SpreadSheets> {
        return listOf(
            SpreadSheets(
                title = "Ábaco post-mortem - Lote A1",
                date = "02/10/2025",
                urlSpreadSheet = "url example"
            ),
            SpreadSheets(
                title = "Relatório de Perda - Silo 3",
                date = "29/09/2025",
                urlSpreadSheet = "url example"
            ),
            SpreadSheets(
                title = "Análise de Mortalidade - Aviário 5",
                date = "25/09/2025",
                urlSpreadSheet = "url example"
            ),
            SpreadSheets(
                title = "Inspeção de Qualidade - Lote B7",
                date = "18/09/2025",
                urlSpreadSheet = "url example"
            ),
            SpreadSheets(
                title = "Relatório de Desempenho - Setor Norte",
                date = "15/09/2025",
                urlSpreadSheet = "url example"
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}