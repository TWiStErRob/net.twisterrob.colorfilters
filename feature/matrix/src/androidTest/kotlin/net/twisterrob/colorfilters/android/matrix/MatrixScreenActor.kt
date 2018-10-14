package net.twisterrob.colorfilters.android.matrix

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import net.twisterrob.colorfilters.android.ScreenActor

class MatrixScreenActor : ScreenActor {

	override fun assertDisplayed() {
		onView(withId(R.id.matrix_reset)).check(matches(isDisplayed()))
	}
}
