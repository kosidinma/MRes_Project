package no.nordicsemi.android.nrftemp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StopPeriodicScanBroadcastReceiver extends BroadcastReceiver {
	public static final String ACTION = "no.nordicsemi.android.nrftemp.STOP";

	@Override
	public void onReceive(Context context, Intent intent) {
		final Intent i = new Intent(context, ScanNowBroadcastReceiver.class);
		final PendingIntent operation = PendingIntent.getBroadcast(context, StartPeriodicScanBroadcastReceiver.START_REQUEST, i, 0);

		final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(operation);
	}

}
