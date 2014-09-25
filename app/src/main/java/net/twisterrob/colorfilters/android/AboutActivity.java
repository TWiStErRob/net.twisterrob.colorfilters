package net.twisterrob.colorfilters.android;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import static android.widget.ArrayAdapter.createFromResource;

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
        TextView feedback = (TextView) findViewById(R.id.about_feedback);
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{BuildConfig.EMAIL});
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.cf_about_feedback_subject,
                        getString(R.string.app_name)));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.cf_about_feedback_body,
                        BuildConfig.PACKAGE_NAME, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));
                startActivity(intent);
            }
        });
        TextView name = (TextView) findViewById(R.id.about_name);
        TextView version = (TextView) findViewById(R.id.about_version);
        version.setText(getString(R.string.cf_about_version,
                BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));
        TextView pkg = (TextView) findViewById(R.id.about_package);
        pkg.setSelected(true);
        pkg.setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        CharSequence currentItem = (CharSequence) l.getAdapter().getItem(position);
        new AlertDialog.Builder(this)
                .setTitle(currentItem)
                .setPositiveButton(android.R.string.ok, null)
                .setView(createContents(licenceContents[(int) id]))
                .create()
                .show()
        ;
    }

    private View createContents(CharSequence content) {
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.dialog_about_licence, null);
        TextView message = (TextView) view.findViewById(android.R.id.message);
        message.setText(content);
        message.setMovementMethod(LinkMovementMethod.getInstance());
        return view;
    }
}
