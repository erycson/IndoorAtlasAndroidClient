/*
 * IndoorAtlas Android Client
 * Copyright (C) 2016  Érycson Nóbrega <egdn2004@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.neweyes.indoor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;
import com.indooratlas.android.sdk.resources.IALocationListenerSupport;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Intent intentService;
    private boolean serviceStarted;
    private boolean fistData = true;
    private SocketClientInstance socket;

    private EditText txtServerHost;
    private EditText txtServerPort;
    private TextView lblAltitude;
    private TextView lblLatitude;
    private TextView lblLongitude;
    private Button btnService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intentService = new Intent(MainActivity.this, IndoorAtlasService.class);
        serviceStarted = false;

        txtServerHost = (EditText) findViewById(R.id.txtServerHost);
        txtServerPort = (EditText) findViewById(R.id.txtServerPort);
        lblAltitude = (TextView) findViewById(R.id.lblAltitude);
        lblLatitude = (TextView) findViewById(R.id.lblLatitude);
        lblLongitude = (TextView) findViewById(R.id.lblLongitude);
        btnService = (Button) findViewById(R.id.btnService);
        btnService.setOnClickListener(this);

        socket = SocketClientInstance.getInstance();
        socket.setOnDisconnected(new SocketClienteDisconectedInterface() {
            @Override
            public void OnClientDisconnected() {
                Log.d("OnClientDisconnected", "Client thread disconnected");
                stopClientService();
            }
        });

        registerReceiver(onLocationChanged, new IntentFilter("location"));
    }

    private BroadcastReceiver onLocationChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            IALocation location = (IALocation)intent.getExtras().get("data");
            lblLatitude.setText("Latitude: " + location.getLatitude());
            lblLongitude.setText("Longitude: " + location.getLongitude());
            lblAltitude.setText("Altitude: " + location.getAltitude());

            if (fistData) {
                fistData = false;
                Toast.makeText(MainActivity.this, "IndoorAtlas Started", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (serviceStarted) {
            stopClientService();
        } else {
            startClientService();
        }
    }

    public void stopClientService() {
        if (!serviceStarted)
            return;

        serviceStarted = false;
        socket.disconnect();
        stopService(intentService);

        btnService.setText("Start Service");
        lblLatitude.setText("Latitude: 0.0");
        lblLongitude.setText("Longitude: 0.0");
        lblAltitude.setText("Altitude: 0.0");

        Toast.makeText(MainActivity.this, "Service Stopped", Toast.LENGTH_LONG).show();
    }

    public void startClientService() {
        fistData = true;
        String host = txtServerHost.getText().toString();
        int port = Integer.parseInt(txtServerPort.getText().toString());

        socket.connect(host, port);
        startService(intentService);
        serviceStarted = true;
        btnService.setText("Stop Service");

        Toast.makeText(MainActivity.this, "Service Started", Toast.LENGTH_LONG).show();
    }
}
