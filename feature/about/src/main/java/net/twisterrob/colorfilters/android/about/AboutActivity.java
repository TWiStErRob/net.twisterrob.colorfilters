package net.twisterrob.colorfilters.android.about;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.*;

import static android.widget.ArrayAdapter.*;

public class AboutActivity extends ListActivity {
	private CharSequence[] licenceContents;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		ListView list = getListView();
		list.addHeaderView(getLayoutInflater().inflate(R.layout.inc_about_header, list, false), null, false);
		list.addFooterView(getLayoutInflater().inflate(R.layout.inc_about_footer, list, false), null, false);

		setListAdapter(createFromResource(this, R.array.cf_about_licences, android.R.layout.simple_list_item_1));
		licenceContents = getResources().getTextArray(R.array.cf_about_licences_content);

		// views inside ListView.headerView
		TextView feedback = findViewById(R.id.about_feedback);
		feedback.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("mailto:" + BuildConfig.EMAIL));
				intent.putExtra(Intent.EXTRA_EMAIL, new String[] {BuildConfig.EMAIL});
				intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.cf_about_feedback_subject,
						getString(getApplicationInfo().labelRes)));
				intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.cf_about_feedback_body,
						BuildConfig.APPLICATION_ID, BuildConfig.VERSION_NAME));
				startActivity(intent);
			}
		});
		TextView name = findViewById(R.id.about_name);
		name.setText(getApplicationInfo().labelRes);
		name.setSelected(true);
		TextView version = findViewById(R.id.about_version);
		version.setText(getString(R.string.cf_about_version, getVersionName()));
		TextView pkg = findViewById(R.id.about_package);
		pkg.setText(getApplicationContext().getPackageName());
		version.setSelected(true);
		pkg.setSelected(true);
		pkg.setVisibility(getResources().getBoolean(R.bool.in_test)? View.VISIBLE : View.GONE);
		ImageView icon = findViewById(R.id.about_icon);
		icon.setImageResource(getApplicationInfo().icon);
	}

	private String getVersionName() {
		try {
			return getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			return "error";
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		CharSequence currentItem = (CharSequence)l.getAdapter().getItem(position);
		new AlertDialog.Builder(this)
				.setTitle(currentItem)
				.setPositiveButton(android.R.string.ok, null)
				.setView(createContents(licenceContents[(int)id]))
				.create()
				.show()
		;
	}

	private View createContents(CharSequence content) {
		@SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.dialog_about_licence, null);
		TextView message = view.findViewById(android.R.id.message);
		message.setText(content);
		message.setMovementMethod(LinkMovementMethod.getInstance());
		return view;
	}
}
