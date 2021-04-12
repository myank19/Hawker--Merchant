package io.goolean.tech.hawker.merchant.Utility;

import android.content.Context;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class CheckDataAvailable {

    Context context;

    CheckDataAvailable(Context context) throws UnknownHostException {
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            //Connected to working internet connection
        } catch (
                UnknownHostException e) {
            e.printStackTrace();
            //Internet not available
        }
    }


}
