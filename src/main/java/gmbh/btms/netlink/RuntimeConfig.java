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

import java.nio.file.Path;

/**
 * <p>The runtime configuration contains the information from the netlink
 * configuration file and information about the runtime environment (path specifications, etc.).
 * </p>
 *
 * @author Oliver Dornauf
 * @since 1.0.0
 */
public class RuntimeConfig {

	public static final String CONFIG_FILENAME = "netlink.xml";
	public static final String NETLINK_CACHE_PATH = "/btms.gmbh/netlink/";
	public static final String NETLINK_TMP_PATH = "/netlink/";
	public static final String NETLINK_LOG_FILE = "/btms.gmbh/netlink/log/netlink.log";
	private static final RuntimeConfig INSTANCE = new RuntimeConfig();

	private Path tempFolder;
	private Path localFileCache;
	private Path localFileRoot;
	private boolean isOnline;

	private RuntimeConfig() {

	}

	public static RuntimeConfig instance() {
		return INSTANCE;
	}

	public Path getTempFolder() {
		return tempFolder;
	}

	public void setTempFolder(Path tempFolder) {
		this.tempFolder = tempFolder;
	}

	public Path getLocalFileCache() {
		return localFileCache;
	}

	public void setLocalFileCache(Path localFileCache) {
		this.localFileCache = localFileCache;
	}

	public Path getLocalFileRoot() {
		return localFileRoot;
	}

	public void setLocalFileRoot(Path localFileRoot) {
		this.localFileRoot = localFileRoot;
	}

	public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean online) {
		isOnline = online;
	}
}


