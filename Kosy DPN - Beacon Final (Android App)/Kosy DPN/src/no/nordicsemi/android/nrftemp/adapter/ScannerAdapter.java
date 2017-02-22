package no.nordicsemi.android.nrftemp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nordic.android.kosydpnfinal.R;

public class ScannerAdapter extends CursorAdapter {
	private static final int COLUMN_ADDRESS = 1;
	private static final int COLUMN_NAME = 2;
	private static final int COLUMN_TEMP = 4;
	private static final int COLUMN_RSSI = 6;

	public ScannerAdapter(Context context, Cursor c) {
		super(context, c, false);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final View view = LayoutInflater.from(context).inflate(R.layout.scanner_list_row, parent, false);

		final ViewHolder holder = new ViewHolder();
		holder.mSignal = (ImageView) view.findViewById(R.id.signal);
		holder.mName = (TextView) view.findViewById(R.id.name);
		holder.mAddress = (TextView) view.findViewById(R.id.address);
		holder.mTemp = (TextView) view.findViewById(R.id.temp);
		view.setTag(holder);

		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		final ViewHolder holder = (ViewHolder) view.getTag();
		holder.mAddress.setText(cursor.getString(COLUMN_ADDRESS));
		holder.mName.setText(cursor.getString(COLUMN_NAME));
		holder.mTemp.setText(context.getResources().getString(R.string.temp, cursor.getFloat(COLUMN_TEMP)));
		holder.mSignal.setImageLevel(cursor.getInt(COLUMN_RSSI));
	}

	/** Temporary holder for views. Speeds up fast scrolling process */
	private class ViewHolder {
		private ImageView mSignal;
		private TextView mName;
		private TextView mAddress;
		private TextView mTemp;
	}
}
