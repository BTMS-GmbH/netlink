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

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.support.DatabaseResults;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;

/**
 * PathPersister
 *
 * @author Oliver Dornauf
 * @since 7.0.0
 */
public class URLPersister extends BaseDataType {

	private static final URLPersister singleTon = new URLPersister();
	public static int DEFAULT_WIDTH = 255;

	private URLPersister() {
		super(SqlType.STRING, new Class<?>[]{URL.class});
	}

	protected URLPersister(SqlType sqlType) {
		super(sqlType);
	}

	/**
	 * Here for others to subclass.
	 */
	protected URLPersister(SqlType sqlType, Class<?>[] classes) {
		super(sqlType, classes);
	}

	public static URLPersister getSingleton() {
		return singleTon;
	}

	@Override
	public Object parseDefaultString(FieldType fieldType, String defaultStr) {
		return defaultStr;
	}

	@Override
	public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
		return results.getString(columnPos);
	}

	@Override
	public Object javaToSqlArg(FieldType fieldType, Object obj) {
		URL url = (URL) obj;
		return url.toString();
	}

	@Override
	public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
		String uuidStr = (String) sqlArg;
		try {
			return new URL(uuidStr);
		} catch (MalformedURLException e) {
			throw SqlExceptionUtil
					      .create("Problems with column " + columnPos + " parsing UUID-string '" + uuidStr + "'", e);
		}
	}

	@Override
	public boolean isValidGeneratedType() {
		return true;
	}

	@Override
	public boolean isSelfGeneratedId() {
		return true;
	}

	@Override
	public Object generateId() {
		return null;
	}

	@Override
	public int getDefaultWidth() {
		return DEFAULT_WIDTH;
	}
}