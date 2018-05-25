package com.bluetoothformongooseos.android.bluetoothtestbed.client;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.bluetoothformongooseos.android.bluetoothtestbed.R;
import com.bluetoothformongooseos.android.bluetoothtestbed.databinding.ActivityClientBinding;
import com.bluetoothformongooseos.android.bluetoothtestbed.databinding.ViewGattServerBinding;
import com.bluetoothformongooseos.android.bluetoothtestbed.util.BluetoothUtils;
import com.bluetoothformongooseos.android.bluetoothtestbed.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.widget.AdapterView.OnItemSelectedListener;
import java.util.Collections;


import static com.bluetoothformongooseos.android.bluetoothtestbed.Constants.SCAN_PERIOD;
import static com.bluetoothformongooseos.android.bluetoothtestbed.Constants.SERVICE_UUID;

public class ClientActivity extends AppCompatActivity implements OnItemSelectedListener{

    private static final String TAG = "ClientActivity";

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_FINE_LOCATION = 2;

    private ActivityClientBinding mBinding;

    private boolean mScanning;
    private Handler mHandler;
    private Handler mLogHandler;
    private Map<String, BluetoothDevice> mScanResults;

    private boolean mConnected;
    private boolean mEchoInitialized;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private ScanCallback mScanCallback;
    private BluetoothGatt mGatt;
    ProgressDialog dialog = null;

