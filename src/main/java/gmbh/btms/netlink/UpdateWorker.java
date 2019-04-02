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

import com.j256.ormlite.dao.ForeignCollection;
import gmbh.btms.exception.ConfigurationRuntimeException;
import gmbh.btms.exception.DownloadRuntimeException;
import gmbh.btms.exception.SignerException;
import gmbh.btms.netlink.config.NetlinkDefinition;
import gmbh.btms.netlink.config.Resource;
import gmbh.btms.netlink.config.ValidatedResource;
import gmbh.btms.netlink.config.X509Attributes;
import gmbh.btms.netlink.db.ConfigurationDB;
import gmbh.btms.netlink.io.ConnectionStatus;
import gmbh.btms.netlink.io.DownloadItem;
import gmbh.btms.netlink.io.Downloader;
import gmbh.btms.netlink.io.ObservableReadableByteChannel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.*;
import javax.swing.*;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

/**
 * <p>The UpdateWorker implements the Update Workflow. The class is derived from SwingWorker and runs as a background task.</p>
 *
 * @author Oliver Dornauf
 * @since 1.0.0
 */
public class UpdateWorker extends SwingWorker<Void, Void> implements Observer {

	/**
	 * The log.
	 */
	private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(UpdateWorker.class);

	private ResourceBundle messages;

