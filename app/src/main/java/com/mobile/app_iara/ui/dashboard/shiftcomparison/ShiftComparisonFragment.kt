package com.mobile.app_iara.ui.dashboard.shiftcomparison

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
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.mobile.app_iara.R
import com.mobile.app_iara.data.model.response.MonthlyEvolution
import com.mobile.app_iara.data.model.response.QuantityPerShift
import com.mobile.app_iara.databinding.FragmentShiftComparisonBinding
import com.mobile.app_iara.databinding.ItemShiftQuantityBinding
import com.mobile.app_iara.ui.error.WifiErrorActivity
import com.mobile.app_iara.ui.status.LoadingApiFragment
import com.mobile.app_iara.util.NetworkUtils

class ShiftComparisonFragment : Fragment() {

    private var _binding: FragmentShiftComparisonBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShiftComparisonViewModel by viewModels {
        ShiftComparisonViewModelFactory()
    }

    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentShiftComparisonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            viewModel.fetchShiftData(factoryId)
        } else {
            Toast.makeText(requireContext(), "Erro: ID da fábrica não encontrado.", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupClickListeners() {
        binding.included.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.included.iconNotificationToolbar.setOnClickListener {
            findNavController().navigate(R.id.action_shiftComparisonFragment_to_notificationsFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.loadingContainer.visibility = View.VISIBLE
            } else {
                binding.loadingContainer.visibility = View.GONE
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            binding.loadingContainer.visibility = View.GONE
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.shiftData.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                setupQuantityList(data.quantityPerShift)
                setupMultiLineChart(data.monthlyEvolution)
            }
        }
    }

    private fun setupQuantityList(quantityData: List<QuantityPerShift>) {
        val nightQuantity = quantityData.find { it.shift == "Night" }?.quantity ?: 0
        val afternoonQuantity = quantityData.find { it.shift == "Afternoon" }?.quantity ?: 0
        val morningQuantity = quantityData.find { it.shift == "Morning" }?.quantity ?: 0

        setupQuantityItem(binding.itemNoturno, "Turno noturno", nightQuantity, R.color.night)
        setupQuantityItem(binding.itemVespertino, "Turno vespertino", afternoonQuantity, R.color.afternoon)
        setupQuantityItem(binding.itemMatutino, "Turno matutino", morningQuantity, R.color.morning)
    }

    private fun setupQuantityItem(itemBinding: ItemShiftQuantityBinding, name: String, quantity: Int, colorRes: Int) {
        itemBinding.tvShiftName.text = name
        itemBinding.tvShiftQuantity.text = quantity.toString()
        itemBinding.viewColor.background.setTint(ContextCompat.getColor(requireContext(), colorRes))
    }

    private fun setupMultiLineChart(evolutionData: MonthlyEvolution) {
        val labels = evolutionData.periods

        val entriesMatutino = ArrayList<Entry>()
        evolutionData.morning.forEachIndexed { index, value ->
            entriesMatutino.add(Entry(index.toFloat(), value))
        }

        val entriesVespertino = ArrayList<Entry>()
        evolutionData.afternoon.forEachIndexed { index, value ->
            entriesVespertino.add(Entry(index.toFloat(), value))
        }

        val entriesNoturno = ArrayList<Entry>()
        evolutionData.night.forEachIndexed { index, value ->
            entriesNoturno.add(Entry(index.toFloat(), value))
        }

        val dataSetMatutino = createDataSet(entriesMatutino, "Matutino", R.color.morning)
        val dataSetVespertino = createDataSet(entriesVespertino, "Vespertino", R.color.afternoon)
        val dataSetNoturno = createDataSet(entriesNoturno, "Noturno", R.color.night)

        binding.lineChartShifts.apply {
            data = LineData(dataSetMatutino, dataSetVespertino, dataSetNoturno)
            description.isEnabled = false
            axisRight.isEnabled = false
            setTouchEnabled(false)
            extraBottomOffset = 20f

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                textColor = Color.GRAY
                valueFormatter = XAxisValueFormatter(labels)
                granularity = 1f
            }
            axisLeft.apply {
                setDrawGridLines(false)
                textColor = Color.GRAY
                axisMinimum = 0f
            }
            legend.apply {
                isEnabled = true
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
                form = Legend.LegendForm.CIRCLE
            }

            invalidate()
        }
    }

    private fun createDataSet(entries: ArrayList<Entry>, label: String, colorRes: Int): LineDataSet {
        return LineDataSet(entries, label).apply {
            color = ContextCompat.getColor(requireContext(), colorRes)
            lineWidth = 2.5f
            setDrawCircles(false)
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            highLightColor = Color.TRANSPARENT
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class XAxisValueFormatter(private val labels: List<String>) : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: com.github.mikephil.charting.components.AxisBase?): String {
            val index = value.toInt()
            return if (index >= 0 && index < labels.size) labels[index] else ""
        }
    }
}
