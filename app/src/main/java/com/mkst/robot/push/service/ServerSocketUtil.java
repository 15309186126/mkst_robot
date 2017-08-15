package com.mkst.robot.push.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 服务端
 */
public class ServerSocketUtil extends Service {
    public ServerSocketUtil() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
