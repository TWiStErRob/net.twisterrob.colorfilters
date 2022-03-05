package net.twisterrob.colorfilters.android

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

	@Suppress("DEPRECATION")
	@get:Rule val activityRule = androidx.test.rule.ActivityTestRule(MainActivity::class.java)

	@Test fun opensLightingScreen() {
		val main = MainActivityActor()

		val screen = main.chooseLightingScreen()

		screen.assertDisplayed()
	}

	@Test fun opensPorterDuffScreen() {
		val main = MainActivityActor()

		val screen = main.choosePorterDuffScreen()

		screen.assertDisplayed()
	}

	@Test fun opensMatrixScreen() {
		val main = MainActivityActor()

		val screen = main.chooseMatrixScreen()

		screen.assertDisplayed()
	}

	@Test fun opensPaletteScreen() {
		val main = MainActivityActor()

		val screen = main.choosePaletteScreen()

		screen.assertDisplayed()
	}

	@Test fun opensSettings() {
		val main = MainActivityActor()

		val screen = main.openSettings()

		screen.assertDisplayed()
	}
}
