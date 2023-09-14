package net.twisterrob.colorfilters.android.image

import android.annotation.TargetApi
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract

class ImageChooserContract : ActivityResultContract<Unit, ImageChooserContract.ImageResult>() {
	override fun createIntent(context: Context, input: Unit): Intent {
		val camIntents = cameraIntents(context)
		val galleryIntent = galleryIntent()
		return Intent.createChooser(galleryIntent, context.getText(R.string.cf_image_action)).apply {
			// Add the camera options, usually there will be only one on higher API levels.
			putExtra(Intent.EXTRA_INITIAL_INTENTS, camIntents.toTypedArray<Parcelable>())
		}
	}

	private fun galleryIntent(): Intent =
		Intent(Intent.ACTION_GET_CONTENT).apply {
		type = "image/*"
		addCategory(Intent.CATEGORY_OPENABLE)
	}

	private fun cameraIntents(context: Context): List<Intent> {
		val captureIntentTemplate = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
		val camIntents = context.packageManager!!
			.queryIntentActivitiesCompat(captureIntentTemplate, 0)
			.map { res ->
				Intent(captureIntentTemplate).apply {
					component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
					`package` = res.activityInfo.packageName
				}
			}
		return camIntents
	}

	override fun parseResult(resultCode: Int, intent: Intent?): ImageResult {
		if (resultCode == Activity.RESULT_OK && intent != null) {
			intent.data?.let { dataUri ->
				return ImageResult.ExternalUri(dataUri)
			}
			intent.extras?.let { extras ->
				val extraData = extras.getBitmap("data")
				if (extraData != null) {
					return ImageResult.InMemory(extraData)
				}
			}
		}
		return ImageResult.Invalid
	}

	sealed class ImageResult {
		data class ExternalUri(val uri: Uri) : ImageResult()
		data class InMemory(val bitmap: Bitmap) : ImageResult()
		data object Invalid : ImageResult()
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
