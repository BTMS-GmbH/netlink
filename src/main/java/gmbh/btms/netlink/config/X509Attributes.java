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
 * X509Attributes
 *
 * @author Oliver Dornauf
 * @since 7.0.0
 */
@DatabaseTable(tableName = "X590ATTR")
public class X509Attributes {

	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	// CN 2.5.4.3
	private String commonName;
	@DatabaseField
	// C 2.5.4.6
	private String countryName;
	@DatabaseField
	// L 2.5.4.7
	private String localityName;
	@DatabaseField
	// ST 2.5.4.8
	private String stateOrProvinceName;
	@DatabaseField
	// STREET 2.5.4.9
	private String streetAddress;
	@DatabaseField
	// O 2.5.4.10
	private String organizationName;
	@DatabaseField
	// OU 2.5.4.11
	private String organizationalUnitName;
	@DatabaseField
	// DC 0.9.2342.19200300.100.1.25
	private String domainComponent;
	@DatabaseField
	// emailAddress 1.2.840.113549.1.9.1
	private String emailAddress;
	@DatabaseField
	private String fingerprint;

	public X509Attributes() {

	}

	public String getFingerprint() {
		return fingerprint;
	}

	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
	}

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getLocalityName() {
		return localityName;
	}

	public void setLocalityName(String localityName) {
		this.localityName = localityName;
	}

	public String getStateOrProvinceName() {
		return stateOrProvinceName;
	}

	public void setStateOrProvinceName(String stateOrProvinceName) {
		this.stateOrProvinceName = stateOrProvinceName;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getOrganizationalUnitName() {
		return organizationalUnitName;
	}

	public void setOrganizationalUnitName(String organizationalUnitName) {
		this.organizationalUnitName = organizationalUnitName;
	}

	public String getDomainComponent() {
		return domainComponent;
	}

	public void setDomainComponent(String domainComponent) {
		this.domainComponent = domainComponent;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
}
