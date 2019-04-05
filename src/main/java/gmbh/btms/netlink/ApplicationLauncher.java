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

import gmbh.btms.netlink.config.NetlinkDefinition;
import gmbh.btms.netlink.config.ValidatedResource;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * ApplicationLauncher
 *
 * @author Oliver Dornauf
 * @since 1.0.0
 */
public class ApplicationLauncher {

	/**
	 * The log.
	 */
	private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(ApplicationLauncher.class);


	public void startApplication(NetlinkDefinition netlinkDefinition) {

		String commandline = getJavaLocation();

		StringBuilder classpath = new StringBuilder("\"");
		for (ValidatedResource validatedResource : netlinkDefinition.getValidatedResources()) {
			classpath.append(validatedResource.getLocalPath());
			classpath.append(File.pathSeparator);
		}
		classpath.append("\"");

		String mainClass = netlinkDefinition.getRuntime().getMainClass();

		ProcessBuilder pb = new ProcessBuilder(commandline, "-classpath", classpath.toString(), mainClass);
		Path workingDirectory = RuntimeConfig.instance().getLocalFileCache();
		if (netlinkDefinition.getApplication().getWorkingDirectory() != null) {
			workingDirectory = Paths.get(workingDirectory.toString(), netlinkDefinition.getApplication().getWorkingDirectory().toString());
		}
		pb.directory(RuntimeConfig.instance().getLocalFileCache().toFile());
		Process p = null;

		try {
			p = pb.start();
		} catch (IOException e) {
			log.catching(Level.ERROR, e);
		}
	}

	private String getJavaLocation() {

		try {
			URI path = this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
			Path javaHome = Paths.get(path);
			Path embeddedJre = Paths.get("/jre/bin/javaw");
			javaHome = Paths.get(javaHome.toString(), embeddedJre.toString());
			if (Files.exists(javaHome)) {
				return javaHome.toString();
			}
		} catch (URISyntaxException e) {
			log.catching(Level.WARN, e);
		}

		Path javaHome = Paths.get(SystemUtils.JAVA_HOME, "bin", "javaw");
		if (Files.exists(javaHome)) {
			return javaHome.toString();
		}

		return "javaw";
	}

}
