package net.twisterrob.colorfilters.android

import android.support.annotation.StringRes
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withClassName
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withParent
import android.support.test.espresso.matcher.ViewMatchers.withText
import net.twisterrob.colorfilters.android.lighting.LightingScreenActor
import net.twisterrob.colorfilters.android.matrix.MatrixScreenActor
import net.twisterrob.colorfilters.android.palette.PaletteScreenActor
import net.twisterrob.colorfilters.android.porterduff.PorterDuffScreenActor
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo

class MainActivityActor {

	fun chooseLightingScreen(): LightingScreenActor {
		chooseScreen(R.string.cf_lighting_title)
		return LightingScreenActor()
	}

	fun choosePorterDuffScreen(): PorterDuffScreenActor {
		chooseScreen(R.string.cf_porterduff_title)
		return PorterDuffScreenActor()
	}

	fun chooseMatrixScreen(): MatrixScreenActor {
		chooseScreen(R.string.cf_matrix_title)
		return MatrixScreenActor()
	}

	fun choosePaletteScreen(): PaletteScreenActor {
		chooseScreen(R.string.cf_palette_title)
		return PaletteScreenActor()
	}

	private fun chooseScreen(@StringRes screenTitle: Int) {
		onView(spinner()).perform(click())
		onView(withText(screenTitle)).perform(click())
		assertChooserLabel(screenTitle)
	}

	private fun assertChooserLabel(@StringRes contents: Int) {
		val spinnerLabel = allOf(withParent(spinner()), withId(android.R.id.text1))
		onView(spinnerLabel).check(matches(withText(contents)))
	}

	private fun spinner() = allOf(
		withParent(withId(R.id.action_bar)),
		withClassName(equalTo("android.support.v7.widget.AppCompatSpinner"))
	)
}
