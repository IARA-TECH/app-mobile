package com.mobile.app_iara.ui.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.mobile.app_iara.databinding.FragmentHomeBinding
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.mobile.app_iara.R
import com.mobile.app_iara.ui.error.WifiErrorActivity
import com.mobile.app_iara.util.NetworkUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.mobile.app_iara.ui.abacus.onboarding.OnboardingActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var onboardingLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onboardingLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                navigateToSelectAbacus()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        viewModel.loadUserProfileData()

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

        binding.btnScan.setOnClickListener {
            val intent = Intent(requireContext(), OnboardingActivity::class.java)
            onboardingLauncher.launch(intent)
        }

        binding.cardAbacus.setOnClickListener {
            val prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val idDaFabricaAtual = prefs.getInt("key_factory_id", -1)

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

    private fun navigateToSelectAbacus() {
        val prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val idDaFabricaAtual = prefs.getInt("key_factory_id", -1)

        if (idDaFabricaAtual == -1) {
            Toast.makeText(requireContext(), "ID da fábrica não encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        val action = HomeFragmentDirections.actionHomeFragmentToSelectAbacus(
            factoryId = idDaFabricaAtual
        )

        if (isAdded) {
            findNavController().navigate(action)
        }
    }

    private fun setupObservers() {
        viewModel.userPhotoUrl.observe(viewLifecycleOwner) { photoUrl ->
            if (!photoUrl.isNullOrEmpty()) {
                Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.ic_profile_circle)
                    .error(R.drawable.ic_profile_circle)
                    .circleCrop()
                    .into(binding.included.imgPerfilToolbar)
            }
        }

        viewModel.userName.observe(viewLifecycleOwner) { name ->
            if (!name.isNullOrEmpty()) {
                binding.txtSaudacao.text = "Olá, $name"
            } else {
                binding.txtSaudacao.text = "Olá!"
            }
        }
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}