package net.twisterrob.colorfilters.android.about

import androidx.test.filters.LargeTest
import de.mannodermaus.junit5.ActivityScenarioExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@LargeTest
class AboutActivityTest {

	/**
	 * `android:label` and `android:versionName` must be set to be able to open the About screen.
	 */
	@RegisterExtension @JvmField
	val activityRule = ActivityScenarioExtension.launch<AboutActivity>()

	@Test fun opensAboutScreen() {
		val about = AboutActivityActor()

		about.assertDisplayed()
	}
}
