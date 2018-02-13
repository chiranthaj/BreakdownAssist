package lk.steps.breakdownassistpluss;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by Jagath on 05/20/2015.
 */
public final class TelephonyInfo {

    private static TelephonyInfo telephonyInfo;
    private String imeiSIM1;
    private String imeiSIM2;
    private boolean isSIM1Ready;
    private boolean isSIM2Ready;

    public String getImeiSIM1() {
        return imeiSIM1;
    }

/*public static void setImeiSIM1(String imeiSIM1) {
    TelephonyInfo.imeiSIM1 = imeiSIM1;
}*/

    public String getImeiSIM2() {
        return imeiSIM2;
    }

/*public static void setImeiSIM2(String imeiSIM2) {
    TelephonyInfo.imeiSIM2 = imeiSIM2;
}*/

    public boolean isSIM1Ready() {
        return isSIM1Ready;
    }

/*public static void setSIM1Ready(boolean isSIM1Ready) {
    TelephonyInfo.isSIM1Ready = isSIM1Ready;
}*/

    public boolean isSIM2Ready() {
        return isSIM2Ready;
    }

/*public static void setSIM2Ready(boolean isSIM2Ready) {
    TelephonyInfo.isSIM2Ready = isSIM2Ready;
}*/

    public boolean isDualSIM() {
        return imeiSIM2 != null;
    }

    private TelephonyInfo() {
    }

    public static TelephonyInfo getInstance(Context context) {

        if (telephonyInfo == null) {
            telephonyInfo = new TelephonyInfo();
            Log.d("TelephonyInfo","=0");
            TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                Log.d("TelephonyInfo","=1");
                telephonyInfo.imeiSIM1 = telephonyManager.getDeviceId();
                telephonyInfo.imeiSIM2 = null;

                Log.d("TelephonyInfo","1="+telephonyManager.getDeviceId());

                try {
                    telephonyInfo.imeiSIM1 = getDeviceIdBySlot(context, "getDeviceId", 0);
                    telephonyInfo.imeiSIM2 = getDeviceIdBySlot(context, "getDeviceId", 1);
                } catch (GeminiMethodNotFoundException e0) {
                    //e0.printStackTrace();
                    Log.d("TelephonyInfo","2="+e0.getMessage());
                    try {
                        telephonyInfo.imeiSIM1 = getDeviceIdBySlot(context, "getDeviceIdGemini", 0);
                        telephonyInfo.imeiSIM2 = getDeviceIdBySlot(context, "getDeviceIdGemini", 1);
                    } catch (GeminiMethodNotFoundException e1) {
                        //e1.printStackTrace();
                        Log.d("TelephonyInfo","3="+e1.getMessage());
                        try {
                            telephonyInfo.imeiSIM1 = getDeviceIdBySlot(context, "getDeviceIdDs", 0);
                            telephonyInfo.imeiSIM2 = getDeviceIdBySlot(context, "getDeviceIdDs", 1);
                        } catch (GeminiMethodNotFoundException e2) {
                            //e2.printStackTrace();
                            Log.d("TelephonyInfo","4="+e2.getMessage());
                            try {
                                telephonyInfo.imeiSIM1 = getDeviceIdBySlot(context, "getSimSerialNumberGemini", 0);
                                telephonyInfo.imeiSIM2 = getDeviceIdBySlot(context, "getSimSerialNumberGemini", 1);
                            } catch (GeminiMethodNotFoundException e3) {
                                //Call here for next manufacturer's predicted method name if you wish
                                //e3.printStackTrace();
                                Log.d("TelephonyInfo","5="+e3.getMessage());
                                try {
                                    telephonyInfo.imeiSIM1 = getDeviceIdBySlot(context, "getImei", 0);
                                    telephonyInfo.imeiSIM2 = getDeviceIdBySlot(context, "getImei", 1);
                                } catch (GeminiMethodNotFoundException e4) {
                                    //Call here for next manufacturer's predicted method name if you wish
                                    //e3.printStackTrace();
                                    Log.d("TelephonyInfo","6="+e3.getMessage());
                                }
                            }
                        }
                    }
                }

                telephonyInfo.isSIM1Ready = telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
                telephonyInfo.isSIM2Ready = false;

                try {
                    telephonyInfo.isSIM1Ready = getSIMStateBySlot(context, "getSimState", 0);
                    telephonyInfo.isSIM2Ready = getSIMStateBySlot(context, "getSimState", 1);
                } catch (GeminiMethodNotFoundException e) {
                    //e.printStackTrace();
                    //Log.d("M4",e.getMessage());
                    try {
                        telephonyInfo.isSIM1Ready = getSIMStateBySlot(context, "getSimStateGemini", 0);
                        telephonyInfo.isSIM2Ready = getSIMStateBySlot(context, "getSimStateGemini", 1);
                    } catch (GeminiMethodNotFoundException e1) {
                        //Call here for next manufacturer's predicted method name if you wish
                        //e1.printStackTrace();
                        //Log.d("M5",e1.getMessage());
                    }
                }
            }
        }

        return telephonyInfo;
    }

    private static String getDeviceIdBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {
        String imei = null;
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);

            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimID.invoke(telephony, obParameter);

            if (ob_phone != null) {
                imei = ob_phone.toString();

            }
        } catch (Exception e) {
            // e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }

        return imei;
    }

    private static boolean getSIMStateBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {

        boolean isReady = false;

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try {

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimStateGemini = telephonyClass.getMethod(predictedMethodName, parameter);

            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimStateGemini.invoke(telephony, obParameter);

            if (ob_phone != null) {
                int simState = Integer.parseInt(ob_phone.toString());
                if (simState == TelephonyManager.SIM_STATE_READY) {
                    isReady = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }

        return isReady;
    }


    private static class GeminiMethodNotFoundException extends Exception {

        private static final long serialVersionUID = -996812356902545308L;

        public GeminiMethodNotFoundException(String info) {
            super(info);
        }
    }
}
