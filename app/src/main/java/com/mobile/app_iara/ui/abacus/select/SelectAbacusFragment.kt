package com.mobile.app_iara.ui.abacus.select

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.app_iara.data.repository.AbacusRepository
import com.mobile.app_iara.databinding.FragmentAbacusListBinding
import com.mobile.app_iara.ui.camera.CameraActivity
import com.mobile.app_iara.ui.error.WifiErrorActivity
import com.mobile.app_iara.util.NetworkUtils
import com.mobile.app_iara.R
import com.mobile.app_iara.ui.status.LoadingApiFragment
import kotlinx.coroutines.launch

class SelectAbacusFragment : Fragment() {

    private var _binding: FragmentAbacusListBinding? = null
    private val binding get() = _binding!!

    private lateinit var selectAbacusAdapter: SelectAbacusAdapter
    private val repository = AbacusRepository()
    private val args: SelectAbacusFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAbacusListBinding.inflate(inflater, container, false)
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

        binding.ivAdd.visibility = View.GONE

        setupRecyclerView()

        val factoryId = args.factoryId
        loadAbacusData(factoryId)

        binding.included.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.included.iconNotificationToolbar.visibility = View.GONE
    }

    private fun setupRecyclerView() {
        binding.rvAbacusList.layoutManager = LinearLayoutManager(requireContext())

        selectAbacusAdapter = SelectAbacusAdapter(
            emptyList(),
            onAbacusSelected = { abacusData ->

                val columns = abacusData.columns

                val abacusColors = columns.joinToString(separator = ",") { it.color }
                val abacusValues = columns.joinToString(separator = ",") { it.value.toString() }

                val factoryId = args.factoryId
                val abacusId = abacusData.id

                val intent = Intent(requireContext(), CameraActivity::class.java).apply {
                    putExtra("ABACUS_COLORS", abacusColors)
                    putExtra("ABACUS_VALUES", abacusValues)

                    putExtra("FACTORY_ID", factoryId)
                    putExtra("ABACUS_ID", abacusId)
                }

                startActivity(intent)

            }
        )
        binding.rvAbacusList.adapter = selectAbacusAdapter
    }

    private fun loadAbacusData(factoryId: Int) {
        binding.rvAbacusList.visibility = View.GONE
        binding.textView36.visibility = View.GONE
        binding.loadingContainer.visibility = View.VISIBLE

        lifecycleScope.launch {
            val result = repository.getAbacusesByFactory(factoryId)
            binding.loadingContainer.visibility = View.GONE

            result.onSuccess { abacusDataList ->
                selectAbacusAdapter.updateData(abacusDataList)

                if (abacusDataList.isEmpty()) {
                    binding.textView36.text = "Nenhum ábaco cadastrado."
                    binding.textView36.visibility = View.VISIBLE
                    binding.rvAbacusList.visibility = View.GONE
                } else {
                    binding.textView36.visibility = View.GONE
                    binding.rvAbacusList.visibility = View.VISIBLE
                }
            }

            result.onFailure { error ->
                Toast.makeText(requireContext(), "Erro ao carregar ábacos: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}