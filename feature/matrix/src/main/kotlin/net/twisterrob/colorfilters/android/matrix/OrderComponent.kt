package net.twisterrob.colorfilters.android.matrix

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.ColorMatrix
import android.os.Build
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.DragShadowBuilder
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat

internal class OrderComponent(
	view: View,
	listener: Component.RefreshListener
) : Component(view, listener) {

	companion object {

		private const val PREF_ORDER_MAP = "Order.map/"
		private val IDENTITY_MAPPING = intArrayOf(0, 1, 2, 3, 4)
	}

	private val places = arrayOf(
		vg(R.id.controls_comp_1),
		vg(R.id.controls_comp_2),
		vg(R.id.controls_comp_3),
		vg(R.id.controls_comp_4),
		vg(R.id.controls_comp_5)
	)
	private val comps = arrayOf(
		v(R.id.controls_comp_rR),
		v(R.id.controls_comp_rG),
		v(R.id.controls_comp_rB),
		v(R.id.controls_comp_S),
		v(R.id.controls_comp_sat)
	)
	private val swaps = arrayOf(
		v(R.id.controls_swap_12),
		v(R.id.controls_swap_23),
		v(R.id.controls_swap_34),
		v(R.id.controls_swap_45)
	)
	var map = IntArray(IDENTITY_MAPPING.size)
		set(map) {
			System.arraycopy(map, 0, this.map, 0, this.map.size)
			refreshUI()
		}

	override fun setupUI() {
		for (i in swaps.indices) {
			val left = places[i]
			val right = places[i + 1]
			swaps[i].setOnClickListener { swapper.dropped(left, right) }
		}

		for (place in places) {
			place.setOnDragListener(ItemDragListener(swapper))
		}
		for (comp in comps) {
			comp.setOnTouchListener(DragStartListener())
		}
	}

	override fun saveToPreferences(editor: SharedPreferences.Editor) {
		for (i in map.indices) {
			editor.putInt(PREF_ORDER_MAP + i, this.map[i])
		}
	}

	override fun restoreFromPreferences(prefs: SharedPreferences) {
		for (i in map.indices) {
			map[i] = prefs.getInt(PREF_ORDER_MAP + i, i)
		}
		map = map // re-set to force a refresh
	}

	override fun reset() {
		map = IDENTITY_MAPPING
	}

	override fun refreshModel() {
		for (origPos in comps.indices) {
			val id = comps[origPos].id
			for (newPos in places.indices) {
				val found: View? = places[newPos].findViewById(id)
				if (found != null) { // the original component was moved from origPos to newPos
					this.map[newPos] = origPos
				}
			}
		}
	}

	override fun combineInto(colorMatrix: ColorMatrix) {
		// no touchy
	}

	override fun appendTo(sb: StringBuilder): Boolean {
		// no visual representation, the order of other components will reflect this
		return false
	}

	/**
	 * @param components must match the order in [comps].
	 */
	fun order(vararg components: Component): Array<Component> {
		require(components.size == map.size) {
			"Invalid number of components (${components.size}), there must be ${map.size} of them."
		}
		return components.indices
			.map { index -> components[map[index]] }
			.toTypedArray()
	}

	private fun refreshUI() {
		for (place in places) {
			place.removeView(place.getChildAt(0))
		}
		for (i in comps.indices) {
			places[i].addView(comps[map[i]])
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	private class DragStartListener : View.OnTouchListener {

		override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
			return motionEvent.action == MotionEvent.ACTION_DOWN && startDragAndDrop(view)
		}

		private fun startDragAndDrop(view: View): Boolean {
			return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
				@Suppress("DEPRECATION")
				view.startDrag(null, DragShadowBuilder(view), OrderDragLocalState(view), 0)
			} else {
				view.startDragAndDrop(null, DragShadowBuilder(view), OrderDragLocalState(view), 0)
			}
		}
	}

	private class OrderDragLocalState(val view: View)

	private class ItemDragListener(
		private val listener: ChangeListener
	) : View.OnDragListener {

		interface ChangeListener {

			fun dropped(source: ViewGroup, target: ViewGroup)
		}

		override fun onDrag(dropTarget: View, event: DragEvent): Boolean {
			if (event.action != DragEvent.ACTION_DRAG_LOCATION) {
				//Log.d("DRAG", describeDragEvent(dropTarget, event))
			}
			val dragged = (event.localState as? OrderDragLocalState ?: return false).view

			when (event.action) {
				DragEvent.ACTION_DRAG_STARTED -> {
					dragged.post { dragged.visibility = View.INVISIBLE }
					setBackground(dropTarget, R.drawable.matrix_order_drop)
				}

				DragEvent.ACTION_DRAG_ENTERED ->
					setBackground(dropTarget, R.drawable.matrix_order_drop_active)

				DragEvent.ACTION_DRAG_EXITED ->
					setBackground(dropTarget, R.drawable.matrix_order_drop)

				DragEvent.ACTION_DRAG_ENDED -> {
					dragged.post { dragged.visibility = View.VISIBLE }
					setBackground(dropTarget, R.drawable.matrix_order_component)
				}

				DragEvent.ACTION_DROP -> {
					val source = dragged.parent as ViewGroup
					val target = dropTarget as ViewGroup
					if (source !== target) {
						listener.dropped(source, target)
					}
				}
			}
			return true
		}

		private fun setBackground(dropTarget: View, @DrawableRes resId: Int) {
			val drawable = ContextCompat.getDrawable(dropTarget.context, resId)
			ViewCompat.setBackground(dropTarget, drawable)
		}
	}

	private val swapper = object : ItemDragListener.ChangeListener {
		override fun dropped(source: ViewGroup, target: ViewGroup) {
			val from = source.getChildAt(0)
			val to = target.getChildAt(0)
			source.removeView(from)
			target.removeView(to)
			source.addView(to)
			target.addView(from)
			refreshModel()
			dispatchRefresh(true)
		}
	}
}
