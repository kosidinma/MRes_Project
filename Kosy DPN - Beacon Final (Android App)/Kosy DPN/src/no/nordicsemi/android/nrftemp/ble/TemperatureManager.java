package no.nordicsemi.android.nrftemp.ble;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import no.nordicsemi.android.nrftemp.SensorsActivity;
import no.nordicsemi.android.nrftemp.adapter.SensorAdapter;
import no.nordicsemi.android.nrftemp.ble.parser.TemperatureData;
import no.nordicsemi.android.nrftemp.ble.parser.TemperatureDataParser;
import no.nordicsemi.android.nrftemp.database.DatabaseHelper;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;

public class TemperatureManager implements BluetoothAdapter.LeScanCallback {
	/** An minimal interval between each data row in the database for a single device */
	private static final long DATA_INTERVAL = 150L;//1800L;//60 * 5 * 1000L; // ms 

	private BluetoothAdapter mBluetoothAdapter;
	private DatabaseHelper mDatabaseHelper;

	private List<TemperatureManagerCallback> mListeners;
	
	private double bpm_val = 0 ;
	private double bpm_val2 = 0 ;
	private double bpm_val3 = 0 ;
	private double ping_val = 0;
	private double last_val = 9;
	private double ping_time = 0;
	private double[] time_dif = new double[7]; //7 different time differences
	private double ping_val2 = 0;
	private double last_val2 = 9;
	private double ping_time2 = 0;
	private double[] time_dif2 = new double[7]; //7 different time differences
	private double ping_val3 = 0;
	private double last_val3 = 9;
	private double ping_time3 = 0;
	private double[] time_dif3 = new double[7]; //7 different time differences
	private double heartrate = 0;
	private double heartrate2 = 0;
	private double heartrate3 = 0;
	File file;
	private static String FILENAME; //file to save data
	private static String FILENAME2; //file to save data
	private static String FILENAME3; //file to save data
	int go = 0;
	int go2 = 0;
	int go3 = 0;
	int file_it = 0;
	long now1 = 0;
	long now2 = 0;
	long now3 = 0;
	long peak_dif1 = 0;
	long peak_dif2 = 0;
	String KM1 = new String("Kosy_MRes");
	String KM2 = new String("Kosy_MRes2");
	String KM3 = new String("Kosy_MRes3");
	String date_time ;
	String hrdata;
	String hrbpm;
	String hrdata2;
	String hrbpm2;
	String hrdata3;
	String hrbpm3;
	

	public TemperatureManager(final Context context, final DatabaseHelper databaseHelper) {
		final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		mDatabaseHelper = databaseHelper;

		mListeners = new ArrayList<TemperatureManagerCallback>(2);
	}

	public void addCallback(final TemperatureManagerCallback callback) {
		mListeners.add(callback);
	}

	public void removeCallback(final TemperatureManagerCallback callback) {
		mListeners.remove(callback);
	}

	private void fireOnDevicesScanned() {
		for (TemperatureManagerCallback callback : mListeners)
			callback.onDevicesScaned();
	}

	private void fireOnRssiUpdate(final long sensorId, final int rssi) {
		for (TemperatureManagerCallback callback : mListeners)
			callback.onRssiUpdate(sensorId, rssi);
	}

	/**
	 * Starts scanning for temperature data. Call {@link #stopScan()} when done to save the power.
	 */
	public void startScan() {
		mBluetoothAdapter.startLeScan(this);
	}

