package net.twisterrob.colorfilters.android

import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.*
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.ColorFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.annotation.ColorInt
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.TypefaceSpan
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import net.twisterrob.colorfilters.android.keyboard.KeyboardHandler
import net.twisterrob.colorfilters.android.keyboard.KeyboardMode
import net.twisterrob.colorfilters.base.R
import net.twisterrob.colorfilters.base.R.string

abstract class ColorFilterFragment : Fragment() {

	private lateinit var listener: Listener

	protected val keyboard: KeyboardHandler get() = listener.keyboard

	abstract val preferredKeyboardMode: KeyboardMode

	protected val currentBitmap: Bitmap? get() = listener.currentBitmap

	protected val isPortrait: Boolean
		get() = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

	protected val prefs: SharedPreferences
		get() = PreferenceManager.getDefaultSharedPreferences(requireContext().applicationContext)

	override fun onAttach(context: Context?) = super.onAttach(context).also {
		listener = context as Listener
	}

	override fun onCreate(savedInstanceState: Bundle?) = super.onCreate(savedInstanceState).also {
		setHasOptionsMenu(true)
	}

	override fun onResume() = super.onResume().also {
		listener.fragmentOnResume()
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.fragment_color_filter, menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.action_info -> {
				displayHelp()
				return true
			}

			R.id.action_share -> {
				val image = listener.renderCurrentView(getString(R.string.cf_share_subject), generateCode())

				val intent = Intent(Intent.ACTION_SEND).apply {
					val sharedText: CharSequence = SpannableStringBuilder().apply {
						append(getText(string.cf_share_text))
						append("\n\n")
						append(generateFormattedCode())
					}
					type = "image/jpeg" //NON-NLS
					putExtra(Intent.EXTRA_SUBJECT, getString(R.string.cf_share_subject))
					putExtra(Intent.EXTRA_TEXT, sharedText)
					putExtra(Intent.EXTRA_STREAM, image)
				}
				startActivity(Intent.createChooser(intent, getText(R.string.cf_share_picker_title)))
				return true
			}

			else -> return super.onOptionsItemSelected(item)
		}
	}

	/** @see displayHelp(Int, Int) */
	protected abstract fun displayHelp()

	protected fun displayHelp(@StringRes titleResourceId: Int, @StringRes descriptionResourceId: Int) {
		fun getInfoMessage(@StringRes descriptionResourceId: Int): CharSequence =
			SpannableStringBuilder().apply {
				append(getText(descriptionResourceId))
				append("\n\n") //NON-NLS
				append(getText(R.string.cf_info_code))
				append("\n") //NON-NLS
				append(generateFormattedCode())
			}
		AlertDialog.Builder(requireActivity()).apply {
			setTitle(getText(titleResourceId))
			setMessage(getInfoMessage(descriptionResourceId))
			setPositiveButton(R.string.cf_info_ok) { dialog, _ -> dialog.dismiss() }
			setNeutralButton(R.string.cf_info_copy) { dialog, _ ->
				dialog.dismiss()
				copyToClipboard(requireContext(), getText(titleResourceId), generateCode())
				Toast.makeText(requireActivity(), R.string.cf_info_copy_toast, Toast.LENGTH_SHORT).show()
			}
		}.show()
	}

	override fun onDestroyView() = super.onDestroyView().also {
		keyboard.hideCustomKeyboard()
	}

	open fun imageChanged() {
		// noop by default, use currentBitmap to query Bitmap if needed
	}

	protected abstract fun createFilter(): ColorFilter?
	protected open fun updateFilter() {
		val filter = createFilter()
		listener.colorFilterChanged(filter)
	}

	protected abstract fun generateCode(): CharSequence
	private fun generateFormattedCode(): CharSequence =
		SpannableString(generateCode()).apply {
			setSpan(TypefaceSpan("monospace"), 0, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
			setSpan(RelativeSizeSpan(0.6f), 0, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
			setSpan(BackgroundColorSpan(0x33999999), 0, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
		}

	abstract fun reset()

	open fun needsImages() = true // TODO property

	interface Listener {

		val keyboard: KeyboardHandler

		fun colorFilterChanged(colorFilter: ColorFilter?)

		fun renderCurrentView(title: CharSequence, description: CharSequence): Uri

		fun fragmentOnResume()

		val currentBitmap: Bitmap?
	}

	companion object {

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		fun copyToClipboard(context: Context, title: CharSequence, content: CharSequence) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
				@Suppress("DEPRECATION")
				val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager
				clipboard.text = content
			} else {
				val clipboard =
					context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
				clipboard.primaryClip = android.content.ClipData.newPlainText(title, content)
			}
		}

		// TODO consider extensions in ColorUtils.kt?

		fun colorToRGBString(@ColorInt color: Int): String =
			"%d, %d, %d".formatRoot(color.red(), color.green(), color.blue())

		fun colorToARGBString(@ColorInt color: Int): String =
			"%d, %d, %d, %d".formatRoot(color.alpha(), color.red(), color.green(), color.blue())

		fun colorToRGBHexString(prefix: String, @ColorInt color: Int): String =
			"%s%06X".formatRoot(prefix, 0xFFFFFF and color)

		fun colorToARGBHexString(prefix: String, @ColorInt color: Int): String =
			"%s%08X".formatRoot(prefix, color)
	}
}