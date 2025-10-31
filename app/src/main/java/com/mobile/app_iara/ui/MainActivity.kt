package com.mobile.app_iara.ui

import NotificationWorker
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.work.WorkManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mobile.app_iara.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit
import android.Manifest
import android.util.Log
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import com.mobile.app_iara.ui.notifications.KEY_NOTIFICATION_DESC
import com.mobile.app_iara.ui.notifications.KEY_NOTIFICATION_TITLE
import java.util.Calendar
import com.google.firebase.auth.FirebaseAuth
import com.mobile.app_iara.R
import com.mobile.app_iara.data.model.request.DailyActiveUsersRequest
import com.mobile.app_iara.data.model.request.EmailRequest
import com.mobile.app_iara.data.repository.DailyActiveUsersRepository
import com.mobile.app_iara.data.repository.UserAccessTypeRepository
import com.mobile.app_iara.data.repository.UserRepository
import com.mobile.app_iara.ui.notifications.ClearNotificationsWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val userRepository = UserRepository()
    private val accessTypeRepository = UserAccessTypeRepository()
    private val dailyActiveUsersRepository = DailyActiveUsersRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_management,
                R.id.navigation_profile
            )
        )

        val bottomNav: BottomNavigationView = binding.navView

        loadUserAccessAndConfigureNav(bottomNav)

        bottomNav.setOnItemSelectedListener { item ->
            if (navController.currentDestination?.id == item.itemId) {
                return@setOnItemSelectedListener false
            }

            val options = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setEnterAnim(0)
                .setExitAnim(0)
                .setPopEnterAnim(0)
                .setPopExitAnim(0)
                .setPopUpTo(navController.graph.findStartDestination().id, inclusive = false, saveState = true)
                .build()

            navController.navigate(item.itemId, null, options)
            true
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNav.menu.findItem(destination.id)?.isChecked = true
        }

        createNotificationChannel(this)
        askNotificationPermission()
    }

    private fun loadUserAccessAndConfigureNav(bottomNav: BottomNavigationView) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val email = FirebaseAuth.getInstance().currentUser?.email
                if (email.isNullOrEmpty()) {
                    withContext(Dispatchers.Main) {
                        bottomNav.menu.findItem(R.id.navigation_management)?.isVisible = false
                    }
                    return@launch
                }

                val userResponse = userRepository.getUserProfileByEmail(EmailRequest(email))
                if (!userResponse.isSuccessful || userResponse.body() == null) {
                    withContext(Dispatchers.Main) {
                        bottomNav.menu.findItem(R.id.navigation_management)?.isVisible = false
                    }
                    return@launch
                }

                val userId = userResponse.body()!!.id

                val accessResponse = accessTypeRepository.getUserAccessType(userId)

                registerDailyActiveUser(userId)

                withContext(Dispatchers.Main) {
                    if (accessResponse.isSuccessful && accessResponse.body() != null) {
                        val accessTypes = accessResponse.body()!!
                        val hasManagementAccess = accessTypes.any {
                            it.accessTypeName.equals("Administrador", ignoreCase = true) ||
                                    it.accessTypeName.equals("Supervisor", ignoreCase = true)
                        }

                        if (hasManagementAccess) {
                            for (i in 0 until bottomNav.menu.size()) {
                                bottomNav.menu.getItem(i).isVisible = true
                            }
                        } else {
                            bottomNav.menu.findItem(R.id.navigation_management)?.isVisible = false
                        }
                    } else {
                        bottomNav.menu.findItem(R.id.navigation_management)?.isVisible = false
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    bottomNav.menu.findItem(R.id.navigation_management)?.isVisible = false
                }
            }
        }
    }

    private suspend fun registerDailyActiveUser(userId: String) {
        try {
            val request = DailyActiveUsersRequest(userId = userId)
            dailyActiveUsersRepository.registerDailyActiveUsers(request)
            Log.i("MainActivity_DAU", "Usuário ativo registrado com sucesso.")
        } catch (e: Exception) {
            Log.e("MainActivity_DAU", "Falha ao registrar usuário ativo (não crítico)", e)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                scheduleAnalysisReminder()
            } else {

            }
        }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    scheduleAnalysisReminder()
                    scheduleGoodMorning()
                    scheduleAfternoon()
                    scheduleDailyClear()
                }

                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }

                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            scheduleAnalysisReminder()
            scheduleGoodMorning()
            scheduleAfternoon()
            scheduleDailyClear()
        }
    }

    private fun scheduleAnalysisReminder() {
        val inputData = Data.Builder()
            .putString(KEY_NOTIFICATION_TITLE, "Análises do dia prontas!")
            .putString(KEY_NOTIFICATION_DESC, "Não se esqueça de verificar os dados de hoje")
            .build()

        val currentTime = Calendar.getInstance()
        val scheduledTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (before(currentTime)) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        val initialDelay = scheduledTime.timeInMillis - currentTime.timeInMillis

        val dailyWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "dailyAnalysisReminderWork",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
    }

    private fun scheduleGoodMorning() {
        val inputData = Data.Builder()
            .putString(KEY_NOTIFICATION_TITLE, "Bom dia!")
            .putString(KEY_NOTIFICATION_DESC, "Pronto para fazer a contagem de hoje valer a pena?")
            .build()

        val currentTime = Calendar.getInstance()
        val scheduledTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (before(currentTime)) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        val initialDelay = scheduledTime.timeInMillis - currentTime.timeInMillis

        val dailyWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "goodMorningWork",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
    }

    private fun scheduleAfternoon() {
        val inputData = Data.Builder()
            .putString(KEY_NOTIFICATION_TITLE, "Uma pausa para os dados!")
            .putString(KEY_NOTIFICATION_DESC, "Recarregue as energias! A produção não para.")
            .build()

        val currentTime = Calendar.getInstance()
        val scheduledTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (before(currentTime)) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        val initialDelay = scheduledTime.timeInMillis - currentTime.timeInMillis

        val dailyWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "afternoonWork",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
    }

    private fun scheduleDailyClear() {
        val currentTime = Calendar.getInstance()

        val scheduledTime = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 1)
            set(Calendar.SECOND, 0)
        }

        val initialDelay = scheduledTime.timeInMillis - currentTime.timeInMillis

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val dailyClearRequest = PeriodicWorkRequestBuilder<ClearNotificationsWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "dailyNotificationClearWork",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyClearRequest
        )
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Lembretes Diários"
            val descriptionText = "Canal para enviar lembretes diários do app."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("DAILY_REMINDER_CHANNEL_ID", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}