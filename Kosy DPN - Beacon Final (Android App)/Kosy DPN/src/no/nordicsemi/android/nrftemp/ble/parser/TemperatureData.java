package no.nordicsemi.android.nrftemp.ble.parser;

import android.bluetooth.BluetoothDevice;

/**
 * Domain class contains data obtained from a single advertising package from a temperature sensor.
 */
public class TemperatureData {
	/** The device address */
	private final String address;

	/** The device name */
	private String name;

	/** The temperature value */
	private double temp;
	private double temp2; //me

	/** Battery status (number 0-100 in percent) */
	private int battery = 0xFF; // unknown value

	/**
	 * The flag whether the data are valid (holds real temperature measurement or not)
	 */
	private boolean valid;

	public TemperatureData(BluetoothDevice device) {
		address = device.getAddress();
		name = device.getName();
	}

	public String getAddress() {
		return address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getTemp() {
		return temp;
	}

	public void setTemp(double temp) {
		this.temp = temp;
		this.valid = true;
	}
	
	public double getTemp2() {
		return temp2;
	}

	public void setTemp2(double temp) {
		this.temp2 = temp;
		this.valid = true;
	}

	public int getBattery() {
		return battery;
	}

	public void setBattery(int battery) {
		this.battery = battery;
	}

	public boolean isValid() {
		return valid;
	}
}
