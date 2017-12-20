package net.twisterrob.colorfilters.android

import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

	@Rule @JvmField val activityRule = ActivityTestRule(MainActivity::class.java)

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
}
