package net.twisterrob.colorfilters.android.lighting

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import net.twisterrob.colorfilters.android.ScreenActor

class LightingScreenActor : ScreenActor {

	override fun assertDisplayed() {
		onView(withId(R.id.mulColor)).check(matches(isDisplayed()))
		onView(withId(R.id.addColor)).check(matches(isDisplayed()))
	}
}
