package com.mobile.app_iara.ui.spreadsheets

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.app_iara.R
import com.mobile.app_iara.databinding.FragmentSpreadSheetsBinding
import com.mobile.app_iara.ui.error.InternalErrorActivity
import java.text.Normalizer
import com.mobile.app_iara.ui.error.WifiErrorActivity
import com.mobile.app_iara.util.NetworkUtils
import com.mobile.app_iara.ui.status.LoadingApiFragment

class SpreadSheetsFragment : Fragment() {

    private var _binding: FragmentSpreadSheetsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SpreadsheetViewModel by viewModels()
    private lateinit var spreadSheetsAdapter: SpreadSheetsAdapter
    private var listaOriginalPlanilhas: List<SpreadSheets> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpreadSheetsBinding.inflate(inflater, container, false)
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

        setupRecyclerView()
        setupClickListeners()
        setupSearchFilter()

        observeViewModel()

        viewModel.fetchSpreadsheets()
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is SpreadsheetUiState.Loading -> {
                    binding.spreadSheetsRecyclerView.visibility = View.GONE
                    binding.loadingContainer.visibility = View.VISIBLE
                }
                is SpreadsheetUiState.Success -> {
                    binding.loadingContainer.visibility = View.GONE

                    if (state.spreadsheets.isEmpty()) {
                        binding.spreadSheetsRecyclerView.visibility = View.GONE
                    } else {
                        binding.spreadSheetsRecyclerView.visibility = View.VISIBLE
                        listaOriginalPlanilhas = state.spreadsheets
                        spreadSheetsAdapter.submitList(listaOriginalPlanilhas)
                    }
                }
                is SpreadsheetUiState.Error -> {
                    val intent = Intent(requireContext(), InternalErrorActivity::class.java)
                    errorActivityLauncher.launch(intent)
                    binding.loadingContainer.visibility = View.GONE
                    binding.spreadSheetsRecyclerView.visibility = View.GONE
                }
            }
        })
    }

    fun String.removeAccents(): String {
        val normalizedString = Normalizer.normalize(this, Normalizer.Form.NFD)

        val regex = "\\p{InCombiningDiacriticalMarks}+".toRegex()

        return regex.replace(normalizedString, "")
    }


    private fun setupRecyclerView() {
        spreadSheetsAdapter = SpreadSheetsAdapter()
        binding.spreadSheetsRecyclerView.apply {
            adapter = spreadSheetsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupClickListeners() {
        binding.included.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.included.iconNotificationToolbar.setOnClickListener {
            findNavController().navigate(R.id.action_spreadSheetsFragment_to_notificationsFragment)
        }
    }

    private fun setupSearchFilter() {
        binding.inputSearchSpreadsheet.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().removeAccents().lowercase()

                val filteredList = listaOriginalPlanilhas.filter { spreadsheet ->
                    spreadsheet.title.removeAccents().lowercase().contains(query)
                }
                spreadSheetsAdapter.submitList(filteredList)
            }
        })
    }

    private val errorActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}