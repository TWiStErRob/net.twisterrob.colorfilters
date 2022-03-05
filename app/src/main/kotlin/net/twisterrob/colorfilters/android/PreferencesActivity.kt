package net.twisterrob.colorfilters.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat

class PreferencesActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_settings)
		if (savedInstanceState == null) {
			supportFragmentManager
				.beginTransaction()
				.add(R.id.prefs_container, PreferencesFragment())
				.commit()
		}
	}

	class PreferencesFragment : PreferenceFragmentCompat() {
		override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
			setPreferencesFromResource(R.xml.preferences, rootKey)
		}
	}
}
