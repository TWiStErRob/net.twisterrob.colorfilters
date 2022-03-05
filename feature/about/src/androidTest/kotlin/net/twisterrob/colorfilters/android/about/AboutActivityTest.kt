package net.twisterrob.colorfilters.android.about

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AboutActivityTest {

	/**
	 * `android:label` and `android:versionName` must be set to be able to open the About screen.
	 */
	@Suppress("DEPRECATION")
	@get:Rule val activityRule = androidx.test.rule.ActivityTestRule(AboutActivity::class.java)

	@Test fun opensLightingScreen() {
		val about = AboutActivityActor()

		about.assertDisplayed()
	}
}
