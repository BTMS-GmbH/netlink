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

/**
 * Information
 *
 * @author Oliver Dornauf
 * @since 7.0.0
 */
@DatabaseTable(tableName = "INFORMATION")
public class Information {

	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private String codebase;
	@DatabaseField
	private String guid;
	@DatabaseField
	private String homepage;
	@DatabaseField
	private String title;
	// X509Attributes
	@DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, columnName = "SIGNATURE_ID")
	private X509Attributes x509Attributes;
	@DatabaseField
	private long configurationTimestamp;
	private String vendor;
	private String bootApp;
	public Information() {

	}

	public long getConfigurationTimestamp() {
		return configurationTimestamp;
	}

	public void setConfigurationTimestamp(long configurationTimestamp) {
		this.configurationTimestamp = configurationTimestamp;
	}

	public X509Attributes getX509Attributes() {
		return x509Attributes;
	}

	public void setX509Attributes(X509Attributes x509Attributes) {
		this.x509Attributes = x509Attributes;
	}

	public String getVendor() {
		return vendor;
	}

	public String getCodebase() {
		return codebase;
	}

	public String getGuid() {
		return guid;
	}

	public String getHomepage() {
		return homepage;
	}

	public String getTitle() {
		return title;
	}

	public String getBootApp() {
		return bootApp;
	}
}
