package no.nordicsemi.android.nrftemp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.nordic.android.kosydpnfinal.R;

public class SensorAdapter extends CursorAdapter {
	/** Battery level that will cause the 'low battery' indicator to appear */
	private static final int CRITICAL_BATTERY = 10; // %

	private static final int COLUMN_NAME = 2;
	private static final int COLUMN_TEMP = 4;
	private static final int COLUMN_BATTERY = 5;
	private static final int COLUMN_RECENT = 7;
	
	private boolean beat = true; //me
	private Handler heartbeathandler = new Handler();
	private int y = 0;
	private int bpm;
	private ViewHolder hr;
	private static String sensorname;

	public SensorAdapter(Context context, Cursor c) {
		super(context, c, false);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final View view = LayoutInflater.from(context).inflate(R.layout.sensors_list_row, parent, false);

		final ViewHolder holder = new ViewHolder();
		holder.mName = (TextView) view.findViewById(R.id.name);
		holder.mStatus = (TextView) view.findViewById(R.id.status);
		holder.mTemp = (TextView) view.findViewById(R.id.temp);
		holder.mBattery = view.findViewById(R.id.battery);
		view.setTag(holder);

		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		final ViewHolder holder = (ViewHolder) view.getTag();
		
		hr = holder; //me
		
		holder.mName.setText(cursor.getString(COLUMN_NAME));
		sensorname = cursor.getString(COLUMN_NAME);

		if (1 == cursor.getInt(COLUMN_RECENT)) {
			holder.mStatus.setText(R.string.status_updated);
			holder.mStatus.setTextColor(Color.BLACK);
		} else {
			holder.mStatus.setText(R.string.status_not_found);
			holder.mStatus.setTextColor(Color.RED);
		}
		//holder.mTemp.setText(context.getResources().getString(R.string.temp, cursor.getFloat(COLUMN_TEMP)));
		
		holder.mTemp.setText(context.getResources().getString(R.string.battery, cursor.getInt(COLUMN_BATTERY)));
		
		final int battery = cursor.getInt(COLUMN_BATTERY);
		bpm = battery;
		if (battery == 0){
			holder.mBattery.setBackgroundResource(R.drawable.noheartbeat);
        }
        else {
        	holder.mBattery.setBackgroundResource(R.drawable.heartpicsmall);
        }
		//heartbeat.run();
		//holder.mBattery.setVisibility(battery <= CRITICAL_BATTERY ? View.VISIBLE : View.INVISIBLE);
	}

	/** Temporary holder for views. Speeds up fast scrolling process */
	private class ViewHolder {
		private TextView mName;
		private TextView mStatus;
		private TextView mTemp;
		private View mBattery;
	}
	
	
	private Runnable heartbeat = new Runnable() { //not in use right now
		@Override
		public void run() {
			y = ((60/bpm) * 1000)/2; //2 cycles
	        if (y < (500/3)) //set limit to 180
	        	y = 167;
	            if (beat){
	            	hr.mBattery.setVisibility(View.VISIBLE);
	            	beat = false;
	            }
	            else if (!beat){
	            	hr.mBattery.setVisibility(View.GONE);
	            	beat = true;
	            }
				heartbeathandler.postDelayed(heartbeat, y);
		}
	};
	
	public static String getsensorName() {
	    return sensorname;
	}
}
