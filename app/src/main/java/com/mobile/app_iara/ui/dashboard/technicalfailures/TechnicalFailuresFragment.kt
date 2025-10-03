package com.mobile.app_iara.ui.dashboard.technicalfailures

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.mobile.app_iara.R
import com.mobile.app_iara.databinding.FragmentTechnicalFailuresBinding

class TechnicalFailuresFragment : Fragment() {

    private var _binding: FragmentTechnicalFailuresBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTechnicalFailuresBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLineChart()
        setupRankingList()
    }

    private fun setupLineChart() {
        val entries = ArrayList<Entry>()
        entries.add(Entry(0f, 25f))
        entries.add(Entry(1f, 28f))
        entries.add(Entry(2f, 35f))
        entries.add(Entry(3f, 30f))
        entries.add(Entry(4f, 28f))
        entries.add(Entry(5f, 29f))
        entries.add(Entry(6f, 26f))

        val dataSet = LineDataSet(entries, "Falhas").apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            color = ContextCompat.getColor(requireContext(), R.color.primaryBlue)
            setCircleColor(ContextCompat.getColor(requireContext(), R.color.primaryBlue))
            lineWidth = 2.5f
            circleRadius = 4f
            setDrawCircleHole(false)
            setDrawValues(false)
            setDrawFilled(true)
            fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.shape_chart_gradient)
            highLightColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
        }

        val months = arrayOf("Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez")

        binding.lineChart.apply {
            data = LineData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false

            isHighlightPerTapEnabled = false
            isHighlightPerDragEnabled = false

            axisLeft.apply {
                setDrawGridLines(false)
                axisMinimum = 0f
            }

            xAxis.apply {
                isEnabled = true
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                valueFormatter = XAxisMonthFormatter(months)
                textColor = ContextCompat.getColor(requireContext(), R.color.lightText)
            }

            isDragEnabled = false
            setScaleEnabled(false)

            invalidate()
        }
    }

    private fun setupRankingList() {
        val rankingItems = listOf(
            RankingItem(1, "Erro na ventilação", 45),
            RankingItem(2, "Manejo incorreto da ração", 38),
            RankingItem(3, "Falha no ajuste de bebedouros", 30),
            RankingItem(4, "Descumprimento de biosseguridade", 25),
            RankingItem(5, "Manutenção preventiva incorreta", 12)
        )

        binding.rvRanking.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRanking.adapter = RankingAdapter(rankingItems)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class XAxisMonthFormatter(private val months: Array<String>) : ValueFormatter() {
        override fun getAxisLabel(
            value: Float,
            axis: com.github.mikephil.charting.components.AxisBase?
        ): String {
            val index = value.toInt()
            return if (index >= 0 && index < months.size) {
                months[index]
            } else {
                ""
            }
        }
    }
}