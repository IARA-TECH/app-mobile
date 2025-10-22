package com.mobile.app_iara.ui.spreadsheets

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.app_iara.R
import com.mobile.app_iara.databinding.FragmentSpreadSheetsBinding
import com.mobile.app_iara.ui.error.WifiErrorActivity
import com.mobile.app_iara.utils.NetworkUtils

class SpreadSheetsFragment : Fragment() {

    private var _binding: FragmentSpreadSheetsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpreadSheetsBinding.inflate(inflater, container, false)
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

        setupBackButton()

        binding.included.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.included.iconNotificationToolbar.setOnClickListener {
            findNavController().navigate(R.id.action_spreadSheetsFragment_to_notificationsFragment)
        }

        val spreadSheetsData = createSpreadSheetsDummyData()
        setupRecyclerView(spreadSheetsData)

    }

    private fun setupRecyclerView(spreadSheetsList: List<SpreadSheets>) {
        val spreadSheetsAdapter = SpreadSheetsAdapter(spreadSheetsList)
        binding.spreadSheetsRecyclerView.apply {
            adapter = spreadSheetsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun createSpreadSheetsDummyData(): List<SpreadSheets> {
        return listOf(
            SpreadSheets(
                title = "Ábaco post-mortem - Lote A1",
                date = "02/10/2025",
                urlSpreadSheet = "https://docs.google.com/spreadsheets/d/1FEHHzfSMBH4TO-2r3LHzxdKigFU-KqMmSxAPXLwpZvk/edit?gid=126838007#gid=126838007"
            ),
            SpreadSheets(
                title = "Relatório de Perda - Silo 3",
                date = "29/09/2025",
                urlSpreadSheet = "https://docs.google.com/spreadsheets/d/1Fhduw2am7gmtoBr3-cTR4e4XEodSPdZYEQo9iHXfpYs/edit?gid=0#gid=0"
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

    private fun setupBackButton() {
        binding.included.imgBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}