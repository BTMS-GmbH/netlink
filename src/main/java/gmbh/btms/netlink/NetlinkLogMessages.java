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

/**
 * <p>For a consistent runtime log, all log messages for the netlink package are defined in these classes.</p>
 * <p>The additional effort pays off in the case of an error analysis.</p>
 *
 * @author Oliver Dornauf
 * @since 1.0.0
 */
public class NetlinkLogMessages {

	public static final String _0000 = "0x0000 | -------------------------------------------------------------------[ VERSION ]--------";
	public static final String _0001 = "0x0001 | BTMS JNLP Launcher Version  [{}]";
	public static final String _0002 = "0x0002 | -------------------------------------------------------------------[    USER ]--------";
	public static final String _0003 = "0x0003 | USER_COUNTRY : {}";
	public static final String _0004 = "0x0004 | USER_DIR     : {}";
	public static final String _0005 = "0x0005 | USER_HOME    : {}";
	public static final String _0006 = "0x0006 | USER_LANGUAGE: {}";
	public static final String _0007 = "0x0007 | USER_NAME    : {}";
	public static final String _0008 = "0x0008 | USER_TIMEZONE: {}";
	public static final String _0009 = "0x0009 | -------------------------------------------------------------------[    JAVA ]--------";
	public static final String _000A = "0x000A | JRT           : {} Version {}";
	public static final String _000B = "0x000B | AWT_TOOLKIT   : {}";
	public static final String _000C = "0x000C | File endcoding: {}";
	public static final String _000D = "0x000D | JAVA_HOME     : {}";
	public static final String _000E = "0x000E | JAVA_LIBRARY_PATH : {}";
	public static final String _000F = "0x000F | JAVA_ENDORSED_DIRS: {}";
	public static final String _0010 = "0x0010 | JAVA_IO_TMPDIR    : {}";
	public static final String _0011 = "0x0011 | JAVA VERSION {} CLASS VERSION {} VM INFO:{}";
	public static final String _0012 = "0x0012 | -------------------------------------------------------------------[      OS ]--------";
	public static final String _0013 = "0x0013 | File separator : |{}|";
	public static final String _0014 = "0x0014 | OS_NAME : {}";
	public static final String _0015 = "0x0015 | OS_VERSION : {}";
	public static final String _0016 = "0x0016 | OS_ARCH : {}";
	public static final String _0017 = "0x0017 | --------------------------------------------------------------------------------------";
	public static final String _0018 = "0x0018 | load boot configuration      {}";
	public static final String _0019 = "0x0019 | configuration form {} was updated with {}";
	public static final String _001A = "0x001A | the latest configuration version is already available locally.";
	public static final String _001B = "0x001B | Error in netlink configuration file, validateJarFile URL syntax [{}]";
	public static final String _001C = "0x001C | Codebase has wrong URL format";
	public static final String _001D = "0x001D | Error while create local netlink.config file for {}";
	public static final String _001E = "0x001E | verifying entry {}";
	public static final String _001F = "0x001F | Invalid netlink configuration file";
	public static final String _0020 = "0x0020 | Element is neither locally nor remotely available.";
	public static final String _0021 = "0x0021 | URL {} is currently not available due bad syntax";
	public static final String _0022 = "0x0022 | No local network interface found. {}";
	public static final String _0023 = "0x0023 | No external network interface found. {}";
	public static final String _0024 = "0x0024 | Database access error.";
	public static final String _0025 = "0x0025 | Error verifing jar file";
	public static final String _0026 = "0x0026 | Error verifing jar signer";
	public static final String _0027 = "0x0027 | Deleting expired jar file {}";
	public static final String _0028 = "0x0028 | Error in boot configuration, can not create remote url";
	public static final String _0029 = "0x0029 | The operating system [{}] does not correspond to the default [{}] for the resource [{}]";
	public static final String _0030 = "0x0030 | The system architecture [{}]does not correspond to the default [{}] for the resource [{}]";
	public static final String _0031 = "0x0031 | System installation is damaged, please contact service";
	public static final String _0032 = "0x0032 | Error in netlink boot configuration file, validateJarFile URL syntax";
	public static final String _0033 = "0x0033 | Can not store xml configuration into database";
	public static final String _0034 = "0x0034 | Error reading configuration file";
	public static final String _0035 = "0x0035 | Can not repair jar file";
	public static final String _0036 = "0x0036 | Repair JAR-File {}";
	public static final String _0037 = "0x0037 | Verifying JAR {}";
	public static final String _0038 = "0x0038 | JAR was not downloaded to temp";
	public static final String _0039 = "0x0039 | Load boot configuration";
	public static final String _003A = "0x003A | Initialize Database {}";
	public static final String _003B = "0x003B | Remove old configuration, timestamp {}";
	public static final String _003C = "0x003C | Jar file is not signed";
}
