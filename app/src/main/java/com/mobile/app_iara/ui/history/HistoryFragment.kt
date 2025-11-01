package com.mobile.app_iara.ui.history

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.app_iara.R
import com.mobile.app_iara.data.model.AbacusPhotoData
import com.mobile.app_iara.data.repository.AbacusPhotoRepository
import com.mobile.app_iara.data.repository.AbacusRepository
import com.mobile.app_iara.data.repository.UserRepository
import com.mobile.app_iara.databinding.FragmentHistoryBinding
import com.mobile.app_iara.ui.error.WifiErrorActivity
import com.mobile.app_iara.ui.start.LoginActivity
import com.mobile.app_iara.util.NetworkUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val photoRepository = AbacusPhotoRepository()
    private val abacusRepository = AbacusRepository()
    private val userRepository = UserRepository()

    private lateinit var historyAdapter: AbacusHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
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
        setupListeners()
        loadValidatedPhotos()
    }

    private fun setupRecyclerView() {
        historyAdapter = AbacusHistoryAdapter(emptyList())
        binding.historyRecyclerView.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupListeners() {
        binding.included.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.included.iconNotificationToolbar.setOnClickListener {
            findNavController().navigate(R.id.action_historyFragment_to_notificationsFragment)
        }
    }

    private fun loadValidatedPhotos() {
        val prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val factoryId = prefs.getInt("key_factory_id", -1)

        if (factoryId == -1) {
            Toast.makeText(requireContext(), "Erro: ID da fábrica não encontrado. Tente logar novamente.", Toast.LENGTH_LONG).show()
            return
        }

        binding.historyRecyclerView.isVisible = false

        lifecycleScope.launch {
            val photosResultDeferred = async { photoRepository.getValidatedPhotosByFactory(factoryId) }
            val abacusesResultDeferred = async { abacusRepository.getAbacusesByFactory(factoryId) }
            val usersResultDeferred = async { userRepository.getAllUsers() }

            val photosResult = photosResultDeferred.await()
            val abacusesResult = abacusesResultDeferred.await()
            val usersResult = usersResultDeferred.await()

            if (photosResult.isSuccess && abacusesResult.isSuccess && usersResult.isSuccess) {

                val photoList = photosResult.getOrThrow()
                val abacusList = abacusesResult.getOrThrow()
                val userList = usersResult.getOrThrow()

                if (photoList.isEmpty()) {
                } else {
                    val abacusNameMap = abacusList.associateBy({ it.id }, { it.name })
                    val userNameMap = userList.associateBy({ it.id }, { it.name })

                    val historyList = photoList.map { mapApiToUi(it, abacusNameMap, userNameMap) }
                    historyAdapter.updateData(historyList)
                    binding.historyRecyclerView.isVisible = true
                }
            } else {
                val exception = photosResult.exceptionOrNull()
                    ?: abacusesResult.exceptionOrNull()
                    ?: usersResult.exceptionOrNull()

                val errorText = if (exception is SocketTimeoutException) {
                    "A API demorou muito para responder. Tente novamente."
                } else {
                    exception?.message ?: "Erro desconhecido ao carregar dados."
                }

                Toast.makeText(requireContext(), "Erro: $errorText", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun mapApiToUi(
        photo: AbacusPhotoData,
        abacusNameMap: Map<String, String>,
        userNameMap: Map<String, String>
    ): AbacusHistory {

        val abacusName = abacusNameMap[photo.abacusId] ?: photo.shiftName
        val takenByName = userNameMap[photo.takenBy] ?: photo.takenBy ?: "Usuário Desconhecido"

        val validatedByName = if (photo.validatedBy != null) {
            userNameMap[photo.validatedBy] ?: photo.validatedBy
        } else {
            "N/A"
        }

        return AbacusHistory(
            urlPhoto = photo.photoUrlBlob,
            titulo = abacusName,
            name = takenByName,
            approve = validatedByName,
            timestamp = formatTimestamp(photo.takenAt)
        )
    }

    private fun formatTimestamp(apiDate: String): String {
        val uiFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm", Locale.getDefault())

        return try {
            val localApiFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
            val localDateTime = LocalDateTime.parse(apiDate, localApiFormatter)
            localDateTime.format(uiFormatter)
        } catch (e: Exception) {
            try {
                val offsetApiFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
                val offsetDateTime = OffsetDateTime.parse(apiDate, offsetApiFormatter)
                offsetDateTime.format(uiFormatter)
            } catch (e2: Exception) {
                apiDate
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}