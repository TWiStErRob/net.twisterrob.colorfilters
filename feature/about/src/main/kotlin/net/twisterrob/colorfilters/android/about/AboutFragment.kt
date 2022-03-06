package net.twisterrob.colorfilters.android.about

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import androidx.fragment.app.ListFragment
import net.twisterrob.colorfilters.android.about.databinding.DialogAboutLicenceBinding
import net.twisterrob.colorfilters.android.about.databinding.IncAboutAppBinding
import net.twisterrob.colorfilters.android.about.databinding.IncAboutFooterBinding
import net.twisterrob.colorfilters.android.about.databinding.IncAboutHeaderBinding

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
			val list = inflater.inflate(R.layout.about_fragment_list, parent, false)
			parent.replace(original, list)
		}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val header = IncAboutHeaderBinding.inflate(layoutInflater, listView, false)
		listView.addHeaderView(header.root, null, false)
		setupHeader(header.aboutAbout)

		listAdapter = ArrayAdapter.createFromResource(
			listView.context,
			R.array.cf_about_licences,
			android.R.layout.simple_list_item_1
		)

		licenceContents = listView.resources.getTextArray(R.array.cf_about_licences_content)

		val footer = IncAboutFooterBinding.inflate(layoutInflater, listView, false)
		listView.addFooterView(footer.root, null, false)
	}

	private fun setupHeader(binding: IncAboutAppBinding) {
		val context = binding.root.context
		binding.aboutFeedback.setOnClickListener {
			val intent = Intent(Intent.ACTION_VIEW).apply {
				data = Uri.parse("mailto:" + BuildConfig.EMAIL)
				putExtra(Intent.EXTRA_EMAIL, arrayOf(BuildConfig.EMAIL))
				val subject = getString(
					R.string.cf_about_feedback_subject,
					getString(context.applicationInfo.labelRes)
				)
				putExtra(Intent.EXTRA_SUBJECT, subject)
				val body = getString(
					R.string.cf_about_feedback_body,
					context.applicationContext.packageName, context.applicationContext.versionName
				)
				putExtra(Intent.EXTRA_TEXT, body)
			}
			try {
				startActivity(intent)
			} catch (ex: ActivityNotFoundException) {
				Log.w("ColorFilters", "Cannot send email via intent.", ex)
				Toast.makeText(
					requireContext(),
					getString(R.string.cf_about_feedback_fail, BuildConfig.EMAIL),
					Toast.LENGTH_LONG
				).show()
			}
		}
		binding.aboutName.apply {
			setText(context.applicationInfo.labelRes)
			isSelected = true
		}
		binding.aboutVersion.apply {
			text = getString(R.string.cf_about_version, context.applicationContext.versionName)
			isSelected = true
		}
		binding.aboutPackage.apply {
			text = context.applicationContext.packageName
			isSelected = true
			visibility = if (resources.getBoolean(R.bool.in_test)) View.VISIBLE else View.GONE
		}
		binding.aboutIcon.apply {
			setImageResource(context.applicationInfo.icon)
		}
	}

	override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
		val currentItem = l.adapter.getItem(position) as CharSequence
		AlertDialog.Builder(requireActivity())
			.setTitle(currentItem)
			.setPositiveButton(android.R.string.ok, null)
			.setView(createContents(licenceContents[id.toInt()]))
			.show()
	}

	private fun createContents(content: CharSequence): View =
		DialogAboutLicenceBinding.inflate(layoutInflater)
			.apply {
				message.text = content
				message.movementMethod = LinkMovementMethod.getInstance()
			}
			.root
}

private val Context.versionName: String
	get() = try {
		packageManager.getPackageInfo(packageName, 0).versionName
	} catch (e: PackageManager.NameNotFoundException) {
		"error"
	}

private fun ViewGroup.replace(original: View, replacement: View) {
	val index = this.children.indexOf(original)
	this.removeViewAt(index)
	this.addView(replacement, index)
}
