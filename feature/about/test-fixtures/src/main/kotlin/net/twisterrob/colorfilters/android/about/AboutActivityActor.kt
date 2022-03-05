package net.twisterrob.colorfilters.android.about

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId

class AboutActivityActor {

	fun assertDisplayed() {
		onView(withId(R.id.about_app)).check(matches(isDisplayed()))
	}
}
