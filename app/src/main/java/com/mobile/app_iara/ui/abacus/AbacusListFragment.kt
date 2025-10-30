package com.mobile.app_iara.ui.abacus

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.app_iara.R
import com.mobile.app_iara.databinding.FragmentAbacusListBinding
import com.mobile.app_iara.ui.error.WifiErrorActivity
import com.mobile.app_iara.util.NetworkUtils

class AbacusListFragment : Fragment() {

    private var _binding: FragmentAbacusListBinding? = null
    private val binding get() = _binding!!

    private lateinit var abacusAdapter: AbacusAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAbacusListBinding.inflate(inflater, container, false)
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

        setupRecyclerView()
        loadAbacusData()

        binding.included.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.ivAdd.setOnClickListener {
            findNavController().navigate(R.id.action_abacusListFragment_to_registerAbacusFragment)
        }

        binding.included.iconNotificationToolbar.setOnClickListener {
            findNavController().navigate(R.id.action_abacusListFragment_to_notificationsFragment)
        }
    }

    private fun setupRecyclerView() {
        binding.rvAbacusList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadAbacusData() {
        val mockData = listOf(
            Abacus(
                title = "Ábaco de Consumo de Ração",
                description = "Ábaco da área fria",
                lines = 12,
                columns = 2,
                imageUrls = listOf(
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b5/RomanAbacusRecon.jpg/250px-RomanAbacusRecon.jpg",
                    "https://fundamentalbrinquedos.com.br/wp-content/uploads/2019/06/1056.jpg",
                    "https://www.trabalhosescolares.net/wp-content/uploads/2014/05/abaco_matematica.jpg",
                    "https://upload.wikimedia.org/wikipedia/commons/e/ea/Boulier1.JPG"
                )
            ),
            Abacus(
                title = "Ábaco de Conversão Alimentar (CA)",
                description = "Ábaco da área fria",
                lines = 12,
                columns = 2,
                imageUrls = listOf(
                    "https://rihappy.vtexassets.com/arquivos/ids/278709/Abaco-Escolar-de-Madeira---Melissa-e-Doug.jpg?v=635472565012900000",
                    "https://acdn-us.mitiendanube.com/stores/335/332/products/abaco-tons-terrosos-madeira-21-7f7b2d22765f1b2c5d16763087429243-640-0.jpg",
                    "https://webpages.ciencias.ulisboa.pt/~ommartins/seminario/abaco/images/schoty.jpg",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Schoty_abacus.jpg/250px-Schoty_abacus.jpg"
                )
            )
        )

        abacusAdapter = AbacusAdapter(mockData)
        binding.rvAbacusList.adapter = abacusAdapter
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}