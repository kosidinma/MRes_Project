package no.nordicsemi.android.nrftemp.adapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import no.nordicsemi.android.nrftemp.SensorsActivity;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.nordic.android.kosydpnfinal.R;

public class SensorDataAdapter extends CursorAdapter {
	private static final int COLUMN_TIMESTAMP = 1;
	private static final int COLUMN_TEMP = 2;
	

	public SensorDataAdapter(Context context, Cursor c) {
		super(context, c, false);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		
		final View view = LayoutInflater.from(context).inflate(R.layout.sensor_data_list_row, parent, false);

		final ViewHolder holder = new ViewHolder();
		holder.mTimestamp = (TextView) view.findViewById(R.id.timestamp);
		holder.mTemp = (TextView) view.findViewById(R.id.temp);
		view.setTag(holder);
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		final ViewHolder holder = (ViewHolder) view.getTag();
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(cursor.getLong(COLUMN_TIMESTAMP));
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH); // Note: zero based!
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		int millis = calendar.get(Calendar.MILLISECOND);
		
		//holder.mTimestamp.setText(String.format(Locale.US, "%1$ta, %1$td-%1$tb-%1$tY at %1$tR", calendar));
		holder.mTimestamp.setText(String.format("%d-%02d-%02d at  %02d:%02d:%02d.%03d", year, month , day, hour, minute, second, millis));
		holder.mTemp.setText(context.getResources().getString(R.string.temp, cursor.getFloat(COLUMN_TEMP)));
	}

	@Override
	public boolean areAllItemsEnabled() {
		// disable all items
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		// disable all items
		return false;
	}

	/** Temporary holder for views. Speeds up fast scrolling process */
	private class ViewHolder {
		private TextView mTimestamp;
		private TextView mTemp;
	}
	

	
}
