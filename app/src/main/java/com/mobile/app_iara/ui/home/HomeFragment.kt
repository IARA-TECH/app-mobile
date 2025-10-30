package com.mobile.app_iara.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobile.app_iara.databinding.FragmentHomeBinding
import androidx.navigation.fragment.findNavController
import com.mobile.app_iara.R
import com.mobile.app_iara.ui.error.WifiErrorActivity
import com.mobile.app_iara.util.NetworkUtils
import com.mobile.app_iara.ui.start.LoginActivity.Companion.KEY_FACTORY_ID
import com.mobile.app_iara.ui.start.LoginActivity.Companion.PREFS_NAME
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.mobile.app_iara.data.remote.RetrofitClient
import com.mobile.app_iara.data.repository.ShiftRepository

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
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

        viewModel.loadCurrentShift()

        viewModel.isShiftActive.observe(viewLifecycleOwner) { isActive ->
            val visibility = if (isActive) View.VISIBLE else View.GONE
            binding.progressCircular.visibility = visibility
            binding.txtTempoRestante.visibility = visibility
        }

        viewModel.progressoTurno.observe(viewLifecycleOwner) { progress ->
            binding.progressCircular.progress = progress
        }

        viewModel.turnoName.observe(viewLifecycleOwner) { name ->
            binding.txtTurno.text = name
        }

        viewModel.horario.observe(viewLifecycleOwner) { horario ->
            binding.txtHorario.text = horario
        }

        viewModel.tempoRestante.observe(viewLifecycleOwner) { tempo ->
            binding.txtTempoRestante.text = tempo
        }

        viewModel.backgroundResource.observe(viewLifecycleOwner) { resourceId ->
            binding.bgPaisagem.visibility = View.VISIBLE
            binding.bgPaisagem.setImageResource(resourceId)
        }

        binding.cardSpreadSheets.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_spreadSheets)
        }

        binding.cardHistory.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_history)
        }

        binding.cardAbacus.setOnClickListener {
            val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val idDaFabricaAtual = prefs.getInt(KEY_FACTORY_ID, -1)

            if (idDaFabricaAtual != -1) {
                val action = HomeFragmentDirections.actionHomeFragmentToAbacusList(idDaFabricaAtual)
                findNavController().navigate(action)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Erro: ID da fábrica não encontrado. Tente logar novamente.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.cardChat.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_chatFragment)
        }
        binding.included.imgPerfilToolbar.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
        binding.included.iconNotificationToolbar.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_notificationsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}