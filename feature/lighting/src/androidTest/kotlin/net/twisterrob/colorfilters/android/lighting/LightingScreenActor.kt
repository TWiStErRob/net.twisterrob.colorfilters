package net.twisterrob.colorfilters.android.lighting

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import net.twisterrob.colorfilters.android.ScreenActor

class LightingScreenActor : ScreenActor {

	override fun assertDisplayed() {
		onView(withId(R.id.mulColor)).check(matches(isDisplayed()))
		onView(withId(R.id.addColor)).check(matches(isDisplayed()))
	}
}
