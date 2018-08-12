package net.twisterrob.colorfilters.android

import android.app.Application
import android.preference.PreferenceManager

class App : Application() {

	override fun onCreate() = super.onCreate().also {
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
	}
}
