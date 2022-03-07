package net.twisterrob.colorfilters.android.palette

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import net.twisterrob.colorfilters.android.test.ui.ScreenActor

class PaletteScreenActor : ScreenActor {

	override fun assertDisplayed() {
		onView(withId(R.id.swatchSort)).check(matches(isDisplayed()))
	}
}
