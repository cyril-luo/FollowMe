package org.ris3.zc.hello;

import android.util.Log;

/**
 * Created by cyril on 17-2-15.
 */

public class TEEService {

    String TAG="TEEService";

    public native int SecAttestInit();
    public native int SecAttestFinal();
    public native int SecAttestRegisterView(int x1, int y1, int x2, int y2);
    public native int SecAttestUnregisterView(int x1, int y1, int x2, int y2);


    private static TEEService instance;

    private static TEEService getInstance() {
        if(instance == null)
            instance = new TEEService();
        return instance;
    }
    public static int SecAttest_Init() {
        return getInstance().SecAttestInit();
    }

    public static int SecAttest_Final() {
        return getInstance().SecAttestFinal();
    }

    public static int SecAttest_Register_View(int x1, int y1, int x2, int y2) {
        return getInstance().SecAttestRegisterView(x1,y1, x2, y2);
    }

    public static int SecAttest_Unregister_View(int x1, int y1, int x2, int y2) {
        return getInstance().SecAttestUnregisterView(x1, y1, x2, y2);
    }


    static{
        Log.d("TEEService","TeeService.java: static");
        try {
            System.loadLibrary("TEEattest");
        } catch (Throwable e) {
            Log.e("TEEService","ERROR: load *.so file failed!");
        }
    }
}
