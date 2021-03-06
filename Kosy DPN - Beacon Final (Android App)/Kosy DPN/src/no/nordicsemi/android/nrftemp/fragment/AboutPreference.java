package no.nordicsemi.android.nrftemp.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.Preference;
import android.util.AttributeSet;

import com.nordic.android.kosydpnfinal.R;

public class AboutPreference extends Preference {

	public AboutPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AboutPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onClick() {
		new AlertDialog.Builder(getContext()).setTitle(R.string.about_title).setMessage(R.string.about_text).setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).show();
	}
}
