package com.mobile.app_iara.ui.dashboard.farmcondemnation

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.mobile.app_iara.R
import com.mobile.app_iara.databinding.FragmentFarmCondemnationBinding
import com.mobile.app_iara.ui.dashboard.ranking.RankingAdapter
import com.mobile.app_iara.ui.dashboard.ranking.RankingItem

class FarmCondemnationFragment : Fragment() {

    private var _binding: FragmentFarmCondemnationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFarmCondemnationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRankingList()
        setupHorizontalBarChart()

        binding.included.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRankingList() {
        val rankingItems = listOf(
            RankingItem(1, "Canibalismo", 12),
            RankingItem(2, "Escaldado vivo", 15),
            RankingItem(3, "Lesão de pele", 16),
            RankingItem(4, "Celulite", 18),
            RankingItem(5, "Lesão inflamatória", 20)
        )
        binding.rvRanking.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = RankingAdapter(rankingItems)
            isNestedScrollingEnabled = false
        }
    }

    private fun setupHorizontalBarChart() {
        val entries = ArrayList<BarEntry>().apply {
            add(BarEntry(0f, 60f)); add(BarEntry(1f, 55f)); add(BarEntry(2f, 45f))
            add(BarEntry(3f, 35f)); add(BarEntry(4f, 25f))
        }
        val labels = listOf("Fábrica A", "Fábrica B", "Fábrica C", "Fábrica D", "Fábrica E")

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
                axisMaximum = 60f
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