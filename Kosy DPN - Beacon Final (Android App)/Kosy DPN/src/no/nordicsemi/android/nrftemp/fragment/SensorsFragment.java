package no.nordicsemi.android.nrftemp.fragment;

import no.nordicsemi.android.nrftemp.SensorsActivity;
import no.nordicsemi.android.nrftemp.adapter.SensorAdapter;
import no.nordicsemi.android.nrftemp.ble.TemperatureManagerCallback;
import no.nordicsemi.android.nrftemp.database.DatabaseHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.nordic.android.kosydpnfinal.R;

public class SensorsFragment extends ListFragment implements TemperatureManagerCallback {
	public static final String TAG = "SensorsFragment";

	private SensorFragmentListener mListener;

	private DatabaseHelper mDatabaseHelper;
	private CursorAdapter mAdapter;

	public interface SensorFragmentListener {
		public void onSettingsOpened();

		public void onScanForSensors();

		public void onShowSensorData(final long sensorId, final boolean focus);
	}

	/**
	 * Returns a new instance of the fragment. Passing arguments to the fragment instance, if needed, should be here.
	 * 
	 * @return a new instance of the fragment.
	 */
	public static SensorsFragment getInstence() {
		final SensorsFragment fragment = new SensorsFragment();

		// pass some arguments to the fragment here if required

		return fragment;
	}

	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);

		try {
			mListener = (SensorFragmentListener) activity;
		} catch (final ClassCastException e) {
			Log.d(TAG, "The parent Activity must implement SensorFragmentListener interface");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();

		mListener = null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final SensorsActivity activity = (SensorsActivity) getActivity();
		mDatabaseHelper = activity.getDatabaseHelper();
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_sensors, container, false);

		setHasOptionsMenu(true);

		// we are in main fragment, remove 'home as up' if set
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);

		return view;
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setListAdapter(mAdapter = new SensorAdapter(getActivity(), mDatabaseHelper.getAllStoredSensors()));

		// attach a click listener to the list empty view
		getListView().getEmptyView().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onScanForSensors();
			}
		});
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.sensors_list, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add:
			mListener.onScanForSensors();
			break;
		case R.id.settings:
			mListener.onSettingsOpened();
			break;
		}
		return true;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, final long id) {
		//mListener.onShowSensorData(id, false); //THIS REMOVES THE SECOND SCREEN
		final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					final Bundle args = getArguments();
					final long sensorId = id;//args.getLong(ARG_SENSOR_ID);

					mDatabaseHelper.removeSensor(sensorId);
					//mListener.onBackPressed();
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					//No button clicked. Do nothing
					break;
				}
				dialog.dismiss();
			}
		};
		new AlertDialog.Builder(getActivity()).setMessage(R.string.alert_remove_sensor_msg).setPositiveButton(android.R.string.yes, dialogClickListener)
				.setNegativeButton(android.R.string.no, dialogClickListener).show();
	}

	@Override
	public void onDevicesScaned() {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (getActivity() == null)
					return;

				mAdapter.changeCursor(mDatabaseHelper.getAllStoredSensors());
				getActivity().invalidateOptionsMenu();
			}
		});
	}

	@Override
	public void onRssiUpdate(final long id, final int rssi) {
		// do nothing
	}
}
