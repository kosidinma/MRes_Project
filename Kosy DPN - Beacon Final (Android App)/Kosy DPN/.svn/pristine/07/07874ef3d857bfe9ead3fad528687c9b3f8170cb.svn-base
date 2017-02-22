package no.nordicsemi.android.nrftemp.ble;

public interface TemperatureManagerCallback {

	/**
	 * The callback is fired each time the valid temperature data was obtained from remote BLE sensor
	 */
	public void onDevicesScaned();

	/**
	 * Fired when an signal strength update from a device was received. The percentage RSSI value is calculated as follows:<br />
	 * param: RSSI - value obtained from the sensor (-127 dBm to 20 dBm)<br />
	 * return: 100% * (127+RSSI) / 127+20)
	 * 
	 * @param sensorId
	 *            the sensor id
	 * @param rssi
	 *            the current RSSI value (0-100%)
	 */
	public void onRssiUpdate(final long sensorId, final int rssi);
}
