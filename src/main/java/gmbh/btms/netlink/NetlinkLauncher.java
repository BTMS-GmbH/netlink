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

package gmbh.btms.netlink;

import gmbh.btms.netlink.swing.NetlinkGUI;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.Level;

import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.*;

/**
 * <p>Loads the initial configuration (boot configuration and) starts the update workflow.</p>
 *
 * @author Oliver Dornauf
 * @since 1.0.0
 */
public class NetlinkLauncher {

	/**
	 * The log.
	 */
	private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(NetlinkLauncher.class);
	private String logFileName = null;

	private NetlinkLauncher() {

	}

	public NetlinkLauncher(String logFileName) {
		this.logFileName = logFileName;
	}

	public static void main(String[] args) {

		try {
			System.setProperty("java.io.useSystemProxies", "true");
			System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			final String logFileName = System.getenv("LocalAppData") + RuntimeConfig.NETLINK_LOG_FILE;
			Log4j2Configuration.getInstance().setLogFileName(logFileName);
			NetlinkLauncher launcher = new NetlinkLauncher(logFileName);
			launcher.run(args);
		} catch (Throwable ex) {
			log.catching(Level.ERROR, ex);
		}
	}

	private void run(String[] args) {

		logStartupMessageVersion();
		updateBootConfiguration();
		NetlinkGUI frame = new NetlinkGUI();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
				frame.update();
			}
		});
	}

	private void logStartupMessageVersion() {
		log.info(NetlinkLogMessages._0000);
		log.info(NetlinkLogMessages._0003, SystemUtils.USER_COUNTRY);
		log.info(NetlinkLogMessages._0004, SystemUtils.USER_DIR);
		log.info(NetlinkLogMessages._0005, SystemUtils.USER_HOME);
		log.info(NetlinkLogMessages._0006, SystemUtils.USER_LANGUAGE);
		log.info(NetlinkLogMessages._0007, SystemUtils.USER_NAME);
		log.info(NetlinkLogMessages._0008, SystemUtils.USER_TIMEZONE);
		log.info(NetlinkLogMessages._0009);
		log.info(NetlinkLogMessages._000A, SystemUtils.JAVA_RUNTIME_NAME, SystemUtils.JAVA_RUNTIME_NAME);
		log.info(NetlinkLogMessages._000B, SystemUtils.AWT_TOOLKIT);
		log.info(NetlinkLogMessages._000C, System.getProperty("file.encoding"));
		log.info(NetlinkLogMessages._000D, SystemUtils.JAVA_HOME);
		log.info(NetlinkLogMessages._000E, SystemUtils.JAVA_LIBRARY_PATH);
		log.info(NetlinkLogMessages._000F, SystemUtils.JAVA_ENDORSED_DIRS);
		log.info(NetlinkLogMessages._0010, SystemUtils.JAVA_IO_TMPDIR);
		log.info(NetlinkLogMessages._0011, SystemUtils.JAVA_VERSION, SystemUtils.JAVA_CLASS_VERSION, SystemUtils.JAVA_VM_INFO);
		log.info(NetlinkLogMessages._0012);
		log.info(NetlinkLogMessages._0013, File.separator);
		log.info(NetlinkLogMessages._0014, SystemUtils.OS_NAME);
		log.info(NetlinkLogMessages._0015, SystemUtils.OS_VERSION);
		log.info(NetlinkLogMessages._0016, SystemUtils.OS_ARCH);
		log.info(NetlinkLogMessages._0017);
	}

	private void updateBootConfiguration() {

		String dataFolderAsString = System.getenv("LocalAppData");
		if (dataFolderAsString == null || dataFolderAsString.length() == 0) {
			dataFolderAsString = System.getProperty("java.io.tmpdir");
		}
		dataFolderAsString = dataFolderAsString + RuntimeConfig.NETLINK_CACHE_PATH;
		Path dataFolder = Paths.get(dataFolderAsString);
		Path tempFolder = Paths.get(System.getProperty("java.io.tmpdir") + RuntimeConfig.NETLINK_TMP_PATH);
		RuntimeConfig runtimeConfig = RuntimeConfig.instance();
		runtimeConfig.setLocalFileCache(dataFolder);
		runtimeConfig.setTempFolder(tempFolder);
	}
}
