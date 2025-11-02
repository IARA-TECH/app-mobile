package com.mobile.app_iara.ui.dashboard.technicalfailures

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.mobile.app_iara.R
import com.mobile.app_iara.data.model.response.EvolutionData
import com.mobile.app_iara.data.model.response.TechnicalRankingData
import com.mobile.app_iara.databinding.FragmentTechnicalFailuresBinding
import com.mobile.app_iara.ui.dashboard.ranking.RankingAdapter
import com.mobile.app_iara.ui.dashboard.ranking.RankingItem
import com.mobile.app_iara.ui.error.WifiErrorActivity
import com.mobile.app_iara.ui.status.LoadingApiFragment // NOVO: Import
import com.mobile.app_iara.util.NetworkUtils

class TechnicalFailuresFragment : Fragment() {

    private var _binding: FragmentTechnicalFailuresBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TechnicalFailuresViewModel by viewModels {
        TechnicalFailuresViewModelFactory()
    }

    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTechnicalFailuresBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // NOVO: Adiciona o fragment de loading
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .add(R.id.loading_container, LoadingApiFragment.newInstance())
                .commit()
        }

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
            viewModel.fetchTechnicalFailures(factoryId)
        } else {
            Toast.makeText(requireContext(), "Erro: ID da fábrica não encontrado.", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupCards(total: Int, rate: Double, comparison: Double) {
        binding.totalCondemnations.text = total.toString()

        binding.occurrenceRate.text = "${String.format("%.1f", rate).replace('.', ',')}%"

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

        binding.comparison.text = comparisonText
        binding.comparison.setTextColor(comparisonColor)
    }

    private fun setupClickListeners() {
        binding.included.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.included.iconNotificationToolbar.setOnClickListener {
            findNavController().navigate(R.id.action_technicalFailuresFragment_to_notificationsFragment)
        }
    }

    private fun observeViewModel() {
        // NOVO: Observador para o isLoading
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.loadingContainer.visibility = View.VISIBLE
            } else {
                binding.loadingContainer.visibility = View.GONE
            }
        }

        // NOVO: Observador para o erro
        viewModel.error.observe(viewLifecycleOwner) { error ->
            binding.loadingContainer.visibility = View.GONE // NOVO: Esconde em caso de erro
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.failuresData.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                setupCards(
                    data.total,
                    data.averageRate,
                    data.previousComparison
                )

                if (data.monthlyEvolution != null) {
                    setupLineChart(data.monthlyEvolution)
                }

                if (data.ranking != null) {
                    setupRankingList(data.ranking)
                }
            }
        }
    }

    private fun setupLineChart(evolutionData: EvolutionData) {
        val entries = ArrayList<Entry>()

        evolutionData.values.forEachIndexed { index, value ->
            entries.add(Entry(index.toFloat(), value))
        }

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

        val months = evolutionData.periods.toTypedArray()

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

    private fun setupRankingList(rankingData: List<TechnicalRankingData>) {
        val rankingItems = rankingData.mapIndexed { index, data ->
            RankingItem(
                position = index + 1,
                description = data.name,
                count = data.total
            )
        }

        binding.rvRanking.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRanking.adapter = RankingAdapter(rankingItems)
        binding.rvRanking.isNestedScrollingEnabled = false
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