package net.twisterrob.colorfilters.android

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.ColorFilter
import android.inputmethodservice.KeyboardView
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.transaction
import net.twisterrob.colorfilters.android.about.AboutActivity
import net.twisterrob.colorfilters.android.image.ImageFragment
import net.twisterrob.colorfilters.android.image.LogoGenerator
import net.twisterrob.colorfilters.android.keyboard.KeyboardHandler
import net.twisterrob.colorfilters.android.keyboard.KeyboardHandlerFactory
import net.twisterrob.colorfilters.android.keyboard.KeyboardMode
import net.twisterrob.colorfilters.android.keyboard.KeyboardMode.NATIVE
import net.twisterrob.colorfilters.android.lighting.LightingFragment
import net.twisterrob.colorfilters.android.matrix.MatrixFragment
import net.twisterrob.colorfilters.android.palette.PaletteFragment
import net.twisterrob.colorfilters.android.porterduff.PorterDuffFragment
import net.twisterrob.colorfilters.android.resources.ResourceFontFragment
import java.util.zip.ZipFile

class MainActivity : AppCompatActivity(), ColorFilterFragment.Listener, ImageFragment.Listener {

	private lateinit var images: ImageFragment
	private var kbd: KeyboardHandler? = null
	private lateinit var imageToggleItem: MenuItem

	private val currentFragment: ColorFilterFragment?
		get() = supportFragmentManager.findFragmentById(R.id.container) as ColorFilterFragment?

	override val currentBitmap: Bitmap?
		get() = images.current

	override val keyboard: KeyboardHandler
		get() = kbd ?: KeyboardHandlerFactory().create(
			prefs.findKeyboardMode(),
			window,
			findViewById<View>(R.id.keyboard) as KeyboardView
		).also { kbd = it }

	private fun SharedPreferences.findKeyboardMode(): KeyboardMode =
		if (this.getBoolean(getString(R.string.cf_pref_keyboard), false)) {
			this@MainActivity.currentFragment!!.preferredKeyboardMode
		} else {
			NATIVE
		}

	private val prefs: SharedPreferences by lazy {
		PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_color_filter)

		images = supportFragmentManager.findFragmentById(R.id.images) as ImageFragment

