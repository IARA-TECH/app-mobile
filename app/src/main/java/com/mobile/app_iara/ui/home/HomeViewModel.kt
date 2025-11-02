package com.mobile.app_iara.ui.home

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mobile.app_iara.data.model.request.EmailRequest
import com.mobile.app_iara.data.repository.UserRepository
import kotlinx.coroutines.launch
import androidx.core.content.edit
import com.mobile.app_iara.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private data class LocalShift(
    val name: String,
    val startsAt: LocalTime,
    val endsAt: LocalTime,
    val background: Int
)

class HomeViewModel(private val application: Application) : AndroidViewModel(application) {

    private val repository = UserRepository()
    private val prefs = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _userPhotoUrl = MutableLiveData<String?>()
    val userPhotoUrl: LiveData<String?> = _userPhotoUrl

    private val _userName = MutableLiveData<String?>()
    val userName: LiveData<String?> = _userName

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var isNetworkCallDone = false

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _turnoName = MutableLiveData<String>()
    val turnoName: LiveData<String> = _turnoName

    private val _horario = MutableLiveData<String>()
    val horario: LiveData<String> = _horario

    private val _tempoRestante = MutableLiveData<String>()
    val tempoRestante: LiveData<String> = _tempoRestante

    private val _progressoTurno = MutableLiveData<Int>()
    val progressoTurno: LiveData<Int> = _progressoTurno

    private val _isShiftActive = MutableLiveData<Boolean>()
    val isShiftActive: LiveData<Boolean> = _isShiftActive

    private val _backgroundResource = MutableLiveData<Int>()
    val backgroundResource: LiveData<Int> = _backgroundResource

    private var timerJob: Job? = null
    private var shiftEndTime: OffsetDateTime? = null
    private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())

    private val allShifts = listOf(
        LocalShift(
            "Matutino",
            LocalTime.parse("07:00:00"),
            LocalTime.parse("12:00:00"),
            R.drawable.bg_morning_shift
        ),
        LocalShift(
            "Vespertino",
            LocalTime.parse("12:00:00"),
            LocalTime.parse("18:00:00"),
            R.drawable.bg_afternoon_shift
        ),
        LocalShift(
            "Noturno",
            LocalTime.parse("18:00:00"),
            LocalTime.MAX,
            R.drawable.bg_night_shift
        )
    )

    fun loadUserProfileData() {
        val cachedUrl = prefs.getString("user_photo_url", null)
        val cachedName = prefs.getString("user_name", null)

        _userPhotoUrl.postValue(cachedUrl)
        _userName.postValue(cachedName)

        if (isNetworkCallDone) return

        viewModelScope.launch {
            try {
                val email = FirebaseAuth.getInstance().currentUser?.email
                if (email.isNullOrEmpty()) {
                    _error.postValue("Usuário não encontrado.")
                    return@launch
                }

                val response = repository.getUserProfileByEmail(EmailRequest(email))
                if (response.isSuccessful && response.body() != null) {
                    val profile = response.body()!!
                    isNetworkCallDone = true

                    val newUrl = profile.userPhotoUrl
                    val newName = profile.name

                    if (newUrl != cachedUrl || newName != cachedName) {
                        _userPhotoUrl.postValue(newUrl)
                        _userName.postValue(newName)

                        prefs.edit {
                            putString("user_photo_url", newUrl)
                            putString("user_name", newName)
                        }
                    }
                } else {
                    _error.postValue("Falha ao carregar os dados do perfil.")
                }
            } catch (e: Exception) {
                _error.postValue("Erro de conexão ao carregar perfil: ${e.message}")
            }
        }
    }

    fun loadCurrentShift() {
        _isLoading.value = true
        stopTimer()

        val currentShift = findActiveShift()

        if (currentShift != null) {
            processStaticData(currentShift)

            val totalDuration = Duration.between(currentShift.startsAt, currentShift.endsAt).seconds
            startTimer(currentShift.endsAt, totalDuration)

            _backgroundResource.value = currentShift.background
            _isShiftActive.value = true
        } else {
            _turnoName.value = "Sem turno"
            _horario.value = "--:-- - --:--"
            _tempoRestante.value = "Fora de expediente"
            _backgroundResource.value = R.drawable.bg_none_shift
            _isShiftActive.value = false
            _progressoTurno.value = 0
        }
        _isLoading.value = false
    }

    private fun findActiveShift(): LocalShift? {
        val now = LocalTime.now()
        return allShifts.find { shift ->
            now.isAfter(shift.startsAt) && now.isBefore(shift.endsAt)
        }
    }

    private fun processStaticData(shift: LocalShift) {
        _turnoName.value = "Turno\n${shift.name}"
        _horario.value = "${shift.startsAt.format(timeFormatter)} - ${shift.endsAt.format(timeFormatter)}"
    }

    private fun startTimer(endTime: LocalTime, totalDurationInSeconds: Long) {
        try {
            val today = LocalDate.now()
            val endDateTime = LocalDateTime.of(today, endTime)
            shiftEndTime = endDateTime.atZone(ZoneId.systemDefault()).toOffsetDateTime()
        } catch (e: Exception) {
            _tempoRestante.value = "Erro no timer"
            return
        }

        timerJob = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                val now = OffsetDateTime.now()
                if (now.isAfter(shiftEndTime)) {
                    _tempoRestante.postValue("Turno finalizado")
                    _progressoTurno.postValue(0)
                    _isShiftActive.postValue(false)
                    stopTimer()
                } else {
                    val duration = Duration.between(now, shiftEndTime)
                    val hours = duration.toHours()
                    val minutes = duration.toMinutes() % 60
                    val seconds = duration.seconds % 60
                    _tempoRestante.postValue("Restam: \n${hours}h ${minutes}m ${seconds}s")

                    val remainingSeconds = duration.seconds
                    if (totalDurationInSeconds > 0) {
                        val progress = (100 * remainingSeconds / totalDurationInSeconds).toInt()
                        _progressoTurno.postValue(progress)
                    }
                }
                delay(1000)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}