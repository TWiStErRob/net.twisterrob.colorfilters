package net.twisterrob.colorfilters.android

import androidx.test.filters.LargeTest
import de.mannodermaus.junit5.ActivityScenarioExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@LargeTest
class MainActivityTest {

	@RegisterExtension @JvmField
	val activityRule = ActivityScenarioExtension.launch<MainActivity>()

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
