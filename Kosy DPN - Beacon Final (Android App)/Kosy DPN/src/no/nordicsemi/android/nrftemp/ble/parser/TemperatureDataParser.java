package no.nordicsemi.android.nrftemp.ble.parser;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

public class TemperatureDataParser {
	private static final String TAG = "TemperatureDataParser";

	private static final int FLAGS = 0x01; // "Flags" Data Type (See section 18.1 of Core_V4.0.pdf)
	private static final int LOCAL_NAME = 0x09; // "Complete Local Name" Data Type (See section 18.3 of Core_V4.0.pdf)
	private static final int SERVICE_DATA = 0x16; // "Service Data" Data Type (See section 18.10 of Core_V4.0.pdf)

	private static final short TEMPERATURE_SERVICE_UUID = 0x1809;
	private static final short BATTERY_SERVICE_UUID = 0x180F;
	private static final short DEVICE_INFORMATION_SERVICE_UUID = 0x180A;

	/**
	 * Parses the advertising package obtained by BLE device
	 * 
	 * @param data
	 *            the data obtained (EIR data). Read Bluetooth Core Specification v4.0 (Core_V4.0.pdf -&gt; Vol.3 -&gt; Part C -&gt; Section 8) for details
	 * @return the parsed temperature data
	 * @throws ParseException
	 *             thrown when the given data does not fit the expected format
	 */
	public static TemperatureData parse(final BluetoothDevice device, final byte[] data) {
		final TemperatureData td = new TemperatureData(device);

		/*
		 * First byte of each EIR Data Structure has it's length (1 octet).
		 * There comes the EIR Data Type (n bytes) and (length - n bytes) of data. 
		 * See Core_V4.0.pdf -> Vol.3 -> Part C -> Section 8 for details
		 */
		for (int i = 0; i < data.length;) {
			final int eirLength = data[i];

			// check whether there is no more to read
			if (eirLength == 0)
				break;

			final int eirDataType = data[++i];
			switch (eirDataType) {
			case FLAGS:
				// do nothing
				break;
			case LOCAL_NAME:
				/*
				 * Local name data structure contains length - 1 bytes of the device name (1 byte for the data type)
				 */
				final String name = decodeLocalName(data, i + 1, eirLength - 1);
				td.setName(name);
				break;
			case SERVICE_DATA:
				/*
				 * First 2 bytes of service data are the 16-bit Service UUID in reverse order. The rest is service specific data.
				 */
				final short serviceUUID = decodeServiceUUID(data, i + 1);

				switch (serviceUUID) {
				case BATTERY_SERVICE_UUID:
					final int battery = decodeBatteryLevel(data, i + 3);
					td.setBattery(battery);
					break;
				case TEMPERATURE_SERVICE_UUID:
					final double temp = decodeTempLevel(data, i + 3);
					td.setTemp(temp);
					break;
				case DEVICE_INFORMATION_SERVICE_UUID:
					final double temp2 = decodeTempLevel(data, i + 3);
					td.setTemp2(temp2);
					// do nothing 
					break;
				}
				break;
			default:
				break;
			}
			i += eirLength;
		}
		return td;
	}

	private static String decodeLocalName(final byte[] data, final int start, final int length) {
		try {
			return new String(data, start, length, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "Unable to convert the local name to UTF-8", e);
			return null;
		}
	}

	private static short decodeServiceUUID(final byte[] data, final int start) {
		return (short) ((data[start + 1] << 8) | data[start]);
	}

	private static int decodeBatteryLevel(final byte[] data, final int start) {
		/*
		 * Battery level is a 1 byte number 0-100 (0x64). Value 255 (0xFF) is used for illegal measurement values
		 */
		return data[start];
	}

	private static float decodeTempLevel(final byte[] data, final int start) {
		return bytesToFloat(data[start], data[start + 1], data[start + 2], data[start + 3]);
	}

	/**
	 * Convert signed bytes to a 32-bit short float value.
	 */
	private static float bytesToFloat(byte b0, byte b1, byte b2, byte b3) {
		//int mantissa = unsignedToSigned(unsignedByteToInt(b0) + (unsignedByteToInt(b1) << 8) + (unsignedByteToInt(b2) << 16), 24);
		int mantissa = unsignedByteToInt(b0) + (unsignedByteToInt(b1) << 8) + (unsignedByteToInt(b2) << 16) + (unsignedByteToInt(b3) << 24);
		return (float) (mantissa);// * Math.pow(10, b3));
	}

	/**
	 * Convert a signed byte to an unsigned int.
	 */
	private static int unsignedByteToInt(byte b) {
		return (int)b & 0xFF;
	}

	/**
	 * Convert an unsigned integer value to a two's-complement encoded signed value.
	 */
	private static int unsignedToSigned(int unsigned, int size) {
		if ((unsigned & (1 << size - 1)) != 0) {
			unsigned = -1 * ((1 << size - 1) - (unsigned & ((1 << size - 1) - 1)));
		}
		return unsigned;
	}
}