	public UpdateWorker() {

		messages = ResourceBundle.getBundle("messages", Locale.getDefault());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {

		if (o instanceof ObservableReadableByteChannel) {
			double progress = (double) arg;
			setProgress((int) progress);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	public Void doInBackground() {

		try {

			firePropertyChange("action", null, messages.getString("msg.integrityCheck"));

			log.info(NetlinkLogMessages._0039);
			NetlinkDefinition bootNetlinkDefinition = XMLConfigLoader.instance().loadBootNetlinkDefinition();

			firePropertyChange("information.title", null, bootNetlinkDefinition.getInformation().getTitle());
			firePropertyChange("application.version", null, messages.getString("msg.wait4Version"));
			firePropertyChange("information.codebase", null, bootNetlinkDefinition.getInformation().getCodebase());
			firePropertyChange("information.vendor", null, bootNetlinkDefinition.getInformation().getVendor());
			setProgress(0);

			ConfigurationDB.instance().initdb();

			if (isCancelled()) {
				return null;
			}

			boolean isOnline = ConnectionStatus.isURLAvailable(bootNetlinkDefinition.getInformation().getCodebase());
			RuntimeConfig.instance().setOnline(isOnline);
			if (isCancelled()) {
				return null;
			}

			NetlinkDefinition latestNetlinkDefinition = determineLatestConfiguration(bootNetlinkDefinition);

			if (!latestNetlinkDefinition.getRuntime().isOfflineAllowed() && !isOnline) {
				firePropertyChange("error", null, "error.offline");
				return null;
			}
			firePropertyChange("application.version", null, latestNetlinkDefinition.getApplication().getVersion());
			firePropertyChange("action", null, messages.getString("msg.checkVersion"));
			if (isCancelled()) {
				return null;
			}
			if (!latestNetlinkDefinition.isLaunchable()) {
				if (!isOnline) {
					firePropertyChange("error", null, "error.canNotLoad");
					return null;
				}
				latestNetlinkDefinition = updateJars(latestNetlinkDefinition);
			} else {
				boolean isOk = verifyAndRepairCache(latestNetlinkDefinition);
				if (isCancelled()){
					return null;
				}
				if (!isOk) {
					firePropertyChange("error", null, "error.canNotRepair");
					return null;
				}
			}
			firePropertyChange("action", null, messages.getString("msg.housekeeping"));
			prepareNativeRessources(latestNetlinkDefinition);
			firePropertyChange("action", null, messages.getString("msg.start"));

			final NetlinkDefinition df = latestNetlinkDefinition;
			Runnable r = new Runnable() {
				public void run() {
					ApplicationLauncher applicationLauncher = new ApplicationLauncher();
					applicationLauncher.startApplication(df);
				}
			};
			new Thread(r).start();

			List<ValidatedResource> unusedElements = ConfigurationDB.instance().getUnusedValidatedResources(latestNetlinkDefinition);
			for (ValidatedResource validatedResource : unusedElements) {
				log.info(NetlinkLogMessages._0027, validatedResource.getLocalPath());
				Files.deleteIfExists(validatedResource.getLocalPath());
			}
			ConfigurationDB.instance().cleanupOldConfigurations(latestNetlinkDefinition);
		} catch (Throwable ex) {
			log.catching(Level.ERROR, ex);
			firePropertyChange("exception", null, ex);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.swing.SwingWorker#done()
	 */
	@Override
	public void done() {
		ConfigurationDB.instance().shutdown();
	}

	/**
	 * <p>Determines the most current configuration. </p>
	 * <p>First, the current configuration is loaded from the database. If there is no entry yet, the bootConfiguration parameter is returned as the result.
	 * The timestamp of the most recent database configuration is compared to that of the remote configuration and the current one of both configurations is returned as the result.</p>
	 *
	 * @param bootConfiguration
	 * @return the most current configuration
	 */
	private NetlinkDefinition determineLatestConfiguration(NetlinkDefinition bootConfiguration) {

		try {
			URL remoteConfig = new URL(bootConfiguration.getInformation().getCodebase() + "/" + RuntimeConfig.CONFIG_FILENAME);
			setProgress(1);
			firePropertyChange("action", null, messages.getString("msg.downloadConfiguration"));

			NetlinkDefinition updatedNetlinkDefinition = ConfigurationDB.instance().loadMostRecentConfiguration(bootConfiguration);
			if (updatedNetlinkDefinition == null || !RuntimeConfig.instance().isOnline()) {
				return bootConfiguration;
			}
			FileTime timestampCurrentConfiguration = FileTime.fromMillis(updatedNetlinkDefinition.getFileTimestamp());
			FileTime timestampRemoteCOnfiguration = Downloader.getLastModified(remoteConfig);
			if (timestampCurrentConfiguration.compareTo(timestampRemoteCOnfiguration) == -1) {
				log.info(NetlinkLogMessages._0019, timestampCurrentConfiguration, timestampRemoteCOnfiguration);
				DownloadItem newConfigurationDownloadItem = downloadToTemp(remoteConfig);
				updatedNetlinkDefinition = XMLConfigLoader.instance().load(newConfigurationDownloadItem.getLocalFile());
				updatedNetlinkDefinition.setFileTimestamp(timestampRemoteCOnfiguration.toMillis());
			} else {
				log.info(NetlinkLogMessages._001A);
			}
			setProgress(5);
			return updatedNetlinkDefinition;
		} catch (MalformedURLException e) {
			ConfigurationRuntimeException ex = new ConfigurationRuntimeException(NetlinkLogMessages._0028, e);
			ex.addContextValue("URL", bootConfiguration.getInformation().getCodebase() + "/" + RuntimeConfig.CONFIG_FILENAME);
			throw ex;
		}
	}

	private boolean verifyAndRepairCache(NetlinkDefinition netlinkDefinition) {

		String msg = messages.getString("msg.verify");
		boolean isOk = false;

		JarProcessor verifier = new JarProcessor();
		ForeignCollection<ValidatedResource> validatedResources = netlinkDefinition.getValidatedResources();
		long totalSize = netlinkDefinition.getTotalSize();
		long downloadedSize = 0;

		for (ValidatedResource validatedResource : validatedResources) {
			if (isCancelled()) {
				return false;
			}
			firePropertyChange("action", null, msg + validatedResource.getFileName());
			X509Attributes verifiedSigner = null;
			boolean cached = true;
			validatedResource = preparePathInformation(validatedResource, netlinkDefinition);
			if (!Files.exists(validatedResource.getLocalPath())) {
				if (!RuntimeConfig.instance().isOnline()) {
					return false;
				}
				fixJAR(validatedResource, downloadedSize, totalSize);
			}
			verifiedSigner = verifier.validateJarFile(validatedResource.getLocalPath(), validatedResource.getX509Attributes());

			if (!verifiedSigner.getFingerprint().equals(validatedResource.getX509Attributes().getFingerprint())) {
				if (!RuntimeConfig.instance().isOnline()) {
					return false;
				}
				fixJAR(validatedResource, downloadedSize, totalSize);
			}
			downloadedSize += validatedResource.getSize();
			double downloaded = ObservableReadableByteChannel.predictProgress(downloadedSize, netlinkDefinition.getTotalSize());
			setProgress((int) downloaded);

		}
		return true;
	}


	private void fixJAR(ValidatedResource validatedResource, long downloadedSize, long totalSize) {

		log.info(NetlinkLogMessages._0036, validatedResource.getLocalPath());

		try {
			JarProcessor verifier = new JarProcessor();
			downloadValidatedResource(validatedResource, downloadedSize, totalSize);
			verifier.validateJarFile(validatedResource.getLocalTmpPath(), validatedResource.getX509Attributes());
			Files.move(validatedResource.getLocalTmpPath(), validatedResource.getLocalPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			SignerException ex = new SignerException(NetlinkLogMessages._0035);
			ex.addContextValue("FILE", validatedResource.getLocalPath());
			throw ex;
		}
	}

	private NetlinkDefinition updateJars(NetlinkDefinition netlinkDefinition) {

		JarProcessor verifier = new JarProcessor();
		ConfigurationDB.instance().writeToDatabase(netlinkDefinition);
		netlinkDefinition = ConfigurationDB.instance().createValidatedResourceCollection(netlinkDefinition);

		try {
			long downloadedSize = 0;
			for (Resource resource : netlinkDefinition.getResources()) {
				if (isCancelled()) {
					return null;
				}
				if (isResourceRelevant4Plattform(resource)) {
					ValidatedResource validatedResource = new ValidatedResource(resource);
					validatedResource = preparePathInformation(validatedResource, netlinkDefinition);
					X509Attributes verifedSigner = null;
					boolean cached = downloadValidatedResource(validatedResource, downloadedSize, netlinkDefinition.getTotalSize());
					if (cached) {
						verifedSigner = verifier.validateJarFile(validatedResource.getLocalPath(), netlinkDefinition.getInformation().getX509Attributes());
					} else {
						verifedSigner = verifier.validateJarFile(validatedResource.getLocalTmpPath(), netlinkDefinition.getInformation().getX509Attributes());
						Files.move(validatedResource.getLocalTmpPath(), validatedResource.getLocalPath(), StandardCopyOption.REPLACE_EXISTING);
					}
					validatedResource.setFileName(validatedResource.getFileName());
					validatedResource.setX509Attributes(verifedSigner);

					netlinkDefinition.getValidatedResources().add(validatedResource);
					downloadedSize += validatedResource.getSize();
				}
			}
			netlinkDefinition.setLaunchable(true);
			ConfigurationDB.instance().writeToDatabase(netlinkDefinition);
			return netlinkDefinition;
		} catch (IOException ex) {
			throw new DownloadRuntimeException(NetlinkLogMessages._0038, ex);
		}
	}

	private boolean downloadValidatedResource(ValidatedResource validatedResource, long downloadedSize, long totalSize) {

		try {
			String msg = messages.getString("msg.checkVersion");
			StringBuilder fileDownload = new StringBuilder(msg).append(validatedResource.getFileName());
			firePropertyChange("action", null, fileDownload.toString());
			Files.createDirectories(validatedResource.getLocalPath().getParent());
			Files.createDirectories(validatedResource.getLocalTmpPath().getParent());
			return downloadIfDeprecated(validatedResource, downloadedSize, totalSize);
		} catch (IOException ex) {
			throw new DownloadRuntimeException(NetlinkLogMessages._001C, ex);
		}
	}

	private void prepareNativeRessources(NetlinkDefinition netlinkDefinition) {

		JarProcessor verifier = new JarProcessor();

		for (ValidatedResource validatedResource : netlinkDefinition.getValidatedResources()) {
			if (validatedResource.isNative() && Files.exists(validatedResource.getLocalPath())) {
				verifier.unzipResource(validatedResource.getLocalPath(), netlinkDefinition);
			}
		}
	}


	private DownloadItem downloadToTemp(URL url) {

		Path localFile = null;
		try {
			Path localPath = RuntimeConfig.instance().getTempFolder();
			if (!Files.exists(localPath)) {
				Files.createDirectory(RuntimeConfig.instance().getTempFolder());
			}
			localFile = Paths.get(localPath.toString(), url.getFile());
			Files.deleteIfExists(localFile);
			Files.createDirectories(localFile.getParent());
			Files.createFile(localFile);
		} catch (IOException ex) {
			throw new DownloadRuntimeException(NetlinkLogMessages._001D, ex);
		}

		try {
			FileTime timestamp = Downloader.getLastModified(url);
			URLConnection connection = url.openConnection();
			InputStream in = connection.getInputStream();
			OutputStream fos = Files.newOutputStream(localFile);
			byte[] buf = new byte[8196];
			while (true) {
				int len = in.read(buf);
				if (len == -1) {
					break;
				}
				fos.write(buf, 0, len);
				if (isCancelled()) {
					return null;
				}
			}
			in.close();
			fos.flush();
			fos.close();

			DownloadItem downloadItem = DownloadItem.newBuilder().withLocalFile(localFile).withFileTime(timestamp).build();
			return downloadItem;
		} catch (IOException ex) {
			log.catching(Level.ERROR, ex);
		}
		return null;
	}

	private boolean isResourceRelevant4Plattform(Resource resource) {

		String os = System.getProperty("os.name");
		String arch = System.getProperty("os.arch");

		if (resource.getOs() != null) {
			if (!StringUtils.isEmpty(resource.getOs()) && !StringUtils.startsWith(os, resource.getOs())) {
				log.info(NetlinkLogMessages._0029, os, resource.getOs(), resource.getFileName());
				return false;
			}
		}
		if (resource.getArch() != null) {
			if (!StringUtils.isEmpty(resource.getArch()) && !StringUtils.startsWith(arch, resource.getArch())) {
				log.info(NetlinkLogMessages._0030, arch, resource.getArch(), resource.getFileName());
				return false;
			}
		}
		return true;
	}

	private ValidatedResource preparePathInformation(ValidatedResource validatedResource, NetlinkDefinition netlinkDefinition) {

		try {
			String codeBaseAsString = netlinkDefinition.getInformation().getCodebase();
			URI codebase = new URI(StringUtils.appendIfMissing(codeBaseAsString, "/"));
			URI remoteFilename = codebase.resolve(validatedResource.getHref());

			String relativePath = codebase.relativize(remoteFilename).toString();
			validatedResource.setLocalPath(Paths.get(RuntimeConfig.instance().getLocalFileCache().toString(), netlinkDefinition.getInformation().getTitle(), relativePath));
			validatedResource.setLocalTmpPath(Paths.get(FileUtils.getTempDirectoryPath(), "netlink", relativePath));
			Path localPath = validatedResource.getLocalPath();
			validatedResource.setFileName(localPath.getFileName().toString());
			validatedResource.setRemoteLocation(remoteFilename.toURL());
			return validatedResource;
		} catch (MalformedURLException | URISyntaxException ex) {
			throw new ConfigurationRuntimeException(NetlinkLogMessages._001B, ex);
		}
	}

	private boolean downloadIfDeprecated(ValidatedResource validatedResource, long downloadedSize, long totalSize) {

		FileTime timestampCachedFile;
		try {
			if (Files.exists(validatedResource.getLocalPath())) {
				BasicFileAttributes attr = Files.readAttributes(validatedResource.getLocalPath(), BasicFileAttributes.class);
				timestampCachedFile = attr.creationTime();
			} else {
				timestampCachedFile = FileTime.fromMillis(0);
			}
			FileTime timestampRemoteFile = Downloader.getLastModified(validatedResource.getRemoteLocation());
			if (timestampRemoteFile.compareTo(timestampCachedFile) == 1) {
				String msg = messages.getString("msg.download");
				StringBuilder fileDownload = new StringBuilder(msg).append(validatedResource.getFileName());
				firePropertyChange("action", null, fileDownload.toString());
				FileOutputStream fos = new FileOutputStream(validatedResource.getLocalTmpPath().toFile());
				URL url = validatedResource.getRemoteLocation();
				ObservableReadableByteChannel observableReadableByteChannel = new ObservableReadableByteChannel(Channels.newChannel(url.openStream()), downloadedSize, totalSize);
				observableReadableByteChannel.addObserver(this);
				fos.getChannel().transferFrom(observableReadableByteChannel, 0, Long.MAX_VALUE);
				Files.setAttribute(validatedResource.getLocalTmpPath(), "basic:creationTime", timestampRemoteFile, NOFOLLOW_LINKS);
				fos.close();
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			DownloadRuntimeException ex = new DownloadRuntimeException(NetlinkLogMessages._0020, e);
			ex.addContextValue("FILE", validatedResource.getFileName());
			throw ex;
		}
	}

	private URL getRemoteConfigFileName(NetlinkDefinition netlinkDefinition) {

		try {
			String configFile = RuntimeConfig.CONFIG_FILENAME;
			String codebaseAsString = netlinkDefinition.getInformation().getCodebase() + "/" + configFile;
			return new URL(codebaseAsString);
		} catch (MalformedURLException ex) {
			throw new ConfigurationRuntimeException(NetlinkLogMessages._001C, ex);
		}
	}
}