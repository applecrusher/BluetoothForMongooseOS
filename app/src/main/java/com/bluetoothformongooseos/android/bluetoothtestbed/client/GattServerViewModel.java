package com.bluetoothformongooseos.android.bluetoothtestbed.client;

import android.bluetooth.BluetoothDevice;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

public class GattServerViewModel extends BaseObservable {

    private BluetoothDevice mBluetoothDevice;

    public GattServerViewModel(BluetoothDevice bluetoothDevice) {
        mBluetoothDevice = bluetoothDevice;
    }

    @Bindable
    public String getServerName() {
        if (mBluetoothDevice == null) {
            return "";
        }

        String name = mBluetoothDevice.getName();

        if(name == null){
            name = "None";
        }
        else if(name.equals("")){
            name = "None";
        }

        return name + '\n' + mBluetoothDevice.getAddress();
    }
}
