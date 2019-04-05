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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import gmbh.btms.exception.ConfigurationRuntimeException;
import gmbh.btms.netlink.config.Runtime;
import gmbh.btms.netlink.config.*;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.CodeSource;
import java.util.List;

/**
 * XMLConfigLoader
 *
 * @author Oliver Dornauf
 * @since 1.0.0
 */
public class XMLConfigLoader {

	/**
	 * The log.
	 */
	private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(XMLConfigLoader.class);

	private static final XMLConfigLoader INSTANCE = new XMLConfigLoader();
	private XStream xstream = null;

	private XMLConfigLoader() {
		xstream = new XStream();
		xstream.alias("netlink", NetlinkDefinition.class);

		xstream.alias("information", Information.class);

		xstream.alias("application", Application.class);

		xstream.alias("runtime", Runtime.class);

		xstream.alias("x509Attributes", X509Attributes.class);
		xstream.useAttributeFor(X509Attributes.class, "commonName");
		xstream.useAttributeFor(X509Attributes.class, "countryName");
		xstream.useAttributeFor(X509Attributes.class, "localityName");
		xstream.useAttributeFor(X509Attributes.class, "stateOrProvinceName");
		xstream.useAttributeFor(X509Attributes.class, "streetAddress");
		xstream.useAttributeFor(X509Attributes.class, "organizationName");
		xstream.useAttributeFor(X509Attributes.class, "domainComponent");
		xstream.useAttributeFor(X509Attributes.class, "organizationalUnitName");
		xstream.useAttributeFor(X509Attributes.class, "emailAddress");

		xstream.alias("resource", Resource.class);
		xstream.useAttributeFor(Resource.class, "isNative");
		xstream.useAttributeFor(Resource.class, "arch");
		xstream.useAttributeFor(Resource.class, "os");
		xstream.useAttributeFor(Resource.class, "size");
		xstream.useAttributeFor(Resource.class, "version");
		xstream.useAttributeFor(Resource.class, "href");
		xstream.useAttributeFor(Resource.class, "targetPath");

		xstream.registerConverter(new ComparableVersionConverter());
	}

	public NetlinkDefinition loadBootNetlinkDefinition() {

		Path bootConfigurationFile = null;
		CodeSource codeSource = null;

		try {
			codeSource = NetlinkLauncher.class.getProtectionDomain().getCodeSource();
			File jarFilePath = new File(codeSource.getLocation().toURI().getPath());
			if (StringUtils.endsWithIgnoreCase(jarFilePath.getName(), ".jar")) {
				bootConfigurationFile = Paths.get(jarFilePath.getParentFile().getPath(), RuntimeConfig.CONFIG_FILENAME);
			} else {
				bootConfigurationFile = Paths.get(jarFilePath.getPath(), RuntimeConfig.CONFIG_FILENAME);
			}
		}
		catch (URISyntaxException e) {
			ConfigurationRuntimeException ex = new ConfigurationRuntimeException(NetlinkLogMessages._0032, e);
			ex.addContextValue("CodeSource", codeSource.getLocation().toString());
			throw ex;
		}

		log.info(NetlinkLogMessages._0018, bootConfigurationFile);
		if (!Files.exists(bootConfigurationFile, LinkOption.NOFOLLOW_LINKS)) {
			ConfigurationRuntimeException ex = new ConfigurationRuntimeException(NetlinkLogMessages._0031);
			ex.addContextValue("FILE", "bootConfigurationFile");
			throw ex;
		}
		NetlinkDefinition bootNetlinkDefinition = XMLConfigLoader.instance().load(bootConfigurationFile);
		RuntimeConfig.instance().setLocalFileRoot(Paths.get(RuntimeConfig.instance().getLocalFileCache().toString()));
		return bootNetlinkDefinition;
	}

	/**
	 * <p></p>
	 *
	 * @param fileName
	 * @return
	 */
	public NetlinkDefinition load(Path fileName) {

		try {
			InputStream is = Files.newInputStream(fileName);
			NetlinkDefinition netlinkDefinition = load(is);
			BasicFileAttributes attr = Files.readAttributes(fileName, BasicFileAttributes.class);
			netlinkDefinition.setFileTimestamp(attr.creationTime().toMillis());
			List<Resource> ressources = netlinkDefinition.getResources();

			long totalSize = ressources.stream().mapToLong(o -> o.getSize()).sum();
			netlinkDefinition.setTotalSize(totalSize);

			return netlinkDefinition;
		} catch (IOException ex) {
			ConfigurationRuntimeException exception = new ConfigurationRuntimeException(NetlinkLogMessages._0034, ex);
			exception.addContextValue("FILE", fileName);
			throw exception;
		}
	}

	public static XMLConfigLoader instance() {
		return INSTANCE;
	}

	public NetlinkDefinition load(InputStream is) {

		try {
			NetlinkDefinition netlinkDefinition = (NetlinkDefinition) xstream.fromXML(is);
			netlinkDefinition.setCached(false);
			return netlinkDefinition;
		} catch (XStreamException ex) {
			ConfigurationRuntimeException exception = new ConfigurationRuntimeException(NetlinkLogMessages._001F, ex);
			throw exception;
		}
	}
}
