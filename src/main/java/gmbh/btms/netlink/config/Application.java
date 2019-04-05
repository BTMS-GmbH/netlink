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

import java.nio.file.Path;

/**
 * Application
 *
 * @author Oliver Dornauf
 * @since 1.0.0
 */
@DatabaseTable(tableName = "APPLICATION")
public class Application {

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField
	private String version;
	@DatabaseField
	private String versionSchema;
	@DatabaseField
	private String netlinkCode;
	@DatabaseField
	private Path workingDirectory;

	public Application() {

	}

	public Path getWorkingDirectory() {
		return workingDirectory;
	}

	public void setWorkingDirectory(Path workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	public String getVersion() {
		return version;
	}

	public String getVersionSchema() {
		return versionSchema;
	}

	public String getNetlinkCode() {
		return netlinkCode;
	}
}
