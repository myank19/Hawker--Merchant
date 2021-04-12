package io.goolean.tech.hawker.merchant.Constant;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector {

	private static Context _context;
	public static ConnectionDetector connectionDetector = null;

	public ConnectionDetector(Context context) {
		_context = context;
	}

	public static ConnectionDetector getConnectionDetector(Context context) {

		if(connectionDetector==null)
		{
			_context=context;
			connectionDetector = new ConnectionDetector(_context);

		}
		return connectionDetector;
	}
	/**
	 * Checking for all possible internet providers
	 * **/


	public static boolean isConnectingToInternet() {

		ConnectivityManager connectivity = (ConnectivityManager) _context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							return true;
					}

		}
			return false;
	}
}