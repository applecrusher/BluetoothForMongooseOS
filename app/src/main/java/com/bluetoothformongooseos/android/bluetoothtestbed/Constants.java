package com.bluetoothformongooseos.android.bluetoothtestbed;

import java.util.UUID;

public class Constants {

    public static String SERVICE_STRING = "5F6D4F53-5F43-4647-5F53-56435F49445F";
    public static String CHARACTERISTIC_ECHO_STRING_SAVE = "326D4F53-5F43-4647-5F73-6176655F5F32"; //save/ save and reboot
    public static String CHARACTERISTIC_ECHO_STRING_VALUE = "316D4F53-5F43-4647-5F76-616C75655F31";
    public static String CHARACTERISTIC_ECHO_STRING_KEY = "306D4F53-5F43-4647-5F6B-65795F5F5F30";



    public static UUID SERVICE_UUID = UUID.fromString(SERVICE_STRING);
    public static UUID CHARACTERISTIC_ECHO_UUID_SAVE = UUID.fromString(CHARACTERISTIC_ECHO_STRING_SAVE);
    public static UUID CHARACTERISTIC_ECHO_UUID_VALUE = UUID.fromString(CHARACTERISTIC_ECHO_STRING_VALUE);
    public static UUID CHARACTERISTIC_ECHO_UUID_KEY = UUID.fromString(CHARACTERISTIC_ECHO_STRING_KEY);

    public static final long SCAN_PERIOD = 5000;
}
