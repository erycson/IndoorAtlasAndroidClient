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

import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketClient implements Runnable {
    private Socket socket;
    private OutputStream stream;
    private String host;
    private int port;

    @Override
    public void run() {
        Thread t = Thread.currentThread();

        try {
            socket = new Socket(host, port);
            stream = socket.getOutputStream();
        } catch (Exception ex) {
            Log.e("SocketClient", ex.getMessage());
            t.getUncaughtExceptionHandler().uncaughtException(t, ex);
        }
    }

    public void sendLocation(double lat, double lng, double alt) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream w = new DataOutputStream(out);
        w.writeDouble(lat);
        w.writeDouble(lng);
        w.writeDouble(alt);
        w.close();

        stream.write(out.toByteArray());
    }

    public void disconnect() throws Exception {
        socket.close();
        throw new Exception("SocketClient disconnected");
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
