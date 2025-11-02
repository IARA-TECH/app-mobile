package com.mobile.app_iara.ui.dashboard.dashboardcomparison

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.mobile.app_iara.R
import com.mobile.app_iara.data.model.response.MonthlyRanking
import com.mobile.app_iara.data.model.response.Totals
import com.mobile.app_iara.databinding.FragmentDashboardComparisonBinding
import com.mobile.app_iara.databinding.ItemShiftQuantityBinding
import com.mobile.app_iara.ui.dashboard.ranking.RankingAdapter
import com.mobile.app_iara.ui.dashboard.ranking.RankingItem
import com.mobile.app_iara.ui.error.WifiErrorActivity
import com.mobile.app_iara.util.NetworkUtils

class DashboardComparisonFragment : Fragment() {

    private var _binding: FragmentDashboardComparisonBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardComparisonViewModel by viewModels {
        DashboardComparisonViewModelFactory()
    }

    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardComparisonBinding.inflate(inflater, container, false)
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

        sharedPrefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        setupClickListeners()

        observeViewModel()

        val factoryId = sharedPrefs.getInt("key_factory_id", -1)
        if (factoryId != -1) {
            viewModel.fetchComparisonData(factoryId)
        } else {
            Toast.makeText(requireContext(), "Erro: ID da fábrica não encontrado.", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupClickListeners() {
        binding.included.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.included.iconNotificationToolbar.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardComparisonFragment_to_notificationsFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.comparisonData.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                setupGroupedBarChart(data.periods, data.technicalFailures, data.farmCondemnations)
                setupRankingList(data.monthlyRanking)
                setupQuantitySummary(data.totals)
            }
        }
    }

    private fun setupGroupedBarChart(periods: List<String>, failures: List<Float>, condemnations: List<Float>) {
        val numGroups = periods.size
        val labels = periods.toTypedArray()

        val entries1 = ArrayList<BarEntry>()
        failures.forEachIndexed { index, value ->
            entries1.add(BarEntry(index.toFloat(), value))
        }

        val entries2 = ArrayList<BarEntry>()
        condemnations.forEachIndexed { index, value ->
            entries2.add(BarEntry(index.toFloat(), value))
        }

        val dataSet1 = BarDataSet(entries1, "Falhas técnicas").apply {
            color = ContextCompat.getColor(requireContext(), R.color.night)
            setDrawValues(false)
        }

        val dataSet2 = BarDataSet(entries2, "Condenas pela granja").apply {
            color = ContextCompat.getColor(requireContext(), R.color.morning)
            setDrawValues(false)
        }

        val barData = BarData(dataSet1, dataSet2)
        binding.groupedBarChart.data = barData

        val groupSpace = 0.4f
        val barSpace = 0.05f
        val barWidth = 0.25f
        barData.barWidth = barWidth

        binding.groupedBarChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
            axisRight.isEnabled = false

            axisLeft.apply {
                setDrawGridLines(true)
                setDrawAxisLine(false)
                axisMinimum = 0f
                textColor = Color.GRAY
            }

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                axisMinimum = 0f
                axisMaximum = numGroups.toFloat()
                textColor = Color.GRAY
                setCenterAxisLabels(true)
                valueFormatter = XAxisMonthFormatter(labels)
            }

            groupBars(0f, groupSpace, barSpace)
            invalidate()
        }
    }

    private fun setupRankingList(rankingData: List<MonthlyRanking>) {
        val items = rankingData.mapIndexed { index, ranking ->
            RankingItem(index + 1, ranking.month, ranking.total)
        }

        binding.rvRankingMonths.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRankingMonths.adapter = RankingAdapter(items)
        binding.rvRankingMonths.isNestedScrollingEnabled = false
    }

    private fun setupQuantitySummary(totals: Totals) {
        setupQuantityItem(binding.itemGranja, "Granja", totals.totalFarmCondemnations, R.color.morning)
        setupQuantityItem(binding.itemFalhasTecnicas, "Falhas técnicas", totals.totalTechnicalFailures, R.color.night)
    }

    private fun setupQuantityItem(itemBinding: ItemShiftQuantityBinding, name: String, quantity: Int, colorRes: Int) {
        itemBinding.tvShiftName.text = name
        itemBinding.tvShiftQuantity.text = quantity.toString()
        itemBinding.viewColor.background.setTint(ContextCompat.getColor(requireContext(), colorRes))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class XAxisMonthFormatter(private val months: Array<String>) : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: com.github.mikephil.charting.components.AxisBase?): String {
            val index = value.toInt()
            return if (index >= 0 && index < months.size) months[index] else ""
        }
    }
}