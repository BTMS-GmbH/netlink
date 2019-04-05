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
 * Runtime
 *
 * @author Oliver Dornauf
 * @since 1.0.0
 */
@DatabaseTable(tableName = "RUNTIME")
public class Runtime {

	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private boolean offlineAllowed;
	@DatabaseField
	private String minJDK;
	@DatabaseField
	private String maxJDK;
	@DatabaseField
	private String mainClass;

	public boolean isOfflineAllowed() {
		return offlineAllowed;
	}

	public String getMinJDK() {
		return minJDK;
	}

	public String getMaxJDK() {
		return maxJDK;
	}

	public String getMainClass() {
		return mainClass;
	}
}
