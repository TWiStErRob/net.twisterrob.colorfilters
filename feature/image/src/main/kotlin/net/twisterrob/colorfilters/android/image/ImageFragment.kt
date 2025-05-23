package net.twisterrob.colorfilters.android.image

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ColorFilter
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.preference.PreferenceManager
import kotlin.math.max

private const val PREF_IMAGE_URL = "Image.url"

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

	private val storagePermission = registerForActivityResult(RequestPermission()) { granted ->
		if (granted) {
			startLoadImage(false)
		} else {
			AlertDialog.Builder(requireContext())
				.setMessage("You may continue, but it is possible the picked image will not load.")
				.setPositiveButton(android.R.string.ok) { _, _ -> startLoadImage() }
				.setNegativeButton(android.R.string.cancel) { _, _ -> }
				.show()
		}
	}

	private val getExternal = registerForActivityResult(ImageChooserContract()) { result ->
		when (result) {
			is ImageChooserContract.ImageResult.ExternalUri -> load(result.uri)
			is ImageChooserContract.ImageResult.InMemory -> load(result.bitmap)
			ImageChooserContract.ImageResult.Invalid -> Unit
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
				@Suppress("LiftReturnOrAssignment", "OptionalWhenBraces")
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
		original.setOnLongClickListener { loadDefaults(); true }

		preview = view.findViewById(R.id.preview)
		preview.setOnClickListener { listener.reset() }
	}

	override fun onViewStateRestored(savedInstanceState: Bundle?) {
		super.onViewStateRestored(savedInstanceState)
		if (savedInstanceState == null) {
			val imageUrl: String? = prefs.getString(PREF_IMAGE_URL, null)
			if (imageUrl != null) {
				load(imageUrl.toUri())
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
		prefs.edit {
			if (uri != null) {
				putString(PREF_IMAGE_URL, uri.toString())
			} else {
				remove(PREF_IMAGE_URL)
			}
		}
	}

	private fun checkPermission(
		@Suppress("SameParameterValue")
		permission: String,
		rationale: (() -> Unit)? = null
	): Boolean {
		@Suppress("LiftReturnOrAssignment")
		if (VERSION_CODES.M <= VERSION.SDK_INT
			&& ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED
		) {
			if (rationale != null && shouldShowRequestPermissionRationale(permission)) {
				rationale()
			} else {
				storagePermission.launch(permission)
			}
			return false
		} else {
			return true
		}
	}

	private fun startLoadImage(skipRationale: Boolean) {
		if (VERSION.SDK_INT <= VERSION_CODES.P && !checkPermission(
				Manifest.permission.READ_EXTERNAL_STORAGE,
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

	private fun startLoadImage() {
		getExternal.launch(Unit)
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

		return createBitmap(w, h).applyCanvas {
			od.draw(this)
			this.translate(
				(if (portrait) ow else 0).toFloat(),
				(if (portrait) 0 else oh).toFloat()
			)
			pd.draw(this)
		}
	}
}
