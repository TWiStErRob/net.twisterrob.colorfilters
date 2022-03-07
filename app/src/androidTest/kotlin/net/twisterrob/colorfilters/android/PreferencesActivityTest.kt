package net.twisterrob.colorfilters.android

import androidx.test.filters.LargeTest
import de.mannodermaus.junit5.ActivityScenarioExtension
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@LargeTest
class PreferencesActivityTest {

	@RegisterExtension @JvmField
	val activityRule = ActivityScenarioExtension.launch<PreferencesActivity>()

	@Test fun opens() {
		val prefs = PreferencesActivityActor()

		prefs.assertDisplayed()
	}

	@Test fun opensAbout() {
		val prefs = PreferencesActivityActor()

		val about = prefs.openAbout()

		about.assertDisplayed()
	}

	@Disabled("Clear shared preferences before launching this.")
	@Test fun togglesExperimentalKeyboards() {
		val prefs = PreferencesActivityActor()
		prefs.assertExperimentalKeyboards(true)

		prefs.toggleExperimentalKeyboards()

		prefs.assertExperimentalKeyboards(false)
	}
}
