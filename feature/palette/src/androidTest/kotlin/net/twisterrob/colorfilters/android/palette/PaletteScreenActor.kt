package net.twisterrob.colorfilters.android.palette

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import net.twisterrob.colorfilters.android.ScreenActor

class PaletteScreenActor : ScreenActor {

	override fun assertDisplayed() {
		onView(withId(R.id.swatchSort)).check(matches(isDisplayed()))
	}
}
