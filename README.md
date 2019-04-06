[![Build Status](https://travis-ci.org/BTMS-GmbH/netlink.svg?branch=master)](https://travis-ci.org/BTMS-GmbH/netlink)
[![Apache 2.0 License](https://img.shields.io/badge/license-Apache%202.0-blue.svg) ](hhttps://github.com/BTMS-GmbH/netlink/blob/master/LICENSE)
[![Bintry](https://api.bintray.com/packages/btms/maven/netlink/images/download.svg) ](https://bintray.com/btms/maven/netlink/_latestVersion)
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v1.4%20adopted-ff69b4.svg)](code-of-conduct.md) 
# netlink
## purpose
Netlink is an update mechanism for Java applications. The integrity is verified with the help of digital signatures.

## Deployment scenario:
Netlink is suitable for use in companies. The application is distributed once per installer and keeps itself up-to-date independently in the future. It is recommended to install an appropriate JRE locally for the application.

## Prerequisites and security:
* The first installation is carried out by an administrative user.
* The end user can only access the installation directory in read-only mode.
* Updates are kept in a cache.
* The signature verification is only effective as long as the JRE and Netlink cannot be manipulated from the outside.

## History:
Netlink was created in 2001 for the automatic update of the desktop client of BTMS Travel Expense Accounting RKA². Later we only used Netlink where Java WebStart could not be used.
Since Oracle has cancelled the support of WebStart, we continue the development actively again.applicationpurpose
Netlink is an update mechanism for Java applications. The integrity is verified with the help of digital signatures.
