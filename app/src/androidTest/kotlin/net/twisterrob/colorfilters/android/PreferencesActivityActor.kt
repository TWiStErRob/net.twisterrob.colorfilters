package net.twisterrob.colorfilters.android

import android.view.View
import android.widget.LinearLayout
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withText
import net.twisterrob.colorfilters.android.about.AboutActivityActor
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo

class PreferencesActivityActor {

	fun assertDisplayed() {
		onView(withText(R.string.app_name)).check(matches(isDisplayed()))
	}

	fun toggleExperimentalKeyboards() {
		onPrefCheckbox(R.string.cf_pref_keyboard_title)
			.perform(click())
	}

	fun assertExperimentalKeyboards(state: Boolean) {
		onPrefCheckbox(R.string.cf_pref_keyboard_title)
			.check(matches(if (state) isChecked() else isNotChecked()))
	}

	private fun onPrefCheckbox(@StringRes titleText: Int) =
		onView(prefCheckBox(withText(titleText)))

	private fun prefCheckBox(titleMatcher: Matcher<View>): Matcher<View> {
		// TODO Why doesn't androidx.preference.R.title|checkbox work instead of textView and checkBox?
		val linearLayout = withClassName(equalTo(LinearLayout::class.qualifiedName))
		val checkBox = withClassName(equalTo(AppCompatCheckBox::class.qualifiedName))
		val textView = withClassName(equalTo(AppCompatTextView::class.qualifiedName))
		val title = allOf(textView, titleMatcher)
		val itemRoot = allOf(linearLayout, hasDescendant(title))
		return allOf(checkBox, isDescendantOfA(itemRoot))
	}

	fun openAbout(): AboutActivityActor {
		onView(withText(R.string.cf_about_title)).perform(click())
		return AboutActivityActor()
	}
}
