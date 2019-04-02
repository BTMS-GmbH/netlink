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

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

/**
 * DownloadItem
 *
 * @author Oliver Dornauf
 * @since 1.0.0
 */
public class DownloadItem {

	private String fileName;
	private boolean cached;
	private Path localTempFile;
	private String localFileName;
	private String localFilePath;
	private String relativeFilePath;
	private Path localFile;
	private long fileSize;
	private FileTime fileTime;
	private URL remotePath;
	private boolean online;

	private DownloadItem(Builder builder) {
		fileName = builder.fileName;
		cached = builder.cached;
		localTempFile = builder.localTempFile;
		localFileName = builder.localFileName;
		localFilePath = builder.localFilePath;
		relativeFilePath = builder.relativeFilePath;
		localFile = builder.localFile;
		fileSize = builder.fileSize;
		fileTime = builder.fileTime;
		remotePath = builder.remotePath;
		online = builder.online;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static Builder newBuilder(DownloadItem copy) {
		Builder builder = new Builder();
		builder.fileName = copy.getFileName();
		builder.cached = copy.isCached();
		builder.localTempFile = copy.getLocalTempFile();
		builder.localFileName = copy.getLocalFileName();
		builder.localFilePath = copy.getLocalFilePath();
		builder.relativeFilePath = copy.getRelativeFilePath();
		builder.localFile = copy.getLocalFile();
		builder.fileSize = copy.getFileSize();
		builder.fileTime = copy.getFileTime();
		builder.remotePath = copy.getRemotePath();
		builder.online = copy.isOnline();
		return builder;
	}

	public String getFileName() {
		return fileName;
	}

	public boolean isCached() {
		return cached;
	}

	public Path getLocalTempFile() {
		return localTempFile;
	}

	public String getLocalFileName() {
		return localFileName;
	}

	public String getLocalFilePath() {
		return localFilePath;
	}

	public String getRelativeFilePath() {
		return relativeFilePath;
	}

	public Path getLocalFile() {
		return localFile;
	}

	public long getFileSize() {
		return fileSize;
	}

	public FileTime getFileTime() {
		return fileTime;
	}

	public URL getRemotePath() {
		return remotePath;
	}

	public boolean isOnline() {
		return online;
	}

	public static final class Builder {

		private String fileName;
		private boolean cached;
		private Path localTempFile;
		private String localFileName;
		private String relativeFilePath;
		private Path localFile;
		private long fileSize;
		private FileTime fileTime;
		private String localFilePath;
		private URL remotePath;
		private boolean online;

		private Builder() {
		}

		public Builder withFileName(String val) {
			fileName = val;
			return this;
		}

		public Builder withCached(boolean val) {
			cached = val;
			return this;
		}

		public Builder withLocalTempFile(Path val) {
			localTempFile = val;
			return this;
		}

		public Builder withLocalFileName(String val) {
			localFileName = val;
			return this;
		}

		public Builder withRelativeFilePath(String val) {
			relativeFilePath = val;
			return this;
		}

		public Builder withLocalFile(Path val) {
			localFile = val;
			return this;
		}

		public Builder withFileSize(long val) {
			fileSize = val;
			return this;
		}

		public Builder withFileTime(FileTime val) {
			fileTime = val;
			return this;
		}

		public Builder withLocalFilePath(String val) {
			localFilePath = val;
			return this;
		}

		public Builder withRemotePath(URL val) {
			remotePath = val;
			return this;
		}

		public Builder withOnline(boolean val) {
			online = val;
			return this;
		}

		public DownloadItem build() {
			return new DownloadItem(this);
		}
	}
}
