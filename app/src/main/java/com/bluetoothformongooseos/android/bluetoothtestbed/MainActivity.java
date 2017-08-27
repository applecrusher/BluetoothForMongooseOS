package com.bluetoothformongooseos.android.bluetoothtestbed;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bluetoothformongooseos.android.bluetoothtestbed.R;
import com.bluetoothformongooseos.android.bluetoothtestbed.client.ClientActivity;
import com.bluetoothformongooseos.android.bluetoothtestbed.databinding.ActivityMainBinding;
import com.bluetoothformongooseos.android.bluetoothtestbed.server.ServerActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);


       /* binding.launchClientButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this,
                ClientActivity.class)));*/



        binding.launchClientButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this,
                        ClientActivity.class));
            }
        });
    }
}
