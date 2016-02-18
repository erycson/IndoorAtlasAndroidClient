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

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;

public class IndoorAtlasService extends Service implements IALocationListener {
    private IALocationManager mIALocationManager;
    private SocketClientInstance socket;

    public int onStartCommand(Intent intent, int flags, int startId) {
        mIALocationManager = IALocationManager.create(this);
        mIALocationManager.requestLocationUpdates(IALocationRequest.create(), this);

        socket = SocketClientInstance.getInstance();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {
        mIALocationManager.removeLocationUpdates(this);
        mIALocationManager.destroy();
    }

    public void onLocationChanged(IALocation location) {
        Intent intent = new Intent("location");
        intent.putExtra("data", location);
        sendBroadcast(intent);
        socket.sendLocation(location.getLatitude(), location.getLongitude(), location.getAltitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) { }
}
