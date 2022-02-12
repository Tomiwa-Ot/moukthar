package com.ot.androidrat;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

public class Serial {

//    // Find all available drivers from attached devices.
//    UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
//    List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
//    if (availableDrivers.isEmpty()) {
//        return;
//    }
//
//    // Open a connection to the first available driver.
//    UsbSerialDriver driver = availableDrivers.get(0);
//    UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
//    if (connection == null) {
//        // add UsbManager.requestPer
//        //    UsbSerialPort port = driver.getPorts(mission(driver.getDevice(), ..) handling here
//        return;
//    }
//).get(0); // Most devices have just one port (port 0)
//    port.open(connection);
//    port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
//
//        port.write(request, WRITE_WAIT_MILLIS);
//    len = port.read(response, READ_WAIT_MILLIS);
}
