package de.hss.sae.sue.chat.server;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;

/**
 * Created by robin.hartmann on 15.11.2016.
 */
public class Utils {
    private Utils() {
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static InetAddress getIPAddress(boolean preferIpV4) {
        InetAddress ipV6 = null;

        try {
            for (NetworkInterface i : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for (InetAddress a : Collections.list(i.getInetAddresses())) {
                    if (!a.isLoopbackAddress()) {
                        if (isIpV4(a) || !preferIpV4) {
                            return a;
                        } else {
                            ipV6 = a;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(Utils.class.toString(), "Error while retrieving IP Address");
            return null;
        }

        return ipV6;
    }

    public static boolean isIpV4(InetAddress address) {
        return address instanceof Inet4Address;
    }

    public static boolean isIpV6(InetAddress address) {
        return address instanceof Inet6Address;
    }

    public static String toString(InetAddress address) {
        String stringAddress = address.getHostAddress();

        if (isIpV6(address)) {
            stringAddress = stringAddress.replaceFirst("%.*", "");
        }

        return stringAddress.replaceFirst("^/", "");
    }
}
