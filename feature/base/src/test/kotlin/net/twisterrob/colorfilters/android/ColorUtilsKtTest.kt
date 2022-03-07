package net.twisterrob.colorfilters.android

import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

class ColorUtilsKtTest {

	class ToString(
		val useCase: String,
		@ColorInt val receiver: Int,
		val expected: String,
	)

	@TestFactory fun toARGBHexString() =
		listOf(
			ToString(
				"All bits are in the right places",
				argb(0x12, 0x34, 0x56, 0x78),
				"12345678",
			),
			ToString(
				"All 0 bits are rendered",
				argb(0x00, 0x00, 0x00, 0x00),
				"00000000",
			),
			ToString(
				"All 1 bits are rendered",
				argb(0xFF, 0xFF, 0xFF, 0xFF),
				"FFFFFFFF",
			),
			ToString(
				"Doesn't strip beginning",
				argb(0x00, 0x12, 0x34, 0x56),
				"00123456"
			),
			ToString(
				"All hex digits are uppercase",
				argb(0xFE, 0xDC, 0xBA, 0x00),
				"FEDCBA00"
			),
		).map {
			dynamicTest(it.useCase) {
				val result = it.receiver.toARGBHexString()

				assertThat(result, equalTo(it.expected))
			}
		}
}

/**
 * Copy of [android.graphics.Color.argb], which can run on JVM without Robolectric.
 */
private fun argb(
	@IntRange(from = 0, to = 255) alpha: Int,
	@IntRange(from = 0, to = 255) red: Int,
	@IntRange(from = 0, to = 255) green: Int,
	@IntRange(from = 0, to = 255) blue: Int
): Int =
	alpha shl 24 or (red shl 16) or (green shl 8) or blue
