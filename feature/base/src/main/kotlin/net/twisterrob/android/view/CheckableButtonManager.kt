package net.twisterrob.android.view

import android.widget.CompoundButton
import java.util.*

class CheckableButtonManager : CompoundButton.OnCheckedChangeListener {

	private val buttons = LinkedList<CompoundButton>()
	private var listener: CompoundButton.OnCheckedChangeListener? = null
	var checked: CompoundButton? = null; private set

	fun addButton(rb: CompoundButton) {
		updateChecked(rb)
		rb.setOnCheckedChangeListener(this)
		buttons.add(rb)
	}

	private fun updateChecked(rb: CompoundButton) {
		if (rb.isChecked) {
			if (checked == null) {
				checked = rb
			} else {
				rb.isChecked = false
			}
		}
	}

	override fun onCheckedChanged(newlyCheckedButton: CompoundButton, isChecked: Boolean) {
		if (isChecked) {
			disable(newlyCheckedButton)
			checked = newlyCheckedButton
			fireCheckedChange()
		}
	}

	fun setOnCheckedChangeListener(listener: CompoundButton.OnCheckedChangeListener) {
		this.listener = listener
	}

	private fun fireCheckedChange() {
		listener?.onCheckedChanged(checked, true)
	}

	private fun disable(newlyCheckedButton: CompoundButton) {
		@Suppress("ConstantConditionIf")
		if (safeMode) {
			buttons.filterNot { it == newlyCheckedButton }.forEach { it.isChecked = false }
		} else {
			checked?.isChecked = false
		}
	}

	companion object {
		private const val safeMode = false
	}
}
