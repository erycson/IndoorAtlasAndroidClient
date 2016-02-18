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

import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.net.Socket;

/**
 * Created by Erycson on 16/02/2016.
 */
public class SocketClientInstance {
    private static SocketClientInstance ourInstance = new SocketClientInstance();

    private SocketClient client;
    private Thread thread;
    private SocketClienteDisconectedInterface onDisconnected;

    public static SocketClientInstance getInstance() {
        return ourInstance;
    }

    private SocketClientInstance() {}

    public void connect(String host, int port) {
        client = new SocketClient();
        client.setHost(host);
        client.setPort(port);
        thread = new Thread(client);
        thread.setName("SocketClient");
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                if (onDisconnected != null)
                    onDisconnected.OnClientDisconnected();
            }
        });
        thread.start();
    }

    public void disconnect() {
        if (thread == null)
            return;

        thread.interrupt();
        thread = null;
        client = null;

        if (onDisconnected != null)
            onDisconnected.OnClientDisconnected();
    }

    public void sendLocation(double lat, double lng, double alt) {
        if (client == null)
            return;

        try {
            client.sendLocation(lat, lng, alt);
        } catch (Exception e) {
            Log.e("SocketClientInstance", "sendLocation: " + e.getMessage());
            disconnect();
        }
    }

    public void setOnDisconnected(SocketClienteDisconectedInterface onDisconnected) {
        this.onDisconnected = onDisconnected;
    }
}
