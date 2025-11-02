package com.mobile.app_iara.ui.dashboard.farmcondemnation

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
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.mobile.app_iara.R
import com.mobile.app_iara.data.model.response.FarmMonthlyEvolution
import com.mobile.app_iara.data.model.response.ReasonRankingItem
import com.mobile.app_iara.databinding.FragmentFarmCondemnationBinding
import com.mobile.app_iara.ui.dashboard.ranking.RankingAdapter
import com.mobile.app_iara.ui.dashboard.ranking.RankingItem
import com.mobile.app_iara.ui.error.WifiErrorActivity
import com.mobile.app_iara.util.NetworkUtils

class FarmCondemnationFragment : Fragment() {

    private var _binding: FragmentFarmCondemnationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FarmCondemnationViewModel by viewModels {
        FarmCondemnationViewModelFactory()
    }

    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFarmCondemnationBinding.inflate(inflater, container, false)
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
            viewModel.fetchFarmData(factoryId)
        } else {
            Toast.makeText(requireContext(), "Erro: ID da fábrica não encontrado.", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupCards(total: Int, rate: Float, comparison: Float) {
        binding.totalValue.text = total.toString()

        binding.averageValue.text = "${String.format("%.1f", rate).replace('.', ',')}%"

        val comparisonText: String
        val comparisonColor: Int

        when {
            comparison > 0 -> {
                comparisonText = "+${String.format("%.0f", comparison)}%"
                comparisonColor = ContextCompat.getColor(requireContext(), R.color.alertRed)
            }
            comparison < 0 -> {
                comparisonText = "${String.format("%.0f", comparison)}%"
                comparisonColor = ContextCompat.getColor(requireContext(), R.color.successGreen)
            }
            else -> {
                comparisonText = "0%"
                comparisonColor = ContextCompat.getColor(requireContext(), R.color.defaultText)
            }
        }

        binding.comparisonValue.text = comparisonText
        binding.comparisonValue.setTextColor(comparisonColor)
    }

    private fun setupClickListeners() {
        binding.included.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.included.iconNotificationToolbar.setOnClickListener {
            findNavController().navigate(R.id.action_farmCondemnationFragment_to_notificationsFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.farmData.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                setupCards(data.total, data.averageRate, data.previousComparison)
                setupRankingList(data.reasonRanking)
                setupHorizontalBarChart(data.monthlyEvolution)
            }
        }
    }

    private fun setupRankingList(rankingData: List<ReasonRankingItem>) {
        val rankingItems = rankingData.mapIndexed { index, item ->
            RankingItem(
                position = index + 1,
                description = item.reason,
                count = item.quantity
            )
        }

        binding.rvRanking.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = RankingAdapter(rankingItems)
            isNestedScrollingEnabled = false
        }
    }

    private fun setupHorizontalBarChart(evolutionData: FarmMonthlyEvolution) {
        val entries = ArrayList<BarEntry>()
        evolutionData.values.forEachIndexed { index, value ->
            entries.add(BarEntry(index.toFloat(), value))
        }
        val labels = evolutionData.periods

        val maxValue = evolutionData.values.maxOrNull() ?: 0f

        val dataSet = BarDataSet(entries, "Condenas").apply {
            val startColor = ContextCompat.getColor(requireContext(), R.color.gradientStartBlue)
            val endColor = ContextCompat.getColor(requireContext(), R.color.gradientEndBlue)
            setGradientColor(startColor, endColor)
            setDrawValues(false)
        }

        binding.horizontalBarChart.apply {
            renderer = RoundedHorizontalBarChartRenderer(this, this.animator, this.viewPortHandler, labels)

            data = BarData(dataSet)
            data.barWidth = 0.6f
            setFitBars(true)
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
            xAxis.isEnabled = false
            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.parseColor("#E0E0E0")
                setDrawAxisLine(true)
                axisMinimum = 0f
                axisMaximum = if (maxValue == 0f) 100f else maxValue * 1.2f
            }
            axisRight.isEnabled = false
            axisLeft.setInverted(true)

            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}