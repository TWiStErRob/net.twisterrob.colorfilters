package net.twisterrob.colorfilters.android.about

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ListActivity
import android.content.Intent
import android.content.pm.PackageManager.NameNotFoundException
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.ArrayAdapter.createFromResource
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView

class AboutActivity : ListActivity() {

	private lateinit var licenceContents: Array<CharSequence>

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_about)

		listView.apply {
			val header = layoutInflater.inflate(R.layout.inc_about_header, this, false)!!
			addHeaderView(header, null, false)
			val footer = layoutInflater.inflate(R.layout.inc_about_footer, this, false)!!
			addFooterView(footer, null, false)
		}
		listAdapter = createFromResource(this, R.array.cf_about_licences, android.R.layout.simple_list_item_1)

		licenceContents = resources.getTextArray(R.array.cf_about_licences_content)

		// views inside ListView.headerView
		findViewById<TextView>(R.id.about_feedback)!!.apply {
			setOnClickListener {
				val intent = Intent(Intent.ACTION_VIEW).apply {
					data = Uri.parse("mailto:" + BuildConfig.EMAIL)
					putExtra(Intent.EXTRA_EMAIL, arrayOf(BuildConfig.EMAIL))
					val subject = getString(
						R.string.cf_about_feedback_subject,
						getString(applicationInfo.labelRes)
					)
					putExtra(Intent.EXTRA_SUBJECT, subject)
					val body = getString(
						R.string.cf_about_feedback_body,
						applicationContext.packageName, versionName
					)
					putExtra(Intent.EXTRA_TEXT, body)
				}
				startActivity(intent)
			}
		}
		findViewById<TextView>(R.id.about_name)!!.apply {
			setText(applicationInfo.labelRes)
			isSelected = true
		}
		findViewById<TextView>(R.id.about_version)!!.apply {
			text = getString(R.string.cf_about_version, versionName)
			isSelected = true
		}
		findViewById<TextView>(R.id.about_package)!!.apply {
			text = applicationContext.packageName
			isSelected = true
			visibility = if (resources.getBoolean(R.bool.in_test)) View.VISIBLE else View.GONE
		}
		findViewById<ImageView>(R.id.about_icon)!!.apply {
			setImageResource(applicationInfo.icon)
		}
	}

	private val versionName: String
		get() = try {
			applicationContext.packageManager.getPackageInfo(packageName, 0).versionName
		} catch (e: NameNotFoundException) {
			"error"
		}

	override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
		val currentItem = l.adapter.getItem(position) as CharSequence
		AlertDialog.Builder(this).apply {
			setTitle(currentItem)
			setPositiveButton(android.R.string.ok, null)
			setView(createContents(licenceContents[id.toInt()]))
			create()
		}.show()
	}

	private fun createContents(content: CharSequence): View {
		@SuppressLint("InflateParams")
		val view = layoutInflater.inflate(R.layout.dialog_about_licence, null)!!
		view.findViewById<TextView>(android.R.id.message)!!.apply {
			text = content
			movementMethod = LinkMovementMethod.getInstance()
		}
		return view
	}
}
