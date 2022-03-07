package net.twisterrob.colorfilters.android

import android.graphics.Color
import androidx.annotation.ColorInt
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

class ColorUtilsKtTest {

	class ReplaceAlphaFrom(
		val useCase: String,
		@ColorInt val receiver: Int,
		@ColorInt val param: Int,
		@ColorInt val expected: Int,
	)

	@TestFactory fun replaceAlphaFrom() =
		listOf(
			ReplaceAlphaFrom(
				"Replaces alpha keeping RGB",
				Color.argb(0x12, 0x34, 0x56, 0x78),
				Color.argb(0x87, 0x65, 0x43, 0x21),
				Color.argb(0x87, 0x34, 0x56, 0x78),
			),
			ReplaceAlphaFrom(
				"Adds only alpha",
				Color.argb(0x00, 0x00, 0x00, 0x00),
				Color.argb(0xAB, 0x00, 0x00, 0x00),
				Color.argb(0xAB, 0x00, 0x00, 0x00),
			),
			ReplaceAlphaFrom(
				"Clears alpha",
				Color.argb(0x12, 0x34, 0x56, 0x78),
				Color.argb(0x00, 0x00, 0x00, 0x00),
				Color.argb(0x00, 0x34, 0x56, 0x78),
			),
		).map {
			dynamicTest(it.useCase) {
				val result = it.receiver.replaceAlphaFrom(it.param)

				assertThat(it.expected, equalTo(result))
			}
		}
}
