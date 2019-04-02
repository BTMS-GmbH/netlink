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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.Observable;

/**
 * <p>>Implements a ReadableByteChannel that shows the download progress. It will:</p
 * <ul>
 * <li>transfer the total amount of data (sum of all expectedSize entries)</li>
 * <li>The amount of data has already been loaded.</li>
 * </ul>
 *
 * @author Oliver Dornauf
 * @since 1.0.0
 */
public class ObservableReadableByteChannel extends Observable implements ReadableByteChannel {

	private long expectedSize;
	private ReadableByteChannel readableByteChannel;
	private long downloadedSize;

	public ObservableReadableByteChannel(ReadableByteChannel readableByteChannel, long downloadedSize, long expectedSize) {
		this.expectedSize = expectedSize;
		this.downloadedSize = downloadedSize;
		this.readableByteChannel = readableByteChannel;
	}

	public static double predictProgress(long downloadedSize, long expectedSize) {
		return expectedSize > 0 ? (double) downloadedSize / (double) expectedSize * 100.0 : -1.0;
	}

	public int read(ByteBuffer bb) throws IOException {
		int n;
		double progress;
		if ((n = readableByteChannel.read(bb)) > 0) {
			downloadedSize += n;
			progress = expectedSize > 0 ? (double) downloadedSize / (double) expectedSize * 100.0 : -1.0;
			setChanged();
			notifyObservers(progress);
		}
		return n;
	}

	public boolean isOpen() {
		return readableByteChannel.isOpen();
	}

	public void close() throws IOException {
		readableByteChannel.close();
	}
}
