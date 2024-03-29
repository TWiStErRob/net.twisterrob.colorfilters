package net.twisterrob.colorfilters.android

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.preference.PreferenceManager
import net.twisterrob.colorfilters.android.app.R
import java.io.File

class App : Application() {

	override fun onCreate() {
		super.onCreate()
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
	}

	companion object {

		fun getShareableCachePath(context: Context, path: String): File =
			context.cacheDir.resolve(path)

		fun getShareableCacheUri(context: Context, file: File): Uri =
			FileProvider.getUriForFile(context, context.packageName, file)
	}
}
