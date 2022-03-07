package net.twisterrob.colorfilters.android.about

import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test

@LargeTest
class AboutActivityTest {

	/**
	 * `android:label` and `android:versionName` must be set to be able to open the About screen.
	 */
	@Suppress("DEPRECATION")
	@get:Rule val activityRule = androidx.test.rule.ActivityTestRule(AboutActivity::class.java)

	@Test fun opensAboutScreen() {
		val about = AboutActivityActor()

		about.assertDisplayed()
	}
}
