package no.nordicsemi.android.nrftemp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.nordic.android.kosydpnfinal.R;

public class SplashscreenActivity extends Activity {
	/** Splash screen duration time in milliseconds */
	private static final int DELAY = 2000;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splashscreen);

		// Jump to SensorsActivity after DELAY milliseconds 
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				final Intent intent = new Intent(SplashscreenActivity.this, starting_activity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
				finish();
			}
		}, DELAY);
	}

	@Override
	public void onBackPressed() {
		// do nothing. Protect from exiting the application when splash screen is shown
	}
}
