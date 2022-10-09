package net.twisterrob.colorfilters.android.image

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.preference.PreferenceManager
import kotlin.math.max

private const val PREF_IMAGE_URL = "Image.url"
private const val REQUEST_CODE_GET_PICTURE = 1235
private const val REQUEST_CODE_PERMISSION_PICTURE = 1236

class ImageFragment : Fragment() {

	interface Listener { // TODO group: ViewModel

		fun reset()
		fun imageChanged()
	}

	private val loadListener = object : BitmapKeeper.Listener {
		override fun loadComplete() {
			listener.imageChanged()
		}
	}
	private val noListener = object : BitmapKeeper.Listener {
		override fun loadComplete() {
			// No op.
		}
	}

	private lateinit var original: ImageView
	private lateinit var preview: ImageView

	private lateinit var listener: Listener

	override fun onAttach(context: Context) {
		super.onAttach(context)
		listener = context as Listener
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
		inflater.inflate(R.layout.fragment_image, container, false)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		requireActivity().addMenuProvider(object : MenuProvider {
			override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
				menuInflater.inflate(R.menu.fragment_image, menu)
			}

			override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
				@Suppress("LiftReturnOrAssignment")
				when (menuItem.itemId) {
					R.id.action_image -> {
						startLoadImage(false)
						return true
					}
					else -> {
						return false
					}
				}
			}
		}, viewLifecycleOwner, Lifecycle.State.STARTED)

		original = view.findViewById(R.id.original)
		original.setOnClickListener { startLoadImage(false) }
		original.setOnLongClickListener { loadDefaults();true }

		preview = view.findViewById(R.id.preview)
		preview.setOnClickListener { listener.reset() }
	}

	override fun onViewStateRestored(savedInstanceState: Bundle?) {
		super.onViewStateRestored(savedInstanceState)
		if (savedInstanceState == null) {
			val imageUrl: String? = prefs.getString(PREF_IMAGE_URL, null)
			if (imageUrl != null) {
				load(Uri.parse(imageUrl))
				return
			}
		} else {
			val originalLoaded = BitmapKeeper.into(parentFragmentManager, original, loadListener)
			val previewLoaded = BitmapKeeper.into(parentFragmentManager, preview, noListener)
			if (originalLoaded && previewLoaded) {
				return
			}
		}
		loadDefaults()
	}

	override fun onStop() {
		super.onStop()
		val uri = BitmapKeeper.getUri(parentFragmentManager)
		prefs.edit().apply {
			if (uri != null) {
				putString(PREF_IMAGE_URL, uri.toString())
			} else {
				remove(PREF_IMAGE_URL)
			}
		}.apply()
	}

	@Suppress("SameParameterValue")
	private fun checkPermission(
		permission: String,
		requestCode: Int,
		rationale: (() -> Unit)? = null
	): Boolean {
		@Suppress("LiftReturnOrAssignment")
		if (VERSION_CODES.M <= VERSION.SDK_INT
			&& ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED
		) {
			if (rationale != null && shouldShowRequestPermissionRationale(permission)) {
				rationale()
			} else {
				@Suppress("DEPRECATION") // TODO group: ActivityResultContract
				requestPermissions(arrayOf(permission), requestCode)
			}
			return false
		} else {
			return true
		}
	}

	private fun startLoadImage(skipRationale: Boolean) {
		if (!checkPermission(
				Manifest.permission.READ_EXTERNAL_STORAGE,
				REQUEST_CODE_PERMISSION_PICTURE,
				if (skipRationale) null else fun() {
					AlertDialog.Builder(requireContext())
						.setMessage("External applications may return a reference to a file on the device's storage.")
						.setPositiveButton(android.R.string.ok) { _, _ -> startLoadImage(true) }
						.setNegativeButton(android.R.string.cancel) { _, _ -> }
						.show()
				}
			)
		) {
			return
		}
		startLoadImage()
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
		when (requestCode) {
			REQUEST_CODE_PERMISSION_PICTURE ->
				if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
					AlertDialog.Builder(requireContext())
						.setMessage("You may continue, but it is possible the picked image will not load.")
						.setPositiveButton(android.R.string.ok) { _, _ -> startLoadImage() }
						.setNegativeButton(android.R.string.cancel) { _, _ -> }
						.show()
				} else {
					startLoadImage(false)
				}
			else ->
				@Suppress("DEPRECATION") // TODO group: ActivityResultContract
				super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		}
	}

	private fun startLoadImage() {
		// Camera
		val captureIntentTemplate = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
		val camIntents = requireContext().packageManager!!
			.queryIntentActivitiesCompat(captureIntentTemplate, 0)
			.map { res ->
				Intent(captureIntentTemplate).apply {
					component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
					`package` = res.activityInfo.packageName
				}
			}

		// Filesystem
		val galleryIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
			type = "image/*"
		}

		// Chooser of filesystem options
		val chooserIntent = Intent.createChooser(galleryIntent, getText(R.string.cf_image_action)).apply {
			// Add the camera options
			putExtra(Intent.EXTRA_INITIAL_INTENTS, camIntents.toTypedArray<Parcelable>())
		}

		@Suppress("DEPRECATION") // TODO group: ActivityResultContract
		startActivityForResult(chooserIntent, REQUEST_CODE_GET_PICTURE)
	}

	@Suppress("NestedBlockDepth") // Outer when is readable; TODO group: ActivityResultContract
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		when (requestCode) {
			REQUEST_CODE_GET_PICTURE -> {
				if (resultCode == Activity.RESULT_OK && data != null) {
					data.data?.let { dataUri ->
						load(dataUri)
						return
					}
					data.extras?.let { extras ->
						val extraData = extras.getBitmap("data")
						if (extraData != null) {
							load(extraData)
							return
						}
					}
				}
			}
		}
		@Suppress("DEPRECATION") // TODO group: ActivityResultContract
		super.onActivityResult(requestCode, resultCode, data)
	}

	private fun loadDefaults() {
		BitmapKeeper.clear(parentFragmentManager)
		original.setImageResource(R.drawable.default_image)
		preview.setImageResource(R.drawable.default_image)
		loadListener.loadComplete()
	}

	fun load(uri: Uri) {
		BitmapKeeper.save(parentFragmentManager, uri)
		BitmapKeeper.into(parentFragmentManager, original, loadListener)
		BitmapKeeper.into(parentFragmentManager, preview, noListener)
	}

	fun load(bitmap: Bitmap) {
		BitmapKeeper.save(parentFragmentManager, bitmap)
		BitmapKeeper.into(parentFragmentManager, original, loadListener)
		BitmapKeeper.into(parentFragmentManager, preview, noListener)
	}

	private val prefs: SharedPreferences
		get() = PreferenceManager.getDefaultSharedPreferences(requireContext().applicationContext)

	fun setColorFilter(colorFilter: ColorFilter?) {
		preview.colorFilter = colorFilter
	}

	val current: Bitmap? get() = original.drawable?.asBitmap()

	fun renderPreview(): Bitmap {
		val od = original.drawable!!
		val pd = preview.drawable!!
		val ow = od.intrinsicWidth
		val pw = pd.intrinsicWidth
		val oh = od.intrinsicHeight
		val ph = pd.intrinsicHeight
		val portrait = max(ow, pw) <= max(oh, ph)

		val w: Int
		val h: Int
		if (portrait) {
			w = ow + pw
			h = max(ph, oh)
		} else {
			w = max(ow, pw)
			h = oh + ph
		}

		val image = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
		val canvas = Canvas(image)
		od.draw(canvas)
		canvas.translate(
			(if (portrait) ow else 0).toFloat(),
			(if (portrait) 0 else oh).toFloat()
		)
		pd.draw(canvas)
		return image
	}
}

@Throws(PackageManager.NameNotFoundException::class)
private fun PackageManager.queryIntentActivitiesCompat(
	intent: Intent,
	flags: Long
): List<ResolveInfo> =
	if (VERSION_CODES.TIRAMISU <= VERSION.SDK_INT) {
		queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(flags))
	} else {
		@Suppress("DEPRECATION")
		queryIntentActivities(intent, flags.toInt())
	}

@TargetApi(VERSION_CODES.TIRAMISU)
private fun Bundle.getBitmap(key: String): Bitmap? =
	if (VERSION_CODES.TIRAMISU <= VERSION.SDK_INT) {
		getParcelable(key, Bitmap::class.java)
	} else {
		@Suppress("DEPRECATION")
		get(key) as? Bitmap
	}
