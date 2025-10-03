package com.mobile.app_iara.ui.dashboard.dashboardcomparison

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.mobile.app_iara.R
import com.mobile.app_iara.databinding.FragmentDashboardComparisonBinding
import com.mobile.app_iara.databinding.ItemShiftQuantityBinding
import com.mobile.app_iara.ui.dashboard.ranking.RankingAdapter
import com.mobile.app_iara.ui.dashboard.ranking.RankingItem

class DashboardComparisonFragment : Fragment() {

    private var _binding: FragmentDashboardComparisonBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardComparisonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupGroupedBarChart()
        setupRankingList()
        setupQuantitySummary()

        binding.included.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupGroupedBarChart() {
        val numGroups = 6
        val labels = arrayOf("Jan", "Fev", "Mar", "Abr", "Mai", "Jun")

        val entries1 = ArrayList<BarEntry>().apply {
            add(BarEntry(0f, 60f)); add(BarEntry(1f, 30f)); add(BarEntry(2f, 55f))
            add(BarEntry(3f, 68f)); add(BarEntry(4f, 22f)); add(BarEntry(5f, 59f))
        }

        val entries2 = ArrayList<BarEntry>().apply {
            add(BarEntry(0f, 95f)); add(BarEntry(1f, 70f)); add(BarEntry(2f, 105f))
            add(BarEntry(3f, 38f)); add(BarEntry(4f, 0f)); add(BarEntry(5f, 89f))
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

    private fun setupRankingList() {
        val items = listOf(
            RankingItem(1, "Março", 145),
            RankingItem(2, "Outubro", 65),
            RankingItem(3, "Abril", 40)
        )
        binding.rvRankingMonths.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRankingMonths.adapter = RankingAdapter(items)
        binding.rvRankingMonths.isNestedScrollingEnabled = false
    }

    private fun setupQuantitySummary() {
        setupQuantityItem(binding.itemGranja, "Granja", 465, R.color.morning)
        setupQuantityItem(binding.itemFalhasTecnicas, "Falhas técnicas", 251, R.color.night)
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