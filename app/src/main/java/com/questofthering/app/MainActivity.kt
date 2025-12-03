package com.questofthering.app

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.questofthering.app.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private lateinit var journeyManager: JourneyManager

    private var initialStepCount: Int = 0
    private var currentStepCount: Int = 0
    private var hasInitialCount = false

    // Permission launcher for Android 10+
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            initializeStepCounter()
        } else {
            Toast.makeText(
                this,
                "Step counter permission is required for this app",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize journey manager
        journeyManager = JourneyManager(this)

        // Setup sensor manager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepCounterSensor == null) {
            Toast.makeText(
                this,
                "No step counter sensor found on this device",
                Toast.LENGTH_LONG
            ).show()
        }

        // Check and request permission
        checkStepCounterPermission()

        // Load journey data
        lifecycleScope.launch {
            journeyManager.loadProgress()
            updateUI()
        }
    }

    private fun checkStepCounterPermission() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                // Android 10+ requires ACTIVITY_RECOGNITION permission
                when {
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACTIVITY_RECOGNITION
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        initializeStepCounter()
                    }
                    else -> {
                        requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                    }
                }
            }
            else -> {
                // Below Android 10, no permission needed
                initializeStepCounter()
            }
        }
    }

    private fun initializeStepCounter() {
        stepCounterSensor?.let { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                if (!hasInitialCount) {
                    initialStepCount = it.values[0].toInt()
                    hasInitialCount = true
                }

                currentStepCount = it.values[0].toInt() - initialStepCount

                // Update journey progress
                lifecycleScope.launch {
                    journeyManager.addSteps(currentStepCount)
                    updateUI()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for step counter
    }

    private fun updateUI() {
        val progress = journeyManager.getCurrentProgress()

        binding.stepsToday.text = currentStepCount.toString()
        binding.totalSteps.text = progress.totalSteps.toString()
        binding.currentLocationText.text = progress.currentLocation.name

        val progressPercent = (progress.progressPercent * 100).toInt()
        binding.journeyProgress.progress = progressPercent
        binding.progressText.text = "$progressPercent% of the journey"

        // Update map view
        binding.mapView.updatePosition(progress.progressPercent)
    }

    override fun onResume() {
        super.onResume()
        stepCounterSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)

        // Save progress
        lifecycleScope.launch {
            journeyManager.saveProgress()
        }
    }
}
