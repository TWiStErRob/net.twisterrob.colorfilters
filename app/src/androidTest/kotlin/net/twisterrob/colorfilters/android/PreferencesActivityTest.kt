package net.twisterrob.colorfilters.android

import androidx.test.filters.LargeTest
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

@LargeTest
class PreferencesActivityTest {

	@Suppress("DEPRECATION")
	@get:Rule val activityRule = androidx.test.rule.ActivityTestRule(PreferencesActivity::class.java)

	@Test fun opens() {
		val prefs = PreferencesActivityActor()

		prefs.assertDisplayed()
	}

	@Test fun opensAbout() {
		val prefs = PreferencesActivityActor()

		val about = prefs.openAbout()

		about.assertDisplayed()
	}

	@Ignore("Clear shared preferences before launching this.")
	@Test fun togglesExperimentalKeyboards() {
		val prefs = PreferencesActivityActor()
		prefs.assertExperimentalKeyboards(true)

		prefs.toggleExperimentalKeyboards()

		prefs.assertExperimentalKeyboards(false)
	}
}
