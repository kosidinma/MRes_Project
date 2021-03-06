package no.nordicsemi.android.nrftemp.fragment;

import no.nordicsemi.android.nrftemp.SensorsActivity;
import no.nordicsemi.android.nrftemp.adapter.ScannerAdapter;
import no.nordicsemi.android.nrftemp.ble.TemperatureManagerCallback;
import no.nordicsemi.android.nrftemp.database.DatabaseHelper;
import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.nordic.android.kosydpnfinal.R;

public class ScannerFragment extends ListFragment implements TemperatureManagerCallback {
	private static final String TAG = "ScannerFragment";

	private ScannerFragmentListener mListener;

	private DatabaseHelper mDatabaseHelper;
	private CursorAdapter mAdapter;

	/**
	 * Returns a new instance of the fragment. Passing arguments to the fragment instance, if needed, should be here.
	 * 
	 * @return a new instance of the fragment.
	 */
	public interface ScannerFragmentListener {
		public void setScannerVisible(final boolean visible);

		public void onAddSensor(final long sensorId);

		public void onUnregisterCallback(final TemperatureManagerCallback callback);

		public void onBackPressed();
	}

	public static ScannerFragment getInstance() {
		final ScannerFragment fragment = new ScannerFragment();

		return fragment;
	}

	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);

		try {
			mListener = (ScannerFragmentListener) activity;
			mListener.setScannerVisible(true);
		} catch (final ClassCastException e) {
			Log.d(TAG, "The parent Activity must implement ScannerFragmentListener interface");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();

		mListener.setScannerVisible(false);
		mListener = null;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final SensorsActivity activity = (SensorsActivity) getActivity();
		mDatabaseHelper = activity.getDatabaseHelper();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_scanner, container, false);

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onBackPressed();
			}
		});

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mDatabaseHelper.removeAllNewSensors();
		setListAdapter(mAdapter = new ScannerAdapter(getActivity(), mDatabaseHelper.getAllNewSensors()));
	}

	@Override
	public void onStop() {
		super.onStop();

		mListener.onUnregisterCallback(this);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			mListener.onBackPressed();
			break;
		}
		return true;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		mListener.onAddSensor(id);
	}

	@Override
	public void onDevicesScaned() {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (getActivity() == null)
					return;

				mAdapter.changeCursor(mDatabaseHelper.getAllNewSensors());
			}
		});
	}

	@Override
	public void onRssiUpdate(long id, int rssi) {
		onDevicesScaned();
	}
}
