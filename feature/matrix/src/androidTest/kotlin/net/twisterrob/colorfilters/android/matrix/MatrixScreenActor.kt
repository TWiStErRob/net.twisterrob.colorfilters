package net.twisterrob.colorfilters.android.matrix

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import net.twisterrob.colorfilters.android.ScreenActor

class MatrixScreenActor : ScreenActor {

	override fun assertDisplayed() {
		onView(withId(R.id.matrix_reset)).check(matches(isDisplayed()))
	}
}
