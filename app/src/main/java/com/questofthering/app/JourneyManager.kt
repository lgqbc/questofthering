package com.questofthering.app

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// DataStore extension
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "journey_data")

data class Location(
    val name: String,
    val description: String,
    val requiredSteps: Int
)

data class JourneyProgress(
    val totalSteps: Int,
    val currentLocation: Location,
    val progressPercent: Float
)

class JourneyManager(private val context: Context) {

    companion object {
        private val TOTAL_STEPS_KEY = intPreferencesKey("total_steps")
        private val CURRENT_LOCATION_INDEX_KEY = intPreferencesKey("current_location_index")

        // The Fellowship's journey route
        // These distances are approximate based on the journey in the books
        // We'll scale them down to be achievable for users
        private const val STEPS_PER_MILE = 2000 // Average steps per mile
        private const val SCALING_FACTOR = 10 // Make the journey 10x shorter for the app
    }

    private val locations = listOf(
        Location(
            "The Shire - Bag End",
            "Your journey begins at Bilbo's home in Hobbiton",
            0
        ),
        Location(
            "Bucklebury Ferry",
            "Crossing the Brandywine River to escape the Black Riders",
            (40 * STEPS_PER_MILE) / SCALING_FACTOR // ~40 miles from Hobbiton
        ),
        Location(
            "Bree",
            "The town where you meet Strider at the Prancing Pony",
            (80 * STEPS_PER_MILE) / SCALING_FACTOR
        ),
        Location(
            "Weathertop",
            "The ruins where Frodo is wounded by the Nazgûl",
            (120 * STEPS_PER_MILE) / SCALING_FACTOR
        ),
        Location(
            "Rivendell",
            "The house of Elrond, where the Fellowship is formed",
            (200 * STEPS_PER_MILE) / SCALING_FACTOR
        ),
        Location(
            "Moria - The Mines",
            "The ancient dwarven kingdom beneath the Misty Mountains",
            (400 * STEPS_PER_MILE) / SCALING_FACTOR
        ),
        Location(
            "Lothlórien",
            "The Golden Wood, realm of the Lady Galadriel",
            (480 * STEPS_PER_MILE) / SCALING_FACTOR
        ),
        Location(
            "Amon Hen",
            "Where the Fellowship breaks and Boromir falls",
            (580 * STEPS_PER_MILE) / SCALING_FACTOR
        ),
        Location(
            "Emyn Muil",
            "The rocky highlands where Frodo and Sam meet Gollum",
            (650 * STEPS_PER_MILE) / SCALING_FACTOR
        ),
        Location(
            "The Dead Marshes",
            "Treacherous swamps haunted by the dead",
            (750 * STEPS_PER_MILE) / SCALING_FACTOR
        ),
        Location(
            "The Black Gate",
            "The main entrance to Mordor - impassable",
            (850 * STEPS_PER_MILE) / SCALING_FACTOR
        ),
        Location(
            "Ithilien",
            "The fair land where Frodo meets Faramir",
            (950 * STEPS_PER_MILE) / SCALING_FACTOR
        ),
        Location(
            "Cirith Ungol",
            "The pass guarded by Shelob the spider",
            (1050 * STEPS_PER_MILE) / SCALING_FACTOR
        ),
        Location(
            "Plains of Gorgoroth",
            "The desolate wastes of Mordor",
            (1150 * STEPS_PER_MILE) / SCALING_FACTOR
        ),
        Location(
            "Mount Doom",
            "The final destination - the Crack of Doom",
            (1250 * STEPS_PER_MILE) / SCALING_FACTOR
        )
    )

    private var totalSteps: Int = 0
    private var currentLocationIndex: Int = 0

    suspend fun loadProgress() {
        val data = context.dataStore.data.first()
        totalSteps = data[TOTAL_STEPS_KEY] ?: 0
        currentLocationIndex = data[CURRENT_LOCATION_INDEX_KEY] ?: 0
    }

    suspend fun saveProgress() {
        context.dataStore.edit { preferences ->
            preferences[TOTAL_STEPS_KEY] = totalSteps
            preferences[CURRENT_LOCATION_INDEX_KEY] = currentLocationIndex
        }
    }

    suspend fun addSteps(steps: Int) {
        totalSteps = steps

        // Update current location based on steps
        for (i in locations.indices.reversed()) {
            if (totalSteps >= locations[i].requiredSteps) {
                currentLocationIndex = i
                break
            }
        }

        saveProgress()
    }

    fun getCurrentProgress(): JourneyProgress {
        val currentLocation = locations.getOrNull(currentLocationIndex) ?: locations.first()
        val totalJourneySteps = locations.last().requiredSteps
        val progress = if (totalJourneySteps > 0) {
            (totalSteps.toFloat() / totalJourneySteps).coerceIn(0f, 1f)
        } else {
            0f
        }

        return JourneyProgress(
            totalSteps = totalSteps,
            currentLocation = currentLocation,
            progressPercent = progress
        )
    }

    fun getNextLocation(): Location? {
        val nextIndex = currentLocationIndex + 1
        return locations.getOrNull(nextIndex)
    }

    fun getAllLocations(): List<Location> = locations
}
