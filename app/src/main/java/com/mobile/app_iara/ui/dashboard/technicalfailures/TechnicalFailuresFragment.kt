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
import com.mobile.app_iara.databinding.FragmentTechnicalFailuresBinding // Importe a classe de binding gerada

class TechnicalFailuresFragment : Fragment() {

    // Variáveis para o View Binding
    private var _binding: FragmentTechnicalFailuresBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla o layout usando o View Binding
        _binding = FragmentTechnicalFailuresBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toda a lógica que manipula as views (botões, textos, etc.) vai aqui.
        setupLineChart()
        setupRankingList()
    }

    private fun setupLineChart() {
        val entries = ArrayList<Entry>()
        // Dados de exemplo para o gráfico
        entries.add(Entry(0f, 25f)) // Jan
        entries.add(Entry(1f, 28f)) // Fev
        entries.add(Entry(2f, 35f)) // Mar
        entries.add(Entry(3f, 30f)) // Abr
        entries.add(Entry(4f, 28f)) // Mai
        entries.add(Entry(5f, 29f)) // Jun
        entries.add(Entry(6f, 26f)) // Jul

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
            // NOVO: Desativa o círculo de destaque que aparece ao redor do ponto clicado
            highLightColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
        }

// NOVO: Lista de meses para o Eixo X
        val months = arrayOf("Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul")

        binding.lineChart.apply {
            data = LineData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false

            // NOVO: Desativa as interações de toque para evitar o comportamento de "bolinhas extras"
            isHighlightPerTapEnabled = false
            isHighlightPerDragEnabled = false

            // Configurações do Eixo Y (Esquerdo)
            axisLeft.apply {
                setDrawGridLines(false) // Remove as linhas de grade horizontais
                axisMinimum = 0f
                // Opcional: remover os rótulos do eixo Y se não forem necessários
                // isEnabled = false
            }

            // ALTERADO: Configurações do Eixo X (Inferior)
            xAxis.apply {
                isEnabled = true // Reativa o eixo X
                position = XAxis.XAxisPosition.BOTTOM // Posiciona os rótulos na parte inferior
                setDrawGridLines(false) // Remove as linhas de grade verticais
                granularity = 1f // Garante que cada ponto de dado tenha um rótulo
                valueFormatter = XAxisMonthFormatter(months) // Usa nosso formatador personalizado
                textColor = ContextCompat.getColor(requireContext(), R.color.lightText) // Cor do texto
            }

            // Opcional: Desativa zoom e arrastar para um gráfico mais estático
            isDragEnabled = false
            setScaleEnabled(false)

            invalidate() // Atualiza o gráfico
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
        // Limpa a referência ao binding para evitar vazamentos de memória
        _binding = null
    }

    class XAxisMonthFormatter(private val months: Array<String>) : ValueFormatter() {
        override fun getAxisLabel(
            value: Float,
            axis: com.github.mikephil.charting.components.AxisBase?
        ): String {
            val index = value.toInt()
            // Retorna o mês correspondente se o índice for válido, senão retorna uma string vazia
            return if (index >= 0 && index < months.size) {
                months[index]
            } else {
                ""
            }
        }
    }
}