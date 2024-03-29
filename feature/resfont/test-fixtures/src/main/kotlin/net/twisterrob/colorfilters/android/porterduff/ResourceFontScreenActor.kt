package net.twisterrob.colorfilters.android.porterduff

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import net.twisterrob.colorfilters.android.resfont.R
import net.twisterrob.colorfilters.android.test.ui.ScreenActor

class ResourceFontScreenActor : ScreenActor {

	override fun assertDisplayed() {
		onView(withId(R.id.table)).check(matches(isDisplayed()))
	}
}
