package net.twisterrob.android.view.color

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.graphics.PointF
import android.os.Build
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import net.twisterrob.android.view.color.swatches.APIDemoSwatch
import net.twisterrob.android.view.color.swatches.PixelAbsoluteSwatch
import net.twisterrob.android.view.color.swatches.Swatch
import net.twisterrob.android.view.color.swatches.pixel.color.ColorReplacer
import net.twisterrob.android.view.color.swatches.pixel.color.LinearHSBGradient
import net.twisterrob.android.view.color.swatches.pixel.color.LinearHueGradient
import net.twisterrob.android.view.color.swatches.pixel.color.RadialHSBGradient
import net.twisterrob.android.view.color.swatches.pixel.color.RadialHueGradient
import net.twisterrob.android.view.color.swatches.pixel.drawer.CenterBitmapDrawer
import net.twisterrob.android.view.color.swatches.pixel.drawer.ColumnByColumnBitmapDrawer
import net.twisterrob.android.view.color.swatches.pixel.drawer.LineByLineBitmapDrawer
import kotlin.math.sqrt

open class ColorPickerView : AppCompatImageView, SwatchChooser.OnSwatchChangeListener {

	interface OnColorChangedListener {
		fun colorChanged(color: Int)
	}

	var colorChangedListener: OnColorChangedListener? = null
	private val touch: Touchy
	private lateinit var swatch: Swatch

