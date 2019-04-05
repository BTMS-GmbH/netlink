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

import gmbh.btms.exception.SignerException;
import gmbh.btms.netlink.config.NetlinkDefinition;
import gmbh.btms.netlink.config.X509Attributes;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.security.auth.x500.X500Principal;

/**
 * <p>Jar File Processor for netlink to validateJarFile and unpack jar files.</p>
 *
 * @author Oliver Dornauf
 * @since 1.0.0
 */
public class JarProcessor {

	/**
	 * The log.
	 */
	private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(JarProcessor.class);
	/**
	 * Mapping OID to distinguished name components
	 *
	 * @see <a href="https://oid-info.com/index.htm">https://oid-info.com/index.htm</a>
	 */
	private Map<String, String> knownOids;
	/**
	 * Mapping between distinguished name components and more readable properties
	 */
	private Map<String, String> readableNames;

	/**
	 * <p>Verification of the signature of a Jar file.</p>
	 * <p>The verification must be performed for each element (JarEntry) of the archive. The verification takes place in steps:</p>
	 * <ul>
	 * <li>Open Jar</li>
	 * <li>Load JarEntry -> If the signature is not secure, the ClassLoader will make a SecurityException.</li>
	 * <li>Compare signature.<br/>
	 * The system checks whether the signature contains the corresponding fields from the signature. This ensures that the signature is not only considered "secure", but
	 * has also been signed by the corresponding organization.</li
	 * </ul>
	 */
	public JarProcessor() {

		knownOids = new HashMap<>();
		knownOids.put("2.5.4.3", "CN");
		knownOids.put("2.5.4.6", "C");
		knownOids.put("2.5.4.7", "L");
		knownOids.put("2.5.4.8", "ST");
		knownOids.put("2.5.4.9", "STREET");
		knownOids.put("2.5.4.10", "O");
		knownOids.put("2.5.4.11", "OU");
		knownOids.put("0.9.2342.19200300.100.1.25", "DC");
		knownOids.put("1.2.840.113549.1.9.1", "EMAILADDRESS");

		readableNames = new HashMap<>();
		readableNames.put("CN", "commonName");
		readableNames.put("C", "countryName");
		readableNames.put("L", "localityName");
		readableNames.put("ST", "stateOrProvinceName");
		readableNames.put("STREET", "streetAddress");
		readableNames.put("O", "organizationName");
		readableNames.put("OU", "organizationalUnitName");
		readableNames.put("DC", "domainComponent");
		readableNames.put("EMAILADDRESS", "emailAddress");
	}

