package net.twisterrob.colorfilters.android.porterduff

import androidx.test.filters.LargeTest
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import net.twisterrob.colorfilters.android.ColorFilterFragment
import net.twisterrob.colorfilters.android.keyboard.KeyboardHandler
import net.twisterrob.colorfilters.android.test.ui.ColorFilterFragmentHost
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@LargeTest
class PorterDuffFragmentTest {

	@Rule @JvmField val activityRule = ColorFilterFragmentHost.rule(PorterDuffFragment::class)

	private lateinit var mockListener: ColorFilterFragment.Listener
	private lateinit var mockKeyboard: KeyboardHandler

	@Before fun setUp() {
		mockListener = activityRule.activity.listener
		mockKeyboard = mockListener.keyboard
	}

	@Test fun fragmentLaunches() {
		verify(mockKeyboard).registerEditText(any())

		val screen = PorterDuffScreenActor()

		screen.assertDisplayed()
	}
}
