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