		supportActionBar!!.apply {
			setDisplayShowTitleEnabled(false)
			setHomeButtonEnabled(true)
			setDisplayHomeAsUpEnabled(true)
			setHomeAsUpIndicator(R.drawable.ic_launcher)
		}.also { setupNavigation(it) }
	}

	@Suppress("DEPRECATION")
	private fun setupNavigation(actionBar: ActionBar) {
		actionBar.navigationMode = ActionBar.NAVIGATION_MODE_LIST
		val adapter = ArrayAdapter(
			actionBar.themedContext,
			android.R.layout.simple_list_item_1,
			android.R.id.text1,
			arrayOf(
				getText(R.string.cf_lighting_title),
				getText(R.string.cf_porterduff_title),
				getText(R.string.cf_matrix_title),
				getText(R.string.cf_palette_title)
			) + if (!BuildConfig.DEBUG) emptyArray<CharSequence>() else arrayOf(
				getText(R.string.cf_resfont_title)
			)
		)
		actionBar.setListNavigationCallbacks(adapter, ActionBar.OnNavigationListener { position, _ ->
			try {
				// keep the existing instance on rotation
				if (getPosition(currentFragment) != position) {
					val fragment = createFragment(position)
					supportFragmentManager.transaction {
						replace(R.id.container, fragment)
					}
				}
				return@OnNavigationListener true
			} catch (ex: RuntimeException) {
				return@OnNavigationListener false
			}
		})
	}

	override fun onResume() = super.onResume().also {
		val position = prefs.getInt(PREF_COLORFILTER_SELECTED, COLORFILTER_DEFAULT)
		@Suppress("DEPRECATION") // will replace when AndroidX or removed
		supportActionBar!!.setSelectedNavigationItem(position)
	}

	override fun onPause() = super.onPause().also {
		val position = getPosition(currentFragment)
		if (0 <= position) {
			prefs.edit().putInt(PREF_COLORFILTER_SELECTED, position).apply()
		}
	}

	override fun onCreateOptionsMenu(menu: Menu) = super.onCreateOptionsMenu(menu).also {
		menuInflater.inflate(R.menu.activity_color_filter, menu)
		imageToggleItem = menu.findItem(R.id.action_image_toggle)
		imageToggleItem.isChecked = prefs.getBoolean(PREF_COLORFILTER_PREVIEW, imageToggleItem.isChecked)
		updateImagesVisibility()
		return true
	}

	@SuppressLint("LogConditional")
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			android.R.id.home -> {
				startActivity(Intent(applicationContext, AboutActivity::class.java))
				return true
			}

			R.id.action_settings -> {
				// no actual result, just want a notification of return from preferences
				startActivityForResult(
					Intent(applicationContext, PreferencesActivity::class.java),
					Activity.RESULT_FIRST_USER
				)
				return true
			}

			R.id.action_logo -> {
				val logoZip = App.getShareableCachePath(this, "logos.zip")
				LogoGenerator.write(logoZip)
				Log.i("LOGO", "Written to: $logoZip")

				val logoUri = App.getShareableCacheUri(this, logoZip)
				val intent = Intent(Intent.ACTION_SEND).apply {
					type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(logoZip.extension)
					putExtra(Intent.EXTRA_STREAM, logoUri)
				}
				startActivity(Intent.createChooser(intent, getText(R.string.cf_share_picker_title)))

				images.load(ZipFile(logoZip).let { zip ->
					val biggest = zip
						.entries()
						.asSequence()
						.maxBy { it.size }
						?: error("No images in $logoZip")
					zip.getInputStream(biggest).use { BitmapFactory.decodeStream(it) }
				})
				return true
			}

			R.id.action_image_toggle -> {
				imageToggleItem.isChecked = !imageToggleItem.isChecked
				prefs.edit().putBoolean(PREF_COLORFILTER_PREVIEW, imageToggleItem.isChecked).apply()
				updateImagesVisibility()
				return true
			}

			else -> return super.onOptionsItemSelected(item)
		}
	}

	private fun updateImagesVisibility() {
		supportFragmentManager.beginTransaction().apply {
			val fragment = currentFragment
			if (imageToggleItem.isChecked && fragment != null && fragment.needsImages) {
				show(images)
			} else {
				hide(images)
			}
			commitAllowingStateLoss()
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		if (requestCode == Activity.RESULT_FIRST_USER) {
			kbd = null
			val fragment = currentFragment
			if (fragment != null) {
				// force recreating the fragment to pick up the new keyboard (in case it changed in settings)
				supportFragmentManager
					.beginTransaction()
					.detach(fragment)
					.attach(fragment)
					// .commit won't work because onResume is called after onActivityResult
					// and at this state we're "after onSaveInstanceState"
					.commitAllowingStateLoss()
			}
			return
		}
		super.onActivityResult(requestCode, resultCode, data)
	}

	override fun reset() {
		currentFragment?.run { reset() }
	}

	override fun onBackPressed() {
		if (kbd?.handleBack() == true) {
			return
		}
		super.onBackPressed()
	}

	private fun createFragment(position: Int): ColorFilterFragment =
		when (position) {
			0 -> LightingFragment()
			1 -> PorterDuffFragment()
			2 -> MatrixFragment()
			3 -> PaletteFragment()
			4 -> ResourceFontFragment()
			else -> throw IllegalStateException("Unknown position $position")
		}

	private fun getPosition(fragment: Fragment?): Int =
		when (fragment) {
			is LightingFragment -> 0
			is PorterDuffFragment -> 1
			is MatrixFragment -> 2
			is PaletteFragment -> 3
			is ResourceFontFragment -> 4
			null -> -1
			else -> throw IllegalStateException("Unknown fragment $fragment")
		}

	override fun colorFilterChanged(colorFilter: ColorFilter?) {
		images.setColorFilter(colorFilter)
	}

	override fun imageChanged() {
		currentFragment?.let {
			if (it.isResumed) {
				it.imageChanged()
			}
		}
	}

	override fun renderCurrentView(title: CharSequence, description: CharSequence): Uri {
		val file = App.getShareableCachePath(this, "temp.jpg")
		file.outputStream().use {
			val shareContent = images.renderPreview()
			val saved = shareContent.compress(CompressFormat.JPEG, 100, it)
			check(saved) {
				error("Couldn't save generated shared content to ${file.absolutePath}")
			}
		}
		return App.getShareableCacheUri(this, file)
	}

	override fun onAttachFragment(fragment: Fragment) {
		kbd = null
	}

	override fun fragmentOnResume() {
		imageChanged()
	}

	companion object {
		private const val PREF_COLORFILTER_SELECTED = "ColorFilter.selected"
		private const val PREF_COLORFILTER_PREVIEW = "ColorFilter.images"
		private const val COLORFILTER_DEFAULT = 0
	}
}