	/**
	 * @see JarFile
	 */
	public X509Attributes validateJarFile(Path jarFilePath, X509Attributes expectedx509Attributes) {

		X509Attributes firstVerifyedSigner = null;
		X509Certificate firstVerifiedCertificate = null;
		log.info(NetlinkLogMessages._0037, jarFilePath);
		try (JarFile jarFile = new JarFile(jarFilePath.toFile(), true)) {
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry jarEntry = entries.nextElement();
				if (isSignaturRelevant(jarEntry)) {
					byte[] buffer = new byte[32768];
					try (InputStream is = jarFile.getInputStream(jarEntry)) {
						int n;
						while ((n = is.read(buffer, 0, buffer.length)) != -1) {
						}
					}
					X509Attributes verifiedSigner = validateEntrySigner(jarEntry, expectedx509Attributes);
					if (verifiedSigner == null) {
						SignerException ex = new SignerException(NetlinkLogMessages._003C);
						ex.addContextValue("FILE", jarFilePath);
						throw ex;
					}
					if (firstVerifyedSigner == null) {
						firstVerifyedSigner = verifiedSigner;
						firstVerifiedCertificate = getCertificate(jarEntry);
					}
				}
			}
			jarFile.close();
			MessageDigest md = MessageDigest.getInstance("SHA-224");
			byte[] der = firstVerifiedCertificate.getEncoded();
			md.update(der);
			byte[] fingerPrint = md.digest();
			firstVerifyedSigner.setFingerprint(byteArrayToHex(fingerPrint));

		} catch (IOException | CertificateEncodingException | NoSuchAlgorithmException | SecurityException e) {
			SignerException ex = new SignerException(NetlinkLogMessages._0025, e);
			ex.addContextValue("FILE", jarFilePath);
			throw ex;
		}
		return firstVerifyedSigner;
	}

	/**
	 * @see JarEntry#getCertificates()
	 */
	private X509Attributes validateEntrySigner(JarEntry jarEntry, X509Attributes x509Attributes) {

		log.debug(NetlinkLogMessages._001E, jarEntry.getName());
		if (isSignaturRelevant(jarEntry)) {
			// see JarEntry.getCertificates()
			Certificate[] certificates = jarEntry.getCertificates();
			if (certificates!= null && certificates.length > 0) {
				X509Certificate certificateForValidation = (X509Certificate) certificates[0];
				X509Attributes verifyedSigner = verifySigner(certificateForValidation, x509Attributes);
				return verifyedSigner;
			}
		}
		return null;
	}

	/**
	 * @see JarEntry#getCertificates()
	 */
	private X509Certificate getCertificate(JarEntry jarEntry) {

		log.debug(NetlinkLogMessages._001E, jarEntry.getName());
		if (isSignaturRelevant(jarEntry)) {
			// see JarEntry.getCertificates()
			Certificate[] certificates = jarEntry.getCertificates();
			if (certificates != null && certificates.length > 0) {
				X509Certificate certificateForValidation = (X509Certificate) certificates[0];
				return certificateForValidation;
			}
		}
		return null;
	}

	public static String byteArrayToHex(byte[] bytes) {

		char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

		StringBuilder buf = new StringBuilder(bytes.length * 2);

		for (int i = 0; i < bytes.length; ++i) {
			buf.append(hexDigits[(bytes[i] & 0xf0) >> 4]);
			buf.append(hexDigits[bytes[i] & 0x0f]);
		}

		return buf.toString();
	}

	private boolean isSignaturRelevant(JarEntry je) {

		boolean isSignatureRelevant = true;
		if (je.isDirectory()) {
			return false;
		}
		String name = StringUtils.upperCase(je.getName(), Locale.ENGLISH);
		return !name.startsWith("META-INF");
	}

	private X509Attributes verifySigner(X509Certificate certificate, X509Attributes x509Attributes) {

		X509Attributes verifiedSigner = new X509Attributes();
		try {
			PublicKey certificatePublicKey = certificate.getPublicKey();
			X500Principal principal = certificate.getSubjectX500Principal();
			String pricipalDistinguishedName = principal.getName(X500Principal.RFC2253, knownOids);

			String[] items = StringUtils.split(pricipalDistinguishedName, ',');

			for (String item : items) {
				String[] component = StringUtils.split(item, "=");
				if (component.length == 2) {
					String propertyName = readableNames.get(component[0]);
					if (propertyName != null) {
						Object expectedValue = PropertyUtils.getProperty(x509Attributes, propertyName);
						if (expectedValue != null && expectedValue.toString().length() > 0) {
							if (!expectedValue.equals(component[1])) {
								SignerException ex = new SignerException(NetlinkLogMessages._0026);
								ex.addContextValue("EXPECTED", expectedValue);
								ex.addContextValue("FOUND", component[1]);
								throw ex;
							}
						}
						PropertyUtils.setProperty(verifiedSigner, propertyName, component[1]);
					}
				}
			}
		} catch (IllegalAccessException | NoSuchElementException | NoSuchMethodException | InvocationTargetException e) {
			log.catching(Level.ERROR, e);
			return null;
		}
		return verifiedSigner;
	}

	public void unzipResource(Path jarFilePath, Path targetPath, NetlinkDefinition netlinkDefinition) {

		try {
			if (!Files.exists(targetPath)) {
				Files.createDirectories(targetPath);
			}
		} catch (IOException ex) {
			log.catching(Level.ERROR, ex);
		}
		try (JarFile jarFile = new JarFile(jarFilePath.toFile())) {
			Enumeration<JarEntry> entries = jarFile.entries();
			JarEntry je = null;
			while (entries.hasMoreElements()) {
				je = entries.nextElement();
				if (isSignaturRelevant(je)) {
					try (InputStream is = jarFile.getInputStream(je)) {
						Path file = Paths.get(targetPath.toString(), je.getName());
						if (!Files.exists(file)) {
							Files.createFile(file);
							FileOutputStream fos = new FileOutputStream(file.toFile());
							byte[] buffer = new byte[8192];
							int bytesRead;
							while ((bytesRead = is.read(buffer)) != -1) {
								fos.write(buffer, 0, bytesRead);
							}
						}
					}
				}
			}
		} catch (IOException ex) {
			System.out.println(ex);
		}
	}
}
