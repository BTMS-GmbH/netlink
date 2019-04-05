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

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import gmbh.btms.netlink.RuntimeConfig;
import org.apache.maven.artifact.versioning.ComparableVersion;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * ChachedResource
 *
 * @author Oliver Dornauf
 * @since 7.0.0
 */
@DatabaseTable(tableName = "RESOURCE")
public class ValidatedResource {

	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField(canBeNull = false, foreign = true)
	private NetlinkDefinition netlinkDefinition;
	@DatabaseField
	private boolean isNative;
	@DatabaseField
	private String arch;
	@DatabaseField
	private String os;
	@DatabaseField
	private String href;
	@DatabaseField
	private long size;
	@DatabaseField
	private String fileName;
	@DatabaseField(persisterClass = PathPersister.class)
	private Path localPath;
	@DatabaseField(persisterClass = PathPersister.class)
	private Path targetPath;
	@DatabaseField(persisterClass = URLPersister.class)
	private URL remoteLocation;
	@DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, columnName = "SIGNER_ID")
	private X509Attributes x509Attributes;
	private Path localTmpPath;
	private ComparableVersion version;

	public ValidatedResource() {

	}

	public ValidatedResource(Resource resource) {

		isNative = resource.isNative();
		arch = resource.getArch();
		os = resource.getOs();
		href = resource.getHref();
		size = resource.getSize();
		fileName = resource.getFileName();
		localPath = resource.getLocalPath();
		if (resource.getTargetPath() != null) {
			targetPath = Paths.get(RuntimeConfig.instance().getLocalFileCache().toString(), resource.getTargetPath().toString());
			targetPath.normalize();
		} else {
			targetPath = Paths.get("");
		}
		remoteLocation = resource.getRemoteLocation();
	}

	public Path getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(Path targetPath) {
		this.targetPath = targetPath;
	}

	public NetlinkDefinition getNetlinkDefinition() {
		return netlinkDefinition;
	}

	public void setNetlinkDefinition(NetlinkDefinition netlinkDefinition) {
		this.netlinkDefinition = netlinkDefinition;
	}

	public Path getLocalTmpPath() {
		return localTmpPath;
	}

	public void setLocalTmpPath(Path localTmpPath) {
		this.localTmpPath = localTmpPath;
	}

	public X509Attributes getX509Attributes() {
		return x509Attributes;
	}

	public void setX509Attributes(X509Attributes x509Attributes) {
		this.x509Attributes = x509Attributes;
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
