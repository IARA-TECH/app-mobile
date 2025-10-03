package com.mobile.app_iara.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.mobile.app_iara.R
import com.mobile.app_iara.databinding.FragmentShiftComparisonBinding
import com.mobile.app_iara.databinding.ItemShiftQuantityBinding

class ShiftComparisonFragment : Fragment() {

    private var _binding: FragmentShiftComparisonBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentShiftComparisonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupQuantityList()
        setupMultiLineChart()
    }

    private fun setupQuantityList() {
        // Noturno
        setupQuantityItem(binding.itemNoturno, "Turno noturno", 45, R.color.night)
        // Vespertino
        setupQuantityItem(binding.itemVespertino, "Turno vespertino", 23, R.color.afternoon)
        // Matutino
        setupQuantityItem(binding.itemMatutino, "Turno matutino", 12, R.color.morning)
    }

    private fun setupQuantityItem(itemBinding: ItemShiftQuantityBinding, name: String, quantity: Int, colorRes: Int) {
        itemBinding.tvShiftName.text = name
        itemBinding.tvShiftQuantity.text = quantity.toString()
        itemBinding.viewColor.background.setTint(ContextCompat.getColor(requireContext(), colorRes))
    }

    private fun setupMultiLineChart() {
        val labels = listOf("Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez")

        // DADOS PARA CADA TURNO
        val entriesMatutino = ArrayList<Entry>().apply {
            add(Entry(0f, 2f)); add(Entry(1f, 22f)); add(Entry(2f, 10f)); add(Entry(3f, 50f))
            add(Entry(4f, 20f)); add(Entry(5f, 22f)); add(Entry(6f, 30f)); add(Entry(7f, 40f))
        }
        val entriesVespertino = ArrayList<Entry>().apply {
            add(Entry(0f, 5f)); add(Entry(1f, 10f)); add(Entry(2f, 30f)); add(Entry(3f, 22f))
            add(Entry(4f, 18f)); add(Entry(5f, 25f)); add(Entry(6f, 5f)); add(Entry(7f, 0f))
        }
        val entriesNoturno = ArrayList<Entry>().apply {
            add(Entry(0f, 10f)); add(Entry(1f, 5f)); add(Entry(2f, 25f)); add(Entry(3f, 15f))
            add(Entry(4f, 25f)); add(Entry(5f, 15f)); add(Entry(6f, 20f)); add(Entry(7f, 30f))
        }

        // CRIAR UM LineDataSet PARA CADA LINHA
        val dataSetMatutino = createDataSet(entriesMatutino, "Matutino", R.color.morning)
        val dataSetVespertino = createDataSet(entriesVespertino, "Vespertino", R.color.afternoon)
        val dataSetNoturno = createDataSet(entriesNoturno, "Noturno", R.color.night)

        // CONFIGURAÇÕES GERAIS DO GRÁFICO
        binding.lineChartShifts.apply {
            data = LineData(dataSetMatutino, dataSetVespertino, dataSetNoturno)
            description.isEnabled = false
            axisRight.isEnabled = false

            // ADICIONADO: Desativa TODAS as interações de toque (clique, zoom, arrastar)
            setTouchEnabled(false)

            // Eixo X
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                textColor = Color.GRAY
                valueFormatter = XAxisValueFormatter(labels)
                labelCount = labels.size
            }
            // Eixo Y
            axisLeft.apply {
                setDrawGridLines(false)
                textColor = Color.GRAY
                axisMinimum = 0f
            }
            // Legenda
            legend.apply {
                isEnabled = true
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
                form = Legend.LegendForm.CIRCLE
            }

            invalidate() // Atualiza o gráfico
        }
    }

    private fun createDataSet(entries: ArrayList<Entry>, label: String, colorRes: Int): LineDataSet {
        return LineDataSet(entries, label).apply {
            color = ContextCompat.getColor(requireContext(), colorRes)
            lineWidth = 2.5f
            setDrawCircles(false)
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            // ADICIONADO: Garante que a linha de destaque que aparece no clique seja invisível
            highLightColor = Color.TRANSPARENT
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Você pode reutilizar a classe XAxisValueFormatter da tela anterior
    class XAxisValueFormatter(private val labels: List<String>) : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: com.github.mikephil.charting.components.AxisBase?): String {
            val index = value.toInt()
            return if (index >= 0 && index < labels.size) labels[index] else ""
        }
    }
}