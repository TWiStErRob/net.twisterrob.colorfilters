package net.twisterrob.colorfilters.android.about

import android.annotation.SuppressLint
import android.app.AlertDialog.Builder
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.NameNotFoundException
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.ListFragment
import net.twisterrob.colorfilters.android.about.R.array
import net.twisterrob.colorfilters.android.about.R.bool
import net.twisterrob.colorfilters.android.about.R.layout
import net.twisterrob.colorfilters.android.about.R.string

class AboutFragment : ListFragment() {

	private lateinit var licenceContents: Array<CharSequence>

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View =
		super.onCreateView(inflater, container, savedInstanceState)!!.apply {
			val original = findViewById<View>(android.R.id.list)
			val parent = original.parent as ViewGroup
			val list = inflater.inflate(layout.about_fragment_list, parent, false)
			parent.replace(original, list)
		}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		val context = requireContext()
		super.onViewCreated(view, savedInstanceState)
		listView.apply {
			val header = layoutInflater.inflate(layout.inc_about_header, this, false)!!
			addHeaderView(header, null, false)
			val footer = layoutInflater.inflate(layout.inc_about_footer, this, false)!!
			addFooterView(footer, null, false)
		}
		listAdapter = ArrayAdapter.createFromResource(
			context,
			array.cf_about_licences,
			android.R.layout.simple_list_item_1
		)

		licenceContents = resources.getTextArray(array.cf_about_licences_content)

		// views inside ListView.headerView
		view.findViewById<TextView>(R.id.about_feedback)!!.apply {
			setOnClickListener {
				val intent = Intent(Intent.ACTION_VIEW).apply {
					data = Uri.parse("mailto:" + BuildConfig.EMAIL)
					putExtra(Intent.EXTRA_EMAIL, arrayOf(BuildConfig.EMAIL))
					val subject = getString(
						string.cf_about_feedback_subject,
						getString(context.applicationInfo.labelRes)
					)
					putExtra(Intent.EXTRA_SUBJECT, subject)
					val body = getString(
						string.cf_about_feedback_body,
						context.packageName, context.applicationContext.versionName
					)
					putExtra(Intent.EXTRA_TEXT, body)
				}
				startActivity(intent)
			}
		}
		view.findViewById<TextView>(R.id.about_name)!!.apply {
			setText(context.applicationInfo.labelRes)
			isSelected = true
		}
		view.findViewById<TextView>(R.id.about_version)!!.apply {
			text = getString(string.cf_about_version, context.applicationContext.versionName)
			isSelected = true
		}
		view.findViewById<TextView>(R.id.about_package)!!.apply {
			text = context.applicationContext.packageName
			isSelected = true
			visibility = if (resources.getBoolean(bool.in_test)) View.VISIBLE else View.GONE
		}
		view.findViewById<ImageView>(R.id.about_icon)!!.apply {
			setImageResource(context.applicationInfo.icon)
		}
	}

	override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
		val currentItem = l.adapter.getItem(position) as CharSequence
		Builder(requireActivity()).apply {
			setTitle(currentItem)
			setPositiveButton(android.R.string.ok, null)
			setView(createContents(licenceContents[id.toInt()]))
			create()
		}.show()
	}

	private fun createContents(content: CharSequence): View {
		@SuppressLint("InflateParams")
		val view = layoutInflater.inflate(layout.dialog_about_licence, null)!!
		view.findViewById<TextView>(android.R.id.message)!!.apply {
			text = content
			movementMethod = LinkMovementMethod.getInstance()
		}
		return view
	}
}

private val Context.versionName: String
	get() = try {
		packageManager.getPackageInfo(packageName, 0).versionName
	} catch (e: NameNotFoundException) {
		"error"
	}

private fun ViewGroup.replace(original: View, replacement: View) {
	val index = this.children.indexOf(original)
	this.removeViewAt(index)
	this.addView(replacement, index)
}
