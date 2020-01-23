package net.twisterrob.colorfilters.android

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class PreferencesActivityTest {

	@get:Rule val activityRule = ActivityTestRule(PreferencesActivity::class.java)

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