	/**
	 * Starts scanning for temperature data. Call {@link #stopScan()} when done to save the power.
	 */
	public void startScan(final long period) {
		mBluetoothAdapter.startLeScan(this);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				stopScan();
			}
		}, period);
	}

	/**
	 * Stops scanning for temperature data from BLE sensors.
	 */
	public void stopScan() {
		mBluetoothAdapter.stopLeScan(this);
	}

	@Override
	public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
		final TemperatureData td = TemperatureDataParser.parse(device, scanRecord);
		if (!td.isValid())
			return;

		final long now = Calendar.getInstance().getTimeInMillis();
		final long then = mDatabaseHelper.getLastTimestamp(td.getAddress());

		if (now - then > DATA_INTERVAL) {
			if (device.getName().equals(KM1))
			{
				file_it = 1;
			//go = 5;
			//decatenation(td.getTemp(), td.getTemp2());
			//temps[6] = bpm();
			//while (go>-1) //done like this totype in right way
			//{
			//	try {
			//		Thread.sleep(100);
			ping_val = td.getTemp();
					if(ping_val != last_val && ping_val !=0)
					{
						now1 = Calendar.getInstance().getTimeInMillis();
						if (go > 6)
						{
							go = 0;
							bpm_val = bpm();
						}
						time_dif[go] = now1 - ping_time;
						last_val = ping_val;
						mDatabaseHelper.addNewSensorData(device.getAddress(), device.getName(), now1, ping_val, (int) bpm_val);//td.getBattery());
						filedata();
						ping_time = now1;
						peak_dif1 = now2 - now1;
						peak_dif2 = now3 - now1;
						go++;
					}
			//		go--;
			//		Thread.sleep(100);
			//	} catch (InterruptedException e) {
			//		// TODO Auto-generated catch block
			//		e.printStackTrace();
			//	}
			//}
			}
			
			 if (device.getName().equals(KM2))
			{
				file_it = 2;
			//go = 5;
			//decatenation(td.getTemp(), td.getTemp2());
			//temps[6] = bpm();
			//while (go>-1) //done like this totype in right way
			//{
			//	try {
			//		Thread.sleep(100);
			ping_val2 = td.getTemp();
			System.out.println(ping_val2);
					try {
						if(ping_val2 != last_val2 && ping_val2 !=0)
						{
							System.out.println(ping_val2);
							now2 = Calendar.getInstance().getTimeInMillis();
							if (go2 > 6)
							{
								go2 = 0;
								bpm_val2 = bpm2();
							}
							time_dif2[go2] = now2 - ping_time2;
							last_val2 = ping_val2;
							mDatabaseHelper.addNewSensorData(device.getAddress(), device.getName(), now2, ping_val2, (int) bpm_val2);//td.getBattery());
							filedata2();
							ping_time2 = now2;
							go2++;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			//		go--;
			//		Thread.sleep(100);
			//	} catch (InterruptedException e) {
			//		// TODO Auto-generated catch block
			//		e.printStackTrace();
			//	}
			//}
			}
			
			 if (device.getName().equals(KM3))
			{
				file_it = 3;
			//go = 5;
			//decatenation(td.getTemp(), td.getTemp2());
			//temps[6] = bpm();
			//while (go>-1) //done like this totype in right way
			//{
			//	try {
			//		Thread.sleep(100);
			ping_val3 = td.getTemp();
					if(ping_val3 != last_val3 && ping_val3 !=0)
					{
						now3 = Calendar.getInstance().getTimeInMillis();
						if (go3 > 6)
						{
							go3 = 0;
							bpm_val3 = bpm3();
						}
						time_dif3[go3] = now3 - ping_time3;
						last_val3 = ping_val3;
						mDatabaseHelper.addNewSensorData(device.getAddress(), device.getName(), now3, ping_val3, (int) bpm_val3);//td.getBattery());
						filedata3();
						ping_time3 = now3;
						go3++;
					}
			//		go--;
			//		Thread.sleep(100);
			//	} catch (InterruptedException e) {
			//		// TODO Auto-generated catch block
			//		e.printStackTrace();
			//	}
			//}
			}
			fireOnDevicesScanned();
		}
		final long sensorId = mDatabaseHelper.findSensor(device.getAddress());
		final int rssiPercent = (int) (100.0f * (127.0f + rssi) / 127.0f + 20.0f);
		mDatabaseHelper.updateSensorRssi(sensorId, rssiPercent);
		fireOnRssiUpdate(sensorId, rssiPercent);
	}

	/**
	 * Return <code>true</code> if Bluetooth is currently enabled and ready for use.
	 * 
	 * @return <code>true</code> if the local adapter is turned on
	 */
	public boolean isEnabled() {
		return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
	}
	
//	private void decatenation(double x, double y)
//	{
//		for (int u = 0; u <6; u++)
//		{
//			temps[u] = 0; //populate temps
//		}
//		if (x<100) //1 value
//		{
//			temps[0] = x;
//		}
//		else if  (x > 99 && x < 10000) //2 values
//		{
//			temps[1] = (int) x/100;
//			temps[0] = (int) x % 100;
//		}
//		else if  (x > 9999 && x < 1000000) //3 values
//		{
//			temps[2] = (int) x/10000;
//			int b = (int) x % 10000;
//			temps[1] = (int) b / 100;
//			temps[0] = (int) b % 100;
//		}
//		if (y<100) //4 values
//		{
//			temps[3] = y;
//		}
//		else if  (y > 99 && y < 10000) //5 values
//		{
//			temps[4] = (int) y/100;
//			temps[3] = (int) y % 100;
//		}
//		else if  (y > 9999 && y < 1000000) //6 values
//		{
//			temps[5] = (int) y/10000;
//			int b = (int) y % 10000;
//			temps[4] = (int) b / 100;
//			temps[3] = (int) b % 100;
//		}
//	}
	
//	public double bpm()
//	{
//		int nonzeros = 0;
//		for (int u = 1; u <6; u++) //temps 0 isn't a difference
//		{
//			if (temps[u] != 0) //populate temps
//				nonzeros++;
//		}
//		heartrate = 60/(((temps[1] + temps[2] + temps[3] + temps[4] + temps[5]) * 0.05)/nonzeros); //20 datasets per second means each data is 50ms
//		//heartrate = frequency * 60 seconds;
//		if (avghr == 0 && (heartrate < 32 || heartrate > 33))
//			avghr = heartrate;
//		else if (heartrate < 32 || heartrate > 33)
//			avghr = (heartrate + avghr)/2;
//		else if (heartrate >= 32 && heartrate <= 33)
//			avghr = 0;
//		return avghr;
//	}
	
	public double bpm()
	{
		double ab = 0;
		//7 pings = 6 time differences
		for (int u = 0; u <7; u++) //temps 0 isn't a difference
		{
			ab = ab + time_dif[u];
		}
		heartrate = 420000/ab; //bpm = 60000/millisecond....millisecond = ab/5
		//heartrate = frequency * 60 seconds;
		return heartrate;
	}
	
	public double bpm2()
	{
		double ab = 0;
		//7 pings = 6 time differences
		for (int u = 0; u <7; u++) //temps 0 isn't a difference
		{
			ab = ab + time_dif2[u];
		}
		heartrate2 = 420000/ab; //bpm = 60000/millisecond....millisecond = ab/5
		//heartrate = frequency * 60 seconds;
		return heartrate2;
	}
	
	public double bpm3()
	{
		double ab = 0;
		//7 pings = 6 time differences
		for (int u = 0; u <7; u++) //temps 0 isn't a difference
		{
			ab = ab + time_dif3[u];
		}
		heartrate3 = 420000/ab; //bpm = 60000/millisecond....millisecond = ab/5
		//heartrate = frequency * 60 seconds;
		return heartrate3;
	}
	
	private void filedata()
	{
		final String PatientName = SensorsActivity.getPatientName();
		final String sensorname = SensorAdapter.getsensorName();
		FILENAME = new StringBuilder().append(PatientName).append("_").append("Kosy_MRes").append(".txt").toString();
		final Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH); // Note: zero based!
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		int millis = calendar.get(Calendar.MILLISECOND);
		date_time = String.format("%d-%02d-%02d at  %02d:%02d:%02d.%03d", year, month , day, hour, minute, second, millis);
		hrdata =  Double.toString(ping_val);
		hrbpm = Double.toString((int)bpm_val);
		
		writeToFile();
	}
	
	private void writeToFile() { ///write data to txt file
		FileOutputStream outputStream;
		try {
		    file = new File(Environment.getExternalStorageDirectory(), FILENAME);
		    outputStream = new FileOutputStream(file,true); //append file
		    outputStream.write(date_time.getBytes());
		    outputStream.write("             ".getBytes());
		    outputStream.write(hrdata.getBytes());
		    outputStream.write("             BPM = ".getBytes());
		    outputStream.write(hrbpm.getBytes());
		    outputStream.write("\n".getBytes());
		    outputStream.write("Peak_Difference1 = ".getBytes());
		    outputStream.write(Long.toString(peak_dif1).getBytes());
		    outputStream.write("\n".getBytes());
		    outputStream.write("Peak_Difference2 = ".getBytes());
		    outputStream.write(Long.toString(peak_dif2).getBytes());
		    outputStream.write("\n".getBytes());
		    outputStream.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
    }
	
	private void filedata2()
	{
		final String PatientName = SensorsActivity.getPatientName();
		final String sensorname = SensorAdapter.getsensorName();
		FILENAME2 = new StringBuilder().append(PatientName).append("_").append("Kosy_MRes2").append(".txt").toString();
		final Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH); // Note: zero based!
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		int millis = calendar.get(Calendar.MILLISECOND);
		date_time = String.format("%d-%02d-%02d at  %02d:%02d:%02d.%03d", year, month , day, hour, minute, second, millis);
		hrdata2 =  Double.toString(ping_val2);
		hrbpm2 = Double.toString((int)bpm_val2);
		writeToFile2();
	}
	
	private void writeToFile2() { ///write data to txt file
		FileOutputStream outputStream;
		try {
		    file = new File(Environment.getExternalStorageDirectory(), FILENAME2);
		    outputStream = new FileOutputStream(file,true); //append file
		    outputStream.write(date_time.getBytes());
		    outputStream.write("             ".getBytes());
		    outputStream.write(hrdata2.getBytes());
		    outputStream.write("             BPM = ".getBytes());
		    outputStream.write(hrbpm2.getBytes());
		    outputStream.write("\n".getBytes());
		    outputStream.write("Peak_Difference1 = ".getBytes());
		    outputStream.write(Long.toString(peak_dif1).getBytes());
		    outputStream.write("\n".getBytes());
		    outputStream.write("Peak_Difference2 = ".getBytes());
		    outputStream.write(Long.toString(peak_dif2).getBytes());
		    outputStream.write("\n".getBytes());
		    outputStream.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
    }
	
	private void filedata3()
	{
		final String PatientName = SensorsActivity.getPatientName();
		final String sensorname = SensorAdapter.getsensorName();
		FILENAME3 = new StringBuilder().append(PatientName).append("_").append("Kosy_MRes3").append(".txt").toString();
		final Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH); // Note: zero based!
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		int millis = calendar.get(Calendar.MILLISECOND);
		date_time = String.format("%d-%02d-%02d at  %02d:%02d:%02d.%03d", year, month , day, hour, minute, second, millis);
		hrdata3 =  Double.toString(ping_val3);
		hrbpm3 = Double.toString((int)bpm_val3);
		writeToFile3();
	}
	
	private void writeToFile3() { ///write data to txt file
		FileOutputStream outputStream;
		try {
		    file = new File(Environment.getExternalStorageDirectory(), FILENAME3);
		    outputStream = new FileOutputStream(file,true); //append file
		    outputStream.write(date_time.getBytes());
		    outputStream.write("             ".getBytes());
		    outputStream.write(hrdata3.getBytes());
		    outputStream.write("             BPM = ".getBytes());
		    outputStream.write(hrbpm3.getBytes());
		    outputStream.write("\n".getBytes());
		    outputStream.write("Peak_Difference1 = ".getBytes());
		    outputStream.write(Long.toString(peak_dif1).getBytes());
		    outputStream.write("\n".getBytes());
		    outputStream.write("Peak_Difference2 = ".getBytes());
		    outputStream.write(Long.toString(peak_dif2).getBytes());
		    outputStream.write("\n".getBytes());
		    outputStream.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
    }
	
	
	
	public static boolean canWriteOnExternalStorage() {	//check if user has permissions to write on external storage or not
		   // get the state of your external storage
		   String state = Environment.getExternalStorageState();
		   if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // if storage is mounted return true
		      //Log.v("sTag", "Yes, can write to external storage.");
		      return true;
		   }
		   return false;
		}
}
