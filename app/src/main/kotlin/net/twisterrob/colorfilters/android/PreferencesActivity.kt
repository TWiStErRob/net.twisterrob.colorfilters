package net.twisterrob.colorfilters.android

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceActivity

class PreferencesActivity : PreferenceActivity() {

	override fun onCreate(savedInstanceState: Bundle?) = super.onCreate(savedInstanceState).also {
		@Suppress("DEPRECATION") // looking for a replacement, this is the simplest I know
		addPreferencesFromResource(R.xml.preferences)
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	override fun isValidFragment(fragmentName: String) = false
}
