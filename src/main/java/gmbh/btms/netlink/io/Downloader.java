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

import gmbh.btms.exception.DownloadRuntimeException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.attribute.FileTime;

/**
 * Downloader
 *
 * @author Oliver Dornauf
 * @since 7.0.0
 */
public class Downloader {

	/**
	 * Gets the last modified.
	 *
	 * @return the last modified
	 */
	public static FileTime getLastModified(URL item) {
		URLConnection conn = null;
		try {
			conn = item.openConnection();
			if (conn instanceof HttpURLConnection) {
				((HttpURLConnection) conn).setRequestMethod("HEAD");
			}
			conn.getInputStream();
			return FileTime.fromMillis(conn.getLastModified());
		} catch (IOException e) {
			if (e instanceof FileNotFoundException) {
				throw new DownloadRuntimeException(item.toString());
			}
			throw new RuntimeException(e);
		} finally {
			if (conn instanceof HttpURLConnection) {
				((HttpURLConnection) conn).disconnect();
			}
		}
	}
}
