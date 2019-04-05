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

package gmbh.btms.netlink.config;

import org.apache.maven.artifact.versioning.ComparableVersion;

import java.net.URL;
import java.nio.file.Path;

/**
 * Resource
 *
 * @author Oliver Dornauf
 * @since 1.0.0
 */

public class Resource {

	long size;
	ComparableVersion version;
	private boolean isNative;
	private String arch;
	private String os;
	private String href;
	private Path targetPath;
	private String fileName;
	private Path localPath;
	private Path localTmpPath;
	private URL remoteLocation;
	public Resource() {

	}

	public Path getTargetPath() {
		return targetPath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Path getLocalPath() {
		return localPath;
	}

	public void setLocalPath(Path localPath) {
		this.localPath = localPath;
	}

	public Path getLocalTmpPath() {
		return localTmpPath;
	}

	public void setLocalTmpPath(Path localTmpPath) {
		this.localTmpPath = localTmpPath;
	}

	public URL getRemoteLocation() {
		return remoteLocation;
	}

	public void setRemoteLocation(URL remoteLocation) {
		this.remoteLocation = remoteLocation;
	}

	public boolean isNative() {
		return isNative;
	}

	public String getArch() {
		return arch;
	}

	public String getOs() {
		return os;
	}

	public String getHref() {
		return href;
	}

	public long getSize() {
		return size;
	}

	public ComparableVersion getVersion() {
		return version;
	}

}