    // Lifecycle

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLogHandler = new Handler(Looper.getMainLooper());

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_client);
        try {
            @SuppressLint("HardwareIds")
            String deviceInfo = "Device Info"
                    + "\nName: " + mBluetoothAdapter.getName()
                    + "\nAddress: " + mBluetoothAdapter.getAddress();
            mBinding.clientDeviceInfoTextView.setText(deviceInfo);
            mBinding.startScanningButton.setOnClickListener(v -> startScan());
            mBinding.stopScanningButton.setOnClickListener(v -> stopScan());
            mBinding.sendMessageButton.setOnClickListener(v -> sendMessage());
            mBinding.disconnectButton.setOnClickListener(v -> disconnectGattServer());
            mBinding.viewClientLog.clearLogButton.setOnClickListener(v -> clearLogs());
        }
        catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "This device does not support Bluetooth", Toast.LENGTH_LONG).show();
        }
            Spinner keySpinner = (Spinner) findViewById(R.id.message_key_edit);
            Spinner saveSpinner = (Spinner) findViewById(R.id.message_edit_text);

            // Spinner click listener
            keySpinner.setOnItemSelectedListener(this);
            saveSpinner.setOnItemSelectedListener(this);

            // Spinner Drop down elements
            List<String> keyCategories = new ArrayList<String>();
            keyCategories.add("wifi.sta.ssid");
            keyCategories.add("wifi.sta.pass");
            keyCategories.add("update.timeout");
            keyCategories.add("update.commit_timeout");
            keyCategories.add("update.url");
            keyCategories.add("update.interval");
            keyCategories.add("update.ssl_ca_file");
            keyCategories.add("update.ssl_client_cert_file");
            keyCategories.add("update.ssl_server_name");
            keyCategories.add("update.enable_post");
            keyCategories.add("device.id");
            keyCategories.add("device.password");
            keyCategories.add("debug.udp_log_addr");
            keyCategories.add("debug.level");
            keyCategories.add("debug.filter");
            keyCategories.add("debug.stdout_uart");
            keyCategories.add("debug.stderr_uart");
            keyCategories.add("debug.factory_reset_gpio");
            keyCategories.add("debug.mg_mgr_hexdump_file");
            keyCategories.add("debug.mbedtls_level");
            keyCategories.add("debug.stdout_topic");
            keyCategories.add("debug.stderr_topic");
            keyCategories.add("sys.mount.path");
            keyCategories.add("sys.mount.dev_type");
            keyCategories.add("sys.mount.dev_opts");
            keyCategories.add("sys.mount.fs_type");
            keyCategories.add("sys.mount.fs_opts");
            keyCategories.add("sys.tz_spec");
            keyCategories.add("sys.wdt_timeout");
            keyCategories.add("sys.pref_ota_lib");
            keyCategories.add("sys.esp32_adc_vref");
            keyCategories.add("sys.atca.enable");
            keyCategories.add("sys.atca.i2c_addr");
            keyCategories.add("sys.atca.ecdh_slots_mask");
            keyCategories.add("conf_acl");
            keyCategories.add("i2c.enable");
            keyCategories.add("i2c.freq");
            keyCategories.add("i2c.debug");
            keyCategories.add("i2c.unit_no");
            keyCategories.add("i2c.sda_gpio");
            keyCategories.add("i2c.scl_gpio");
            keyCategories.add("mqtt.enable");
            keyCategories.add("mqtt.server");
            keyCategories.add("mqtt.client_id");
            keyCategories.add("mqtt.user");
            keyCategories.add("mqtt.pass");
            keyCategories.add("mqtt.reconnect_timeout_min");
            keyCategories.add("mqtt.reconnect_timeout_max");
            keyCategories.add("mqtt.ssl_cert");
            keyCategories.add("mqtt.ssl_key");
            keyCategories.add("mqtt.ssl_ca_cert");
            keyCategories.add("mqtt.ssl_cipher_suites");
            keyCategories.add("mqtt.ssl_psk_identity");
            keyCategories.add("mqtt.ssl_psk_key");
            keyCategories.add("mqtt.clean_session");
            keyCategories.add("mqtt.keep_alive");
            keyCategories.add("mqtt.will_topic");
            keyCategories.add("mqtt.will_message");
            keyCategories.add("mqtt.max_qos");
            keyCategories.add("mqtt.recv_mbuf_limit");
            keyCategories.add("shadow.lib");
            keyCategories.add("aws.thing_name");
            keyCategories.add("aws.greengrass.enable");
            keyCategories.add("aws.greengrass.reconnect_timeout_min");
            keyCategories.add("aws.greengrass.reconnect_timeout_max");
            keyCategories.add("bt.enable");
            keyCategories.add("bt.dev_name");
            keyCategories.add("bt.adv_enable");
            keyCategories.add("bt.scan_rsp_data_hex");
            keyCategories.add("bt.keep_enabled");
            keyCategories.add("bt.allow_pairing");
            keyCategories.add("bt.max_paired_devices");
            keyCategories.add("bt.random_address");
            keyCategories.add("bt.gatts.min_sec_level");
            keyCategories.add("bt.gatts.require_pairing");
            keyCategories.add("bt.config_svc_enable");
            keyCategories.add("bt.debug_svc_enable");
            keyCategories.add("eth.enable");
            keyCategories.add("eth.phy_addr");
            keyCategories.add("eth.ip");
            keyCategories.add("eth.netmask");
            keyCategories.add("eth.gw");
            keyCategories.add("eth.mdc_gpio");
            keyCategories.add("eth.mdio_gpio");
            keyCategories.add("wifi.sta.enable");
            keyCategories.add("wifi.sta.ssid");
            keyCategories.add("wifi.sta.pass");
            keyCategories.add("wifi.sta.user");
            keyCategories.add("wifi.sta.anon_identity");
            keyCategories.add("wifi.sta.cert");
            keyCategories.add("wifi.sta.key");
            keyCategories.add("wifi.sta.ca_cert");
            keyCategories.add("wifi.sta.ip");
            keyCategories.add("wifi.sta.netmask");
            keyCategories.add("wifi.sta.gw");
            keyCategories.add("wifi.sta.nameserver");
            keyCategories.add("wifi.sta.dhcp_hostname");
            keyCategories.add("wifi.ap.enable");
            keyCategories.add("wifi.ap.ssid");
            keyCategories.add("wifi.ap.pass");
            keyCategories.add("wifi.ap.hidden");
            keyCategories.add("wifi.ap.channel");
            keyCategories.add("wifi.ap.max_connections");
            keyCategories.add("wifi.ap.ip");
            keyCategories.add("wifi.ap.netmask");
            keyCategories.add("wifi.ap.gw");
            keyCategories.add("wifi.ap.dhcp_start");
            keyCategories.add("wifi.ap.dhcp_end");
            keyCategories.add("wifi.ap.trigger_on_gpio");
            keyCategories.add("wifi.ap.disable_after");
            keyCategories.add("wifi.ap.hostname");
            keyCategories.add("wifi.ap.keep_enabled");
            keyCategories.add("http.enable");
            keyCategories.add("http.listen_addr");
            keyCategories.add("http.document_root");
            keyCategories.add("http.ssl_cert");
            keyCategories.add("http.ssl_key");
            keyCategories.add("http.ssl_ca_cert");
            keyCategories.add("http.upload_acl");
            keyCategories.add("http.hidden_files");
            keyCategories.add("http.auth_domain");
            keyCategories.add("http.auth_file");
            keyCategories.add("rpc.enable");
            keyCategories.add("rpc.max_frame_size");
            keyCategories.add("rpc.max_queue_length");
            keyCategories.add("rpc.default_out_channel_idle_close_timeout");
            keyCategories.add("rpc.acl_file");
            keyCategories.add("rpc.auth_domain");
            keyCategories.add("rpc.auth_file");
            keyCategories.add("rpc.ws.enable");
            keyCategories.add("rpc.ws.server_address");
            keyCategories.add("rpc.ws.reconnect_interval_min");
            keyCategories.add("rpc.ws.reconnect_interval_max");
            keyCategories.add("rpc.ws.ssl_server_name");
            keyCategories.add("rpc.ws.ssl_ca_file");
            keyCategories.add("rpc.ws.ssl_client_cert_file");
            keyCategories.add("rpc.gatts.enable");
            keyCategories.add("rpc.mqtt.enable");
            keyCategories.add("rpc.mqtt.topic");
            keyCategories.add("rpc.uart.uart_no");
            keyCategories.add("rpc.uart.baud_rate");
            keyCategories.add("rpc.uart.fc_type");
            keyCategories.add("rpc.uart.wait_for_start_frame");
            keyCategories.add("dash.enable");
            keyCategories.add("dash.token");
            keyCategories.add("dash.server");
            keyCategories.add("dash.ca_file");
            keyCategories.add("dash.send_logs");
            keyCategories.add("dash.stats_interval");
            keyCategories.add("dash.ota_chunk_size");
            keyCategories.add("dns_sd.enable");
            keyCategories.add("dns_sd.host_name");
            keyCategories.add("dns_sd.txt");
            keyCategories.add("dns_sd.ttl");
            keyCategories.add("gcp.enable");
            keyCategories.add("gcp.project");
            keyCategories.add("gcp.region");
            keyCategories.add("gcp.registry");
            keyCategories.add("gcp.device");
            keyCategories.add("gcp.key");
            keyCategories.add("gcp.token_ttl");
            keyCategories.add("pppos.enable");
            keyCategories.add("pppos.uart_no");
            keyCategories.add("pppos.baud_rate");
            keyCategories.add("pppos.fc_enable");
            keyCategories.add("pppos.apn");
            keyCategories.add("pppos.user");
            keyCategories.add("pppos.pass");
            keyCategories.add("pppos.connect_cmd");
            keyCategories.add("pppos.echo_interval");
            keyCategories.add("pppos.echo_fails");
            keyCategories.add("pppos.hexdump_enable");
            keyCategories.add("sntp.enable");
            keyCategories.add("sntp.server");
            keyCategories.add("sntp.retry_min");
            keyCategories.add("sntp.retry_max");
            keyCategories.add("sntp.update_interval");
            keyCategories.add("spi.enable");
            keyCategories.add("spi.debug");
            keyCategories.add("spi.unit_no");
            keyCategories.add("spi.miso_gpio");
            keyCategories.add("spi.mosi_gpio");
            keyCategories.add("spi.sclk_gpio");
            keyCategories.add("spi.cs0_gpio");
            keyCategories.add("spi.cs1_gpio");
            keyCategories.add("spi.cs2_gpio");
            keyCategories.add("pins.led");
            keyCategories.add("pins.button");
            keyCategories.add("mjs.generate_jsc");
            Collections.sort(keyCategories);



            List<String> saveCategories = new ArrayList<String>();
            saveCategories.add("Save");
            saveCategories.add("Save and Restart");

            // Creating adapter for spinner
            ArrayAdapter<String> keyDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, keyCategories);
            ArrayAdapter<String> saveDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, saveCategories);

            // Drop down layout style - list view with radio button
            keyDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            saveDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            keySpinner.setAdapter(keyDataAdapter);
            saveSpinner.setAdapter(saveDataAdapter);


    }

    public void customKeySelected(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        View keyValueText = findViewById(R.id.custom_key_text);
        View messageKeyEditView = findViewById(R.id.message_key_edit);
        //now you want to show the custom view
        if(checkBox.isChecked()){
            keyValueText.setVisibility(View.VISIBLE);
            messageKeyEditView.setVisibility(View.GONE);
        }
        else{
            keyValueText.setVisibility(View.GONE);
            messageKeyEditView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check low energy support
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            // Get a newer device
            logError("No LE Support.");
            finish();
        }
    }

    // Scanning

    private void startScan() {
        if (!hasPermissions() || mScanning) {
            return;
        }

        dialog = ProgressDialog.show(this, "",
                "Scanning. Please wait...", true);

        disconnectGattServer();

        mBinding.serverListContainer.removeAllViews();

        mScanResults = new HashMap<>();
        mScanCallback = new BtleScanCallback(mScanResults);

        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        // Note: Filtering does not work the same (or at all) on most devices. It also is unable to
        // search for a mask or anything less than a full UUID.
        // Unless the full UUID of the server is known, manual filtering may be necessary.
        // For example, when looking for a brand of device that contains a char sequence in the UUID
        ScanFilter scanFilter = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(SERVICE_UUID))
                .build();
        List<ScanFilter> filters = new ArrayList<>();
       // filters.add(scanFilter);

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build();

        mBluetoothLeScanner.startScan(filters, settings, mScanCallback);

        mHandler = new Handler();
        mHandler.postDelayed(this::stopScan, SCAN_PERIOD);

        mScanning = true;
        log("Started scanning.");
    }

    private void stopScan() {
        if (mScanning && mBluetoothAdapter != null && mBluetoothAdapter.isEnabled() && mBluetoothLeScanner != null) {
            mBluetoothLeScanner.stopScan(mScanCallback);
            scanComplete();
        }

        mScanCallback = null;
        mScanning = false;
        mHandler = null;
        log("Stopped scanning.");
    }

    private void scanComplete() {
        if (mScanResults.isEmpty()) {
            log("Scan size is 0");
            return;
        }
        else{
            log("Scan size is " + mScanResults.size());
        }

        for (String deviceAddress : mScanResults.keySet()) {
            BluetoothDevice device = mScanResults.get(deviceAddress);
            GattServerViewModel viewModel = new GattServerViewModel(device);

            ViewGattServerBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this),
                    R.layout.view_gatt_server,
                    mBinding.serverListContainer,
                    true);
            binding.setViewModel(viewModel);
            binding.connectGattServerButton.setOnClickListener(v -> connectDevice(device));
        }

        if(dialog != null){
            dialog.dismiss();
        }
    }

    private boolean hasPermissions() {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            requestBluetoothEnable();
            return false;
        } else if (!hasLocationPermissions()) {
            requestLocationPermission();
            return false;
        }
        return true;
    }

    private void requestBluetoothEnable() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        log("Requested user enables Bluetooth. Try starting the scan again.");
    }

    private boolean hasLocationPermissions() {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
        log("Requested user enable Location. Try starting the scan again.");
    }

    // Gatt connection

    private void connectDevice(BluetoothDevice device) {
        log("Connecting to " + device.getAddress());
        GattClientCallback gattClientCallback = new GattClientCallback();
        mGatt = device.connectGatt(this, false, gattClientCallback);
    }

    // Messaging

    private void sendMessage() {
        if (!mConnected || !mEchoInitialized) {
            return;
        }

        //I think here is the best place to loop

        for(int i =0 ; i < 3 ; i++) {

            BluetoothGattCharacteristic characteristic = BluetoothUtils.findEchoCharacteristic(mGatt , i);
            if (characteristic == null) {
                logError("Unable to find echo characteristic.");
                disconnectGattServer();
                return;
            }
            String message = "";
            if(i == 0){
                CheckBox customKeyCheckBox = (CheckBox) findViewById(R.id.custom_key_checkbox);
                if(customKeyCheckBox.isChecked()){
                    message = ((EditText) findViewById(R.id.custom_key_text)).getText().toString();
                    log("Custom message is " + message);
                }
                else {
                    message = mBinding.messageKeyEdit.getSelectedItem().toString();
                }
            }
            else if(i ==1){
                message = mBinding.messageValueEdit.getText().toString();
            }

            else{
                message = mBinding.messageEditText.getSelectedItem().toString();
                if(message.equals("Save")){
                    message = "0";
                }
                else if(message.equals("Save and Restart")){
                    message = "2";
                }

                else{
                    message = "2";
                }
            }
            log("Sending message: " + message);

            byte[] messageBytes = StringUtils.bytesFromString(message);
            if (messageBytes.length == 0) {
                logError("Unable to convert message to bytes");
                return;
            }

            characteristic.setValue(messageBytes);
            boolean success = mGatt.writeCharacteristic(characteristic);
            if (success) {
                log("Wrote: " + StringUtils.byteArrayInHexFormat(messageBytes));
            } else {
                log("Failed to write data");
            }
            try{
                //you need to wait to write each characteristic, I am not sure exactly what a good wait time is, but it causes issues without it
                Thread.sleep(250);
            }
            catch(InterruptedException e){

            }

        }
    }

    // Logging

    private void clearLogs() {
        mLogHandler.post(() -> mBinding.viewClientLog.logTextView.setText(""));
    }

    public void log(String msg) {
        Log.d(TAG, msg);
        mLogHandler.post(() -> {
            mBinding.viewClientLog.logTextView.append(msg + "\n");
            mBinding.viewClientLog.logScrollView.post(() -> mBinding.viewClientLog.logScrollView.fullScroll(View.FOCUS_DOWN));
        });
    }

    public void logError(String msg) {
        log("Error: " + msg);
    }

    // Gat Client Actions

    public void setConnected(boolean connected) {
        mConnected = connected;
    }

    public void initializeEcho() {
        mEchoInitialized = true;
    }

    public void disconnectGattServer() {
        log("Closing Gatt connection");
        clearLogs();
        mConnected = false;
        mEchoInitialized = false;
        if (mGatt != null) {
            mGatt.disconnect();
            mGatt.close();
        }
    }

    // Callbacks

    private class BtleScanCallback extends ScanCallback {

        private Map<String, BluetoothDevice> mScanResults;

        BtleScanCallback(Map<String, BluetoothDevice> scanResults) {
            mScanResults = scanResults;
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            addScanResult(result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult result : results) {
                addScanResult(result);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            logError("BLE Scan Failed with code " + errorCode);
        }

        private void addScanResult(ScanResult result) {
            BluetoothDevice device = result.getDevice();
            String deviceAddress = device.getAddress();
            mScanResults.put(deviceAddress, device);
        }
    }

    private class GattClientCallback extends BluetoothGattCallback {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            log("onConnectionStateChange newState: " + newState);

            if (status == BluetoothGatt.GATT_FAILURE) {
                logError("Connection Gatt failure status " + status);
                disconnectGattServer();
                return;
            } else if (status != BluetoothGatt.GATT_SUCCESS) {
                // handle anything not SUCCESS as failure
                logError("Connection not GATT sucess status " + status);
                disconnectGattServer();
                return;
            }

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                log("Connected to device " + gatt.getDevice().getAddress());
                setConnected(true);
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                log("Disconnected from device");
                disconnectGattServer();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            if (status != BluetoothGatt.GATT_SUCCESS) {
                log("Device service discovery unsuccessful, status " + status);
                return;
            }

            List<BluetoothGattCharacteristic> matchingCharacteristics = BluetoothUtils.findCharacteristics(gatt);
            if (matchingCharacteristics.isEmpty()) {
                logError("Unable to find characteristics.");
                return;
            }

            log("Initializing: setting write type and enabling notification");
            for (BluetoothGattCharacteristic characteristic : matchingCharacteristics) {
                characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                enableCharacteristicNotification(gatt, characteristic);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                log("Characteristic written successfully");
            } else {
                logError("Characteristic write unsuccessful, status: " + status);
                disconnectGattServer();
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                log("Characteristic read successfully");
                readCharacteristic(characteristic);
            } else {
                logError("Characteristic read unsuccessful, status: " + status);
                // Trying to read from the Time Characteristic? It doesnt have the property or permissions
                // set to allow this. Normally this would be an error and you would want to:
                // disconnectGattServer();
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            log("Characteristic changed, " + characteristic.getUuid().toString());
            readCharacteristic(characteristic);
        }

        private void enableCharacteristicNotification(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            boolean characteristicWriteSuccess = gatt.setCharacteristicNotification(characteristic, true);
            if (characteristicWriteSuccess) {
                log("Characteristic notification set successfully for " + characteristic.getUuid().toString());
                if (BluetoothUtils.isEchoCharacteristic(characteristic)) {
                    initializeEcho();
                }
            } else {
                logError("Characteristic notification set failure for " + characteristic.getUuid().toString());
            }
        }

        private void readCharacteristic(BluetoothGattCharacteristic characteristic) {
            byte[] messageBytes = characteristic.getValue();
            log("Read: " + StringUtils.byteArrayInHexFormat(messageBytes));
            String message = StringUtils.stringFromBytes(messageBytes);
            if (message == null) {
                logError("Unable to convert bytes to string");
                return;
            }

            log("Received message: " + message);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}
