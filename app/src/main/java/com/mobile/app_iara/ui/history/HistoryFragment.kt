
package com.mobile.app_iara.ui.history

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
import com.mobile.app_iara.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBackButton()

        binding.included.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.included.iconNotificationToolbar.setOnClickListener {
            findNavController().navigate(R.id.action_historyFragment_to_notificationsFragment)
        }

        val historyData = createDummyData()
        setupRecyclerView(historyData)
    }

    private fun setupRecyclerView(historyList: List<AbacusHistory>) {
        val historyAdapter = AbacusHistoryAdapter(historyList)
        binding.historyRecyclerView.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun createDummyData(): List<AbacusHistory> {
        return listOf(
            AbacusHistory(
                urlPhoto = "https://picsum.photos/id/10/200/200",
                titulo = "Ábaco de Consumo de Ração",
                name = "Carlos Homero",
                approve = "Júlio Carneiro",
                timestamp = "23/10/2025 - 10:45"
            ),
            AbacusHistory(
                urlPhoto = "https://picsum.photos/id/20/200/200",
                titulo = "Ábaco de Conversão Alimentar (CA)",
                name = "Julia Maria",
                approve = "Carla de Jesus",
                timestamp = "23/10/2025 - 09:45"
            ),
            AbacusHistory(
                urlPhoto = "https://picsum.photos/id/30/200/200",
                titulo = "Ábaco de Índice de Eficiência Produtiva",
                name = "Julia Maria",
                approve = "Júlio Carneiro",
                timestamp = "26/10/2025 - 10:10"
            ),
            AbacusHistory(
                urlPhoto = "https://picsum.photos/id/40/200/200",
                titulo = "Ábaco de Espaço por Ave",
                name = "Lucas Fernando Souza",
                approve = "Carla de Jesus",
                timestamp = "28/10/2025 14:00"
            ),
            AbacusHistory(
                urlPhoto = "https://picsum.photos/id/10/200/200",
                titulo = "Ábaco de Consumo de Ração",
                name = "Carlos Homero",
                approve = "Lucas Fernando Souza",
                timestamp = "29/10/2025 - 10:27"
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