	constructor(context: Context) : super(context) {
		touch = Touchy(context)
		init(context)
	}

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
		touch = Touchy(context)
		init(context)
	}

	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
		touch = Touchy(context)
		init(context)
	}

	private fun init(@Suppress("UNUSED_PARAMETER") context: Context) {
		setOnLongClickListener(touch)
		setOnTouchListener(touch)
		isFocusableInTouchMode = true
		setupDefaultSwatch()
	}

	private fun setupDefaultSwatch() {
		val swatch = swatches.iterator().next()
		if (!isInEditMode) {
			(swatch as? PixelAbsoluteSwatch)?.forceAsync()
		}
		setSwatch(swatch)
	}

	var isContinuousMode: Boolean // === by touch::isContinuousMode
		get() = touch.isContinuousMode
		set(continuousMode) {
			touch.isContinuousMode = continuousMode
		}

	fun getSwatch(): Swatch? {
		return swatch
	}

	fun setSwatch(swatchIndex: Int): Boolean {
		if (0 <= swatchIndex && swatchIndex < swatches.size) {
			setSwatch(swatches[swatchIndex])
			return true
		}
		return false
	}

	fun setSwatch(swatch: Swatch) {
		if (this::swatch.isInitialized) {
			swatch.currentColor = this.swatch.currentColor
		}
		this.swatch = swatch
		setImageDrawable(swatch)
	}

	var color: Int // by swatch.currentColor
		get() = swatch.currentColor
		set(color) {
			if (color == swatch.currentColor) {
				return
			}
			swatch.currentColor = color
			invalidate()
			fireColorChanged()
		}

	/**
	 * @see [Tutorial](http://www.jayway.com/2012/12/12/creating-custom-android-views-part-4-measuring-and-how-to-force-a-view-to-be-square)
	 */
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		val RATIO = 1f / 1f

		var width = measuredWidth
		var height = measuredHeight
		val widthWithoutPadding = width - paddingLeft - paddingRight
		val heightWithoutPadding = height - paddingTop - paddingBottom

		val maxWidth = (heightWithoutPadding * RATIO).toInt()
		val maxHeight = (widthWithoutPadding / RATIO).toInt()

		if (maxWidth < maxHeight) {
			width = maxWidth + paddingLeft + paddingRight
		} else {
			height = maxHeight + paddingTop + paddingBottom
		}

		setMeasuredDimension(width, height)
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	override fun getBaseline(): Int {
		if (Build.VERSION_CODES.HONEYCOMB <= Build.VERSION.SDK_INT && baselineAlignBottom) {
			return measuredHeight
		}
		return paddingTop + (measuredHeight - paddingTop - paddingBottom) / 2
	}

	protected open fun fireColorChanged() {
		colorChangedListener?.colorChanged(swatch.currentColor)
	}

	private inner class Touchy(context: Context) : View.OnTouchListener, View.OnLongClickListener {

		private val mTouchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop
		private var trackedArea = Swatch.AREA_INVALID
		private var highlightArea = Swatch.AREA_INVALID
			set(value) {
				field = value
				swatch.setCurrentArea(field)
			}
		var isContinuousMode = true

		private val longTapStart = PointF()

		override fun onTouch(v: View, event: MotionEvent): Boolean {
			if (drawable is SwatchChooser) {
				return (drawable as SwatchChooser).onTouch(v, event)
			}
			invalidate()
			val x = event.x - paddingLeft
			val y = event.y - paddingTop
			if (!swatch.bounds.contains(x.toInt(), y.toInt())) {
				return unhandled(event)
			}
			val inCenter = swatch.getAreaCode(x, y)

			when (event.action) {
				MotionEvent.ACTION_DOWN -> {
					startLongTap(x, y)
					//rebuild()
					trackedArea = inCenter
					if (trackedArea != Swatch.AREA_INVALID && !isContinuousMode) {
						highlightArea = inCenter
					}
					return handled(event)
				}

				MotionEvent.ACTION_MOVE -> {
					updateLongTap(x, y)
					if (trackedArea != Swatch.AREA_INVALID) {
						if (trackedArea == inCenter && !isContinuousMode) {
							highlightArea = inCenter
						} else {
							highlightArea = Swatch.AREA_INVALID
						}
						val color = swatch.findColor(trackedArea, x, y)
						if (isContinuousMode) {
							this@ColorPickerView.color = color
						} else {
							swatch.currentColor = color
						}
					}
					return handled(event)
				}

				MotionEvent.ACTION_UP -> {
					if (trackedArea != Swatch.AREA_INVALID) {
						if (!isContinuousMode
							&& trackedArea == highlightArea
							&& swatch.triggersColorChange(trackedArea)
						) {
							val color = swatch.findColor(trackedArea, x, y)
							this@ColorPickerView.color = color
							val view = this@ColorPickerView // ClickableViewAccessibility doesn't see the connection
							view.performClick()
						}
						trackedArea = Swatch.AREA_INVALID
						highlightArea = Swatch.AREA_INVALID
					}
					return handled(event)
				}

				MotionEvent.ACTION_CANCEL -> {
					trackedArea = Swatch.AREA_INVALID
					highlightArea = Swatch.AREA_INVALID
					return handled(event)
				}
			}
			return unhandled(event)
		}

		private fun unhandled(@Suppress("UNUSED_PARAMETER") event: MotionEvent) = false

		private fun handled(event: MotionEvent) = true.also {
			onTouchEvent(event)
		}

		fun startLongTap(x: Float, y: Float) {
			longTapStart.set(x, y)
		}

		private fun updateLongTap(x: Float, y: Float) {
			val dist2 = (x - longTapStart.x) * (x - longTapStart.x) + (y - longTapStart.y) * (y - longTapStart.y)
			if (sqrt(dist2) >= mTouchSlop) {
				cancelLongPress()
			}
		}

		override fun onLongClick(v: View) = true.also {
			showChooser()
		}
	}

	override fun performClick(): Boolean {
		(swatch as? PixelAbsoluteSwatch)?.forceAsync()
		setSwatch(swatch)
		return super.performClick()
	}

	/**
	 * Manipulate as you wish.
	 */
	val swatches: List<Swatch> = mutableListOf(
		PixelAbsoluteSwatch(
			ColorReplacer.wrap(CenterBitmapDrawer.factory(), Color.BLACK, Color.TRANSPARENT),
			RadialHSBGradient()
		),
		PixelAbsoluteSwatch(ColumnByColumnBitmapDrawer.factory(), LinearHSBGradient()),
		APIDemoSwatch(),
		PixelAbsoluteSwatch(CenterBitmapDrawer.factory(), RadialHueGradient()),
		PixelAbsoluteSwatch(LineByLineBitmapDrawer.factory(), LinearHueGradient())
	)

	val swatchIndex get() = this.swatches.indexOf(this.swatch)

	/**
	 * To hide chooser: call [swatch::set].
	 */
	fun showChooser() {
		val chooser = SwatchChooser(swatches)
		chooser.setOnSwatchChangeListener(this)
		chooser.setTileMargin(TypedValue.applyDimension(COMPLEX_UNIT_DIP, 4f, resources.displayMetrics).toInt())
		setImageDrawable(chooser)
	}

	override fun swatchSelected(swatch: Swatch) {
		this.swatch = swatch
	}
}