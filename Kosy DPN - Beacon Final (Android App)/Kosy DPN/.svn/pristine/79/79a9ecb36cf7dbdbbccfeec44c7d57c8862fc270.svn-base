package no.nordicsemi.android.nrftemp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper {
	/** Database version */
	private static final int DATABASE_VERSION = 2;

	/** Database file name */
	private static final String DATABASE_NAME = "sensors.db";

	private interface Tables {
		/** Sensors table. See {@link SensorContract.Sensor} for column names */
		public static final String SENSORS = "sensors";
		/** Sensors data table. See {@link SensorContract.Sensor.Data} for column names */
		public static final String SENSOR_DATA = "sensor_data";
		/** Sensors RSSI table. Keeps the latest RSSI values of all devices. See {@link SensorContract.Sensor.Rssi} for column names */
		public static final String SENSOR_RSSI = "sensor_rssi";
	}

	private interface Views {
		/**
		 * The view that contains the last data of each sensor.
		 */
		public static final String SENSOR_LAST_DATA = "sensor_last_data";
	}

	// the last column contains 1 (true) if the timestamp is less than 5 minutes old. 0 otherwise. Method strftime(..) returns time in seconds, timestamp is in milliseconds
	private static final String[] LAST_DATA_PROJECTION = new String[] { SensorContract.Sensor._ID, SensorContract.Sensor.ADDRESS, SensorContract.Sensor.NAME, SensorContract.Sensor.Data.TIMESTAMP,
			SensorContract.Sensor.Data.TEMP, SensorContract.Sensor.Data.BATTERY, SensorContract.Sensor.Rssi.RSSI,
			SensorContract.Sensor.Data.TIMESTAMP + " > 1000 * strftime('%s','now', '-5 minutes') as " + SensorContract.Sensor.Data.RECENT };

	private static final String[] DATA_PROJECTION = new String[] { SensorContract.Sensor.Data._ID, SensorContract.Sensor.Data.TIMESTAMP, SensorContract.Sensor.Data.TEMP,
			SensorContract.Sensor.Data.BATTERY };

	private static final String[] SENSOR_ID_PROJECTION = new String[] { SensorContract.Sensor._ID };
	private static final String[] SENSOR_ID_STORED_PROJECTION = new String[] { SensorContract.Sensor._ID, SensorContract.Sensor.STORED };

	private static final String ONLY_STORED = SensorContract.Sensor.STORED + "=1";
	private static final String ONLY_NEW = SensorContract.Sensor.STORED + "=0";

	private SQLiteHelper mDatabaseHelper;
	private SQLiteDatabase mDatabase;
	private String[] mSingleArg = new String[1];

	public DatabaseHelper(Context context) {
		mDatabaseHelper = new SQLiteHelper(context);
		mDatabase = mDatabaseHelper.getWritableDatabase();
	}

	/**
	 * Returns the most recent data of all sensors from the database
	 */
	public Cursor getAllStoredSensors() {
		return mDatabase.query(Views.SENSOR_LAST_DATA, LAST_DATA_PROJECTION, ONLY_STORED, null, null, null, SensorContract.Sensor.Data.RECENT + " DESC," + SensorContract.Sensor.NAME + " ASC");
	}

	/**
	 * Returns the most recent data of all sensors from the database
	 */
	public Cursor getAllNewSensors() {
		return mDatabase.query(Views.SENSOR_LAST_DATA, LAST_DATA_PROJECTION, ONLY_NEW, null, null, null, SensorContract.Sensor._ID + " ASC");
	}

	/**
	 * Removes all devices that wasn't stored from the database
	 * 
	 * @return number of rows deleted
	 */
	public int removeAllNewSensors() {
		return mDatabase.delete(Tables.SENSORS, ONLY_NEW, null);
	}

	/**
	 * Removes all RSSI data from database
	 * 
	 * @return number of rows deleted
	 */
	public int removeRssi() {
		return mDatabase.delete(Tables.SENSOR_RSSI, null, null);
	}

	/**
	 * Returns the most recent data of the sensor with given id
	 * 
	 * @param sensorId
	 *            the sensor id in the database
	 */
	public Cursor getSensor(final long sensorId) {
		final String selection = SensorContract.Sensor._ID + "=?";
		mSingleArg[0] = String.valueOf(sensorId);

		return mDatabase.query(Views.SENSOR_LAST_DATA, LAST_DATA_PROJECTION, selection, mSingleArg, null, null, null);
	}

	/**
	 * Returns all captured records of data obtained from the given device
	 * 
	 * @param sensorId
	 *            the sensor id in the database
	 */
	public Cursor getSensorHistory(final long sensorId) {
		final String selection = SensorContract.Sensor.Data.SENSOR_ID + "=?";
		mSingleArg[0] = String.valueOf(sensorId);

		return mDatabase.query(Tables.SENSOR_DATA, DATA_PROJECTION, selection, mSingleArg, null, null, SensorContract.Sensor.Data.TIMESTAMP + " DESC");
	}

	/**
	 * Updates sensor name in the database
	 * 
	 * @param sensorId
	 *            the sensor id in the database
	 * @param newName
	 *            the new sensor name
	 * @return the number of rows affected
	 */
	public int updateSensorName(final long sensorId, final String newName) {
		final String selection = SensorContract.Sensor._ID + "=?";
		mSingleArg[0] = String.valueOf(sensorId);

		final ContentValues values = new ContentValues();
		values.put(SensorContract.Sensor.NAME, newName);

		return mDatabase.update(Tables.SENSORS, values, selection, mSingleArg);
	}

	/**
	 * Sets the sensor as stored on the device. From now on the historical data will be stored in the database
	 * 
	 * @param sensorId
	 *            the sensor id in the database
	 * @return the number of rows affected
	 */
	public int updateSensorAddToStored(final long sensorId) {
		final String selection = SensorContract.Sensor._ID + "=?";
		mSingleArg[0] = String.valueOf(sensorId);

		final ContentValues values = new ContentValues();
		values.put(SensorContract.Sensor.STORED, 1);

		return mDatabase.update(Tables.SENSORS, values, selection, mSingleArg);
	}

	/**
	 * Updates the sensor rssi status in the database.
	 * 
	 * @param sensorId
	 *            the sensor id from 'sensor' table
	 * @param rssi
	 *            the current RSSI value
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	public long updateSensorRssi(final long sensorId, final int rssi) {
		final ContentValues values = new ContentValues();
		values.put(SensorContract.Sensor.Rssi.SENSOR_ID, sensorId);
		values.put(SensorContract.Sensor.Rssi.RSSI, rssi);

		return mDatabase.replace(Tables.SENSOR_RSSI, null, values);
	}

	/**
	 * Removes the given sensor data from the database
	 * 
	 * @param sensorId
	 *            the sensor id in the database
	 * @return the number of sensors removed
	 */
	public int removeSensor(final long sensorId) {
		final String selection = SensorContract.Sensor._ID + "=?";
		final String selectionData = SensorContract.Sensor.Data.SENSOR_ID + "=?";
		mSingleArg[0] = String.valueOf(sensorId);

		mDatabase.delete(Tables.SENSOR_DATA, selectionData, mSingleArg);
		return mDatabase.delete(Tables.SENSORS, selection, mSingleArg);
	}

	/**
	 * Searches for a device with given address
	 * 
	 * @param address
	 *            the device address
	 * @return the sensor id or -1 if not found
	 */
	public long findSensor(final String address) {
		final String selection = SensorContract.Sensor.ADDRESS + "=?";
		mSingleArg[0] = address;

		final Cursor cursor = mDatabase.query(Tables.SENSORS, SENSOR_ID_PROJECTION, selection, mSingleArg, null, null, null);
		try {
			long id = -1;
			if (cursor.moveToNext()) {
				id = cursor.getLong(0 /* _ID */);
			}
			return id;
		} finally {
			cursor.close();
		}
	}

	/**
	 * Adds the new sensor data to the database
	 * 
	 * @param address
	 *            the sensor address
	 * @param name
	 *            the sensor name
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	private long addSensor(final String address, final String name) {
		final ContentValues values = new ContentValues();
		values.put(SensorContract.Sensor.ADDRESS, address);
		values.put(SensorContract.Sensor.NAME, name);

		return mDatabase.insert(Tables.SENSORS, null, values);
	}

	/**
	 * Adds the new sensor data to the database
	 * 
	 * @param sensorId
	 *            the sensor id from 'sensor' table
	 * @param timestamp
	 *            the timestamp then the readings were received
	 * @param temp
	 *            the sensor temperature
	 * @param battery
	 *            the battery level of the sensor (0-100)
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	private long addSensorData(final long sensorId, final long timestamp, final double temp, final int battery) {
		final ContentValues values = new ContentValues();
		values.put(SensorContract.Sensor.Data.SENSOR_ID, sensorId);
		values.put(SensorContract.Sensor.Data.TIMESTAMP, timestamp);
		values.put(SensorContract.Sensor.Data.TEMP, temp);
		values.put(SensorContract.Sensor.Data.BATTERY, battery);

		return mDatabase.insert(Tables.SENSOR_DATA, null, values);
	}

	/**
	 * Updates the sensor data in the database. This is used for unstored sensors. Application keeps just the most up-to-date values
	 * 
	 * @param sensorId
	 *            the sensor id from 'sensor' table
	 * @param timestamp
	 *            the timestamp then the readings were received
	 * @param temp
	 *            the sensor temperature
	 * @param battery
	 *            the battery level of the sensor (0-100)
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	private long updateSensorData(final long sensorId, final long timestamp, final double temp, final int battery) {
		final String selection = SensorContract.Sensor._ID + "=?";
		mSingleArg[0] = String.valueOf(sensorId);

		final ContentValues values = new ContentValues();
		values.put(SensorContract.Sensor.Data.SENSOR_ID, sensorId);
		values.put(SensorContract.Sensor.Data.TIMESTAMP, timestamp);
		values.put(SensorContract.Sensor.Data.TEMP, temp);
		values.put(SensorContract.Sensor.Data.BATTERY, battery);

		return mDatabase.update(Tables.SENSOR_DATA, values, selection, mSingleArg);
	}

	/**
	 * Adds the new sensor data to the database. If the sensor with given address does not exist in the database, it will be added.
	 * 
	 * @param address
	 *            the sensor address
	 * @param name
	 *            the sensor name
	 * @param timestamp
	 *            the timestamp when the data was obtained
	 * @param temp
	 *            the sensor temperature
	 * @param battery
	 *            the sensor battery level (0-100)
	 * @return the row ID of the newly inserted sensor data, or -1 if an error occurred
	 */
	public long addNewSensorData(final String address, final String name, final long timestamp, final double temp, final int battery) {
		final String selection = SensorContract.Sensor.ADDRESS + "=?";
		mSingleArg[0] = address;

		final Cursor cursor = mDatabase.query(Tables.SENSORS, SENSOR_ID_STORED_PROJECTION, selection, mSingleArg, null, null, null);
		try {
			long id = -1;
			if (!cursor.moveToNext()) {
				id = addSensor(address, name);
				return addSensorData(id, timestamp, temp, battery);
			} else {
				id = cursor.getLong(0 /* _ID */);
				final boolean stored = 1 == cursor.getInt(1 /* STORED */);
				if (stored)
					return addSensorData(id, timestamp, temp, battery);
				else
					return updateSensorData(id, timestamp, temp, battery);
			}
		} finally {
			cursor.close();
		}
	}

	/**
	 * Returns the last timestamp of data for sensor with given address
	 * 
	 * @param address
	 *            the sensor address
	 * @return the timestamp or 0 if not exists
	 */
	public long getLastTimestamp(final String address) {
		final String selection = SensorContract.Sensor.ADDRESS + "=?";
		mSingleArg[0] = address;

		final Cursor cursor = mDatabase.query(Views.SENSOR_LAST_DATA, LAST_DATA_PROJECTION, selection, mSingleArg, null, null, null);
		try {
			if (cursor.moveToNext())
				return cursor.getLong(3 /* TIMESTAMP */);
			return 0;
		} finally {
			cursor.close();
		}
	}

	private class SQLiteHelper extends SQLiteOpenHelper {
		/**
		 * The SQL code that creates the sensors table:
		 * 
		 * <pre>
		 * -------------------------------------------------------------------------------
		 * |                                  sensors                                    |
		 * -------------------------------------------------------------------------------
		 * | _id (int, pk, auto increment) | address (text) | name (text) | stored (int) |
		 * -------------------------------------------------------------------------------
		 * </pre>
		 */
		private static final String CREATE_SENSORS = "CREATE TABLE " + Tables.SENSORS + "(" + SensorContract.Sensor._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + SensorContract.Sensor.ADDRESS
				+ " TEXT NOT NULL, " + SensorContract.Sensor.NAME + " TEXT, " + SensorContract.Sensor.STORED + " INT NOT NULL DEFAULT(0));";

		/**
		 * The SQL code that creates the sensor data table:
		 * 
		 * <pre>
		 * --------------------------------------------------------------------------------------------------------------------
		 * |                                                   sensor_data                                                    |
		 * --------------------------------------------------------------------------------------------------------------------
		 * | _id (int, pk, auto increment) | sensor_id (int, fk->sensors(_id) | timestamp (int) | temp (real) | battery (int) |
		 * --------------------------------------------------------------------------------------------------------------------
		 * </pre>
		 */
		private static final String CREATE_DATA = "CREATE TABLE " + Tables.SENSOR_DATA + "(" + SensorContract.Sensor.Data._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ SensorContract.Sensor.Data.SENSOR_ID + " INTEGER NOT NULL," + SensorContract.Sensor.Data.TIMESTAMP + " INTEGER NOT NULL, " + SensorContract.Sensor.Data.TEMP + " REAL, "
				+ SensorContract.Sensor.Data.BATTERY + " INTEGER, FOREIGN KEY(" + SensorContract.Sensor.Data.SENSOR_ID + ") REFERENCES " + Tables.SENSORS + "(" + SensorContract.Sensor._ID + "));";

		/**
		 * The SQL code that creates the sensor rssi table:
		 * 
		 * <pre>
		 * -------------------------------------------------
		 * |               sensor_rssi                     |
		 * -------------------------------------------------
		 * | sensor_id (int, fk->sensors(_id) | rssi (int) |
		 * -------------------------------------------------
		 * </pre>
		 */
		private static final String CREATE_RSSI = "CREATE TABLE " + Tables.SENSOR_RSSI + "(" + SensorContract.Sensor.Rssi.SENSOR_ID + " INTEGER PRIMARY KEY NOT NULL,"
				+ SensorContract.Sensor.Rssi.RSSI + " INTEGER NOT NULL DEFAULT(0), FOREIGN KEY(" + SensorContract.Sensor.Rssi.SENSOR_ID + ") REFERENCES " + Tables.SENSORS + "("
				+ SensorContract.Sensor._ID + "));";

		/**
		 * The SQL code that creates a view for a sensor with it's most current data (the one with the greater timestamp):
		 * 
		 * <pre>
		 * -------------------------------------------------------------------------------------------------------------------------
		 * |                                                 sensor_last_data                                                      |
		 * -------------------------------------------------------------------------------------------------------------------------
		 * |                            (sensors)                                  |                   (sensor_data)               |
		 * ------------------------------------------------------------------------------------------------------------------------
		 * | _id (int, pk, auto increment) | address (text) | name (text) | stored | timestamp (int) | temp (real) | battery (int) |
		 * -------------------------------------------------------------------------------------------------------------------------
		 * </pre>
		 */
		private static final String CREATE_LAST_DATA_VIEW = "CREATE VIEW " + Views.SENSOR_LAST_DATA + " AS SELECT " + SensorContract.Sensor._ID + "," + SensorContract.Sensor.ADDRESS + ","
				+ SensorContract.Sensor.NAME + "," + SensorContract.Sensor.STORED + ",newest." + SensorContract.Sensor.Data.TIMESTAMP + " AS " + SensorContract.Sensor.Data.TIMESTAMP + ",newest."
				+ SensorContract.Sensor.Data.TEMP + " AS " + SensorContract.Sensor.Data.TEMP + ",newest." + SensorContract.Sensor.Data.BATTERY + " AS " + SensorContract.Sensor.Data.BATTERY + ",rssi."
				+ SensorContract.Sensor.Rssi.RSSI + " AS " + SensorContract.Sensor.Rssi.RSSI + " FROM " + Tables.SENSORS + " LEFT OUTER JOIN " + Tables.SENSOR_RSSI + " rssi ON " + Tables.SENSORS
				+ "." + SensorContract.Sensor._ID + "=rssi." + SensorContract.Sensor.Rssi.SENSOR_ID + " INNER JOIN (SELECT " + SensorContract.Sensor.Data.SENSOR_ID + ","
				+ SensorContract.Sensor.Data.TIMESTAMP + "," + SensorContract.Sensor.Data.TEMP + "," + SensorContract.Sensor.Data.BATTERY + " FROM " + Tables.SENSOR_DATA + " GROUP BY "
				+ SensorContract.Sensor.Data.SENSOR_ID + " ORDER BY " + SensorContract.Sensor.Data.TIMESTAMP + " DESC) newest ON " + Tables.SENSORS + "." + SensorContract.Sensor._ID + "=newest."
				+ SensorContract.Sensor.Data.SENSOR_ID + ";";

		public SQLiteHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(final SQLiteDatabase db) {
			db.execSQL(CREATE_SENSORS);
			db.execSQL(CREATE_DATA);
			db.execSQL(CREATE_RSSI);
			db.execSQL(CREATE_LAST_DATA_VIEW);
		}

		@Override
		public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
			db.execSQL("DROP VIEW IF EXISTS " + Views.SENSOR_LAST_DATA);
			db.execSQL("DROP TABLE IF EXISTS " + Tables.SENSOR_RSSI);
			db.execSQL("DROP TABLE IF EXISTS " + Tables.SENSOR_DATA);
			db.execSQL("DROP TABLE IF EXISTS " + Tables.SENSORS);
			onCreate(db);
		}

	}
}
