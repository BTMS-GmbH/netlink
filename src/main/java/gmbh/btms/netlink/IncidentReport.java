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

package gmbh.btms.netlink;

import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.filechooser.FileSystemView;

/**
 * <p>Creates a report for diagnosis by Support. The necessary files are written into a ZIP file.</p>
 * <p>The end user can open this file himself and view and check the contained information.</p>
 *
 * @author Oliver Dornauf
 * @since 1.0.0
 */

public class IncidentReport {

	/**
	 * The log.
	 */
	private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(IncidentReport.class);

	private final static char[] characters = {
			'?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '`', '?', '?', '?', '?', '?', '&', '?', ' ', '>', '<', '?', '?', '?', '?', '?', '?', '-', '\'', '?', '?', '?',
			'?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?',
			'?', '\"', '?', '?', '?', '?', '?'
	};

	private final static String[] entities = {
			"&sup2;", "&Uuml;", "&Auml;", "&Ouml;", "&Euml;", "&Ccedil;", "&AElig;", "&Aring;", "&Oslash;", "&uuml;", "&auml;", "&ouml;", "&euml;", "&ccedil;", "&aring;", "&oslash;", "&grave;",
			"&agrave;", "&egrave;", "&igrave;", "&ograve;", "&ugrave;",
			"&amp;", "&szlig;", "&nbsp;", "&gt;", "&lt;", "&copy;", "&cent;", "&pound;", "&laquo;", "&raquo;", "&reg;", "&middot;", "&acute;", "&aacute;", "&uacute;", "&oacute;", "&eacute;", "&iacute;",
			"&ntilde;", "&sect;", "&egrave;", "&icirc;", "&ocirc;",
			"&acirc;", "&ucirc;", "&ecirc;", "&aelig;", "&iexcl;", "&quot;", "&ordf;", "&times;", "&deg;", "&euro;", "&brvbar;"
	};

	private Map<Character, String> mapChar2HTMLEntity;

	public boolean createReport(Path applicationPath, Path root) {

		log.info("create incident report, reqest ID:");
		Path logFile = Paths.get(root.toString(), "log");

		String diagnoseFile = null;

		try {
			diagnoseFile = SystemUtils.USER_HOME + File.separator + "netlink-diagnose.html";
			createHTLMReport(diagnoseFile);
		} catch (Throwable e) {
			log.catching(Level.ERROR, e);
			// All exceptions are ignored. In any case a diagnostic file should be created.
		}

		ArrayList<MyZipEntry> fileNameList = new ArrayList<MyZipEntry>();

		if (logFile != null) {
			fileNameList.add(new MyZipEntry(logFile, root));

			// Get all 7 generations of Log-Files
			for (int i = 1; i < 8; i++) {
				String logFileArchive = String.format("%s.%d", logFile.toString(), i);
				Path path = Paths.get(logFileArchive);
				fileNameList.add(new MyZipEntry(path, root));
			}
		}
		fileNameList = getFileList(root, applicationPath, fileNameList);
		// TODO: Is it working with MacOS?
		File userDesktop = FileSystemView.getFileSystemView().getHomeDirectory();
		zipFiles(fileNameList, userDesktop.getAbsolutePath() + "\\netlink-diagnose.zip");
		File file = new File(diagnoseFile);
		file.delete();
		return true;
	}

	public String htmlEntityEncode(String s) {
		int length = s.length();
		final StringBuffer sb = new StringBuffer(length * 2);
		char ch;

		for (int i = 0; i < length; ++i) {
			ch = s.charAt(i);
			if (((ch >= 63) && (ch <= 90)) || ((ch >= 97) && (ch <= 122)) || (ch == '<') || (ch == '>')) {
				sb.append(ch);
			} else {
				String ss = mapChar2HTMLEntity.get(Character.valueOf(ch));

				if (ss == null) {
					sb.append(ch);
				} else {
					sb.append(ss);
				}
			}
		}

		return sb.toString();
	}

	/**
	 * Get files list from the directory recursive to the sub directory.
	 */
	private ArrayList<MyZipEntry> getFileList(Path root, Path directory, ArrayList<MyZipEntry> fileList) {

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
			for (Path path : directoryStream) {
				if (Files.isDirectory(path)) {
					getFileList(root, path, fileList);
				} else {
					fileList.add(new MyZipEntry(path, root));
				}
			}
		} catch (IOException ex) {

		}
		return fileList;
	}

	private void createHTLMReport(String diagnoseFile) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(diagnoseFile));
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy. 'um' HH:mm:ss zzzz");
		Date date = new Date();

		out.write(htmlEntityEncode("<h1>KeepCurrent2 - System Report</h1><br>generated:" + simpleDateFormat.format(date)));
		out.write(htmlEntityEncode("<br><br><h2>Softwareversioninfo:</h2>"));
		out.write(htmlEntityEncode("<p>-------------------------------------------------------------------[ VERSION ]--------<br/>"));

		out.write(htmlEntityEncode("--------------------------------------------------------------------------------------</p>"));
		out.write(htmlEntityEncode("</tbody></table>"));
		out.write(htmlEntityEncode("<h2>JAVA Properties:</h2>"));
		out.write(htmlEntityEncode("<table><thead><tr><th>properties</th><th>value</th></tr></thead><tbody>"));

		Enumeration<?> e = System.getProperties().propertyNames();

		while (e.hasMoreElements()) {
			String prop = (String) e.nextElement();

			out.write(htmlEntityEncode("<tr><td>" + prop + "</td><td>" + System.getProperty(prop) + "</td></tr>"));
		}

		out.write(htmlEntityEncode("</tbody></table>"));
		out.flush();
		out.close();
	}

	private void zipFiles(ArrayList<MyZipEntry> filenames, String outFilename) {

		// Create a buffer for reading the files
		byte[] buf = new byte[4196];
		try {

			// Create the ZIP file
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename, false));

			// Compress the files
			for (MyZipEntry filename : filenames) {
				FileInputStream in = null;

				try {
					in = new FileInputStream(filename.filename.toFile());
				}
				// continue with next file if there is a problem with the current file
				catch (Throwable t) {
					continue;
				}

				String zipEntry = filename.getRelativePath().toString();

				// Add ZIP entry to output stream.
				out.putNextEntry(new ZipEntry(zipEntry));

				// Transfer bytes from the file to the ZIP file
				int len;

				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}

				// Complete the entry
				out.closeEntry();
				in.close();
			}

			// Complete the ZIP file
			out.close();
		} catch (IOException e) {
			// generation of support file failed. Nothing we can do now.
			log.catching(Level.ERROR, e);
		}
	}

	private class MyZipEntry {

		/**
		 * Field description
		 */
		private Path filename;
		private Path root;

		/**
		 * <p>Constructs ...</p>
		 */
		public MyZipEntry(Path filename, Path root) {
			this.filename = filename;
			this.root = root;
		}

		public Path getFileName() {
			return filename;
		}

		public Path getRelativePath() {
			return root.relativize(filename);
		}
	}
}
