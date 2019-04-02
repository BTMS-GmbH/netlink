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

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

/**
 * NetlinkDefinition
 *
 * @author Oliver Dornauf
 * @since 1.0.0
 */
@DatabaseTable(tableName = "NETLINK")
public class NetlinkDefinition {

	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private boolean launchable;
	@DatabaseField
	private boolean cached;
	@DatabaseField
	private long fileTimestamp;
	@DatabaseField
	private long totalSize;
	// information
	@DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, columnName = "INFORMATION_ID")
	private Information information;
	//application>
	@DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, columnName = "APPLICATION_ID")
	private Application application;
	// runtime
	@DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, columnName = "RUNTIME_ID")
	private Runtime runtime;
	// Resources that were downloaded and validated
	@ForeignCollectionField(eager = true)
	private ForeignCollection<ValidatedResource> validatedResources;
	// Resources defined inside the netlink.xml file
	@XStreamImplicit
	private List<Resource> resources;

	public NetlinkDefinition() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isLaunchable() {
		return launchable;
	}

	public void setLaunchable(boolean launchable) {
		this.launchable = launchable;
	}

	public boolean isCached() {
		return cached;
	}

	public void setCached(boolean cached) {
		this.cached = cached;
	}

	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	public ForeignCollection<ValidatedResource> getValidatedResources() {
		return validatedResources;
	}

	public void setValidatedResources(ForeignCollection<ValidatedResource> validatedResources) {
		this.validatedResources = validatedResources;
	}

	public List<Resource> getResources() {
		return resources;
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

	public long getFileTimestamp() {
		return fileTimestamp;
	}

	public void setFileTimestamp(long fileTimestamp) {
		this.fileTimestamp = fileTimestamp;
	}

	public Information getInformation() {
		return information;
	}

	public void setInformation(Information information) {
		this.information = information;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public Runtime getRuntime() {
		return runtime;
	}

	public void setRuntime(Runtime runtime) {
		this.runtime = runtime;
	}
}
