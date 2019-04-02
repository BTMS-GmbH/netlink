/*
 *  Copyright 2019 [https://btms.gmbh]
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package gmbh.btms.netlink.io;

//~--- JDK imports ------------------------------------------------------------

import gmbh.btms.netlink.NetlinkLogMessages;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.*;
import java.util.Enumeration;

/**
 * <p>Class description</p>
 *
 * @author Oliver Dornauf
 * @since 1.0.0
 */

public class ConnectionStatus {

	/**
	 * Log4j2 instance
	 */
	private static final Logger log = LogManager.getLogger(ConnectionStatus.class);

	/**
	 * If a String is available (such as might come in via
	 * an HttpRequest parameter),
	 * convert it to a URL before testing.
	 */
	public static boolean isURLAvailable(String address) {
		try {
			URL u = new URL(address);

			return isURLAvailable(u);
		} catch (MalformedURLException m) {
			log.info(NetlinkLogMessages._0021, address);
			return false;
		}
	}

	/**
	 * Given a URL, get the host portion.  Then see if the
	 * InetAddress associated with that host is the same as
	 * the InetAddress associated with the localhost.
	 * <p>
	 * If they are the same, then  the address is local, so
	 * make sure the local interface is available.
	 * <p>
	 * Otherwise the address is external, so make sure
	 * external interfaces are available.  An
	 * UnknownHostException will be thrown when interfaces
	 * are down, so take that as meaning false.
	 */
	public static boolean isURLAvailable(URL url) {
		String host = url.getHost();

		try {
			InetAddress i = InetAddress.getByName(host);
			if (i.equals(InetAddress.getLocalHost())) {
				return isLocalAvailable();
			} else {
				return isAnyExternalAvailable();
			}
		} catch (UnknownHostException ue) {
			return false;
		}
	}

	/**
	 * Check to see if there is a local network interface
	 * available.
	 */
	public static boolean isLocalAvailable() {
		Enumeration e, addr;
		NetworkInterface ni;

		try {
			for (e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements(); ) {

				// If we reach this spot, then at least one
				// network interface exists
				ni = (NetworkInterface) e.nextElement();
				for (addr = ni.getInetAddresses(); addr.hasMoreElements(); ) {

					// If we reach this spot, then at least
					// one InetAddress is associated with this
					// network interface.  Check to see if it
					// is the local address.
					InetAddress i = (InetAddress) addr.nextElement();

					// For this method we want the local
					// loopback address.
					if (i.isLoopbackAddress()) {
						return true;
					}
				}
			}

			// Found no local network interfaces, or none
			// that were active.
			return false;
		} catch (SocketException se) {
			log.info(NetlinkLogMessages._0022, se.getLocalizedMessage());
			return false;
		}
	}

	/**
	 * Check to see if any network interfaces are
	 * available, active and not local.
	 */
	public static boolean isAnyExternalAvailable() {
		Enumeration e, addr;
		NetworkInterface ni;

		try {
			for (e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements(); ) {

				// If we reach this spot, then at least one
				// network interface exists
				ni = (NetworkInterface) e.nextElement();
				for (addr = ni.getInetAddresses(); addr.hasMoreElements(); ) {

					// If we reach this spot, then at least
					// one InetAddress is associated with this
					// network interface.  Check to see if it
					// is the local address.
					InetAddress i = (InetAddress) addr.nextElement();

					// If the address isn't the local loopback
					// then we have at least one valid
					// possibility for network connectivity,
					// so return true
					if (!i.isLoopbackAddress()) {
						return true;
					}
				}
			}

			// Found no network interfaces that were active
			// that were not the local loopback device.
			return false;
		} catch (SocketException se) {
			log.info(NetlinkLogMessages._0023, se.getLocalizedMessage());
			return false;
		}
	}
}
