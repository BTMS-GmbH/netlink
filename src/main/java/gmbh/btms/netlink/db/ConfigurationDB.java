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
package gmbh.btms.netlink.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.db.H2DatabaseType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import gmbh.btms.exception.DatabaseRuntimeException;
import gmbh.btms.netlink.NetlinkLogMessages;
import gmbh.btms.netlink.RuntimeConfig;
import gmbh.btms.netlink.config.Runtime;
import gmbh.btms.netlink.config.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Interface to the database.</p>
 *
 * @author Oliver Dornauf
 * @since 1.0.0
 */
public class ConfigurationDB implements java.lang.AutoCloseable {

	/**
	 * The log.
	 */
	private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(ConfigurationDB.class);
	private static final ConfigurationDB INSTANCE = new ConfigurationDB();

	private Dao<NetlinkDefinition, Integer> netlinkDefinitionDAO;
	private Dao<ValidatedResource, Integer> resourcesDAO;
	private ConnectionSource connectionSource;

	private ConfigurationDB() {

	}

	public static ConfigurationDB instance() {
		return INSTANCE;
	}

	public void shutdown() {

		if (connectionSource != null) {
			connectionSource.closeQuietly();
		}
	}

	public void initdb() {

		String dbConnectionString = databaseConnectionString();
		log.info(NetlinkLogMessages._003A, dbConnectionString);
		try (ConnectionSource connectionSource = new JdbcConnectionSource(dbConnectionString, "sa", "", new H2DatabaseType())) {

			TableUtils.createTableIfNotExists(connectionSource, gmbh.btms.netlink.config.Information.class);
			TableUtils.createTableIfNotExists(connectionSource, gmbh.btms.netlink.config.Application.class);
			TableUtils.createTableIfNotExists(connectionSource, gmbh.btms.netlink.config.Runtime.class);
			TableUtils.createTableIfNotExists(connectionSource, X509Attributes.class);
			TableUtils.createTableIfNotExists(connectionSource, gmbh.btms.netlink.config.ValidatedResource.class);
			TableUtils.createTableIfNotExists(connectionSource, gmbh.btms.netlink.config.NetlinkDefinition.class);
			DaoManager.createDao(connectionSource, Application.class);
			DaoManager.createDao(connectionSource, Information.class);
			DaoManager.createDao(connectionSource, ValidatedResource.class);
			DaoManager.createDao(connectionSource, Runtime.class);
			DaoManager.createDao(connectionSource, X509Attributes.class);
			netlinkDefinitionDAO = DaoManager.createDao(connectionSource, NetlinkDefinition.class);
			resourcesDAO = DaoManager.createDao(connectionSource, ValidatedResource.class);
			DeleteBuilder<NetlinkDefinition, Integer> deleteBuilder = netlinkDefinitionDAO.deleteBuilder();
			deleteBuilder.where().eq("launchable", "0");
			deleteBuilder.delete();
			this.connectionSource = connectionSource;
		} catch (SQLException | IOException ex) {
			throw new DatabaseRuntimeException(NetlinkLogMessages._0024, ex);
		}
	}


	private String databaseConnectionString() {

		StringBuilder dbUrl = new StringBuilder("jdbc:h2:");
		dbUrl.append(RuntimeConfig.instance().getLocalFileCache()).toString();
		dbUrl.append(File.separator);
		dbUrl.append("netlink");
		return dbUrl.toString();
	}

	@Override
	public void close() throws Exception {

		connectionSource.close();
	}

	public NetlinkDefinition createValidatedResourceCollection(NetlinkDefinition netlinkDefinition) {

		try {
			netlinkDefinitionDAO.assignEmptyForeignCollection(netlinkDefinition, "validatedResources");
			return netlinkDefinition;
		} catch (SQLException e) {
			throw new DatabaseRuntimeException(NetlinkLogMessages._0033, e);
		}

	}

	public NetlinkDefinition createConfiguration(NetlinkDefinition netlinkDefinition) {

		boolean retval = false;
		try {
			if (netlinkDefinition.getValidatedResources() == null) {
				netlinkDefinitionDAO.assignEmptyForeignCollection(netlinkDefinition, "validatedResources");
			}
			netlinkDefinition.setCached(false);
			netlinkDefinition.setLaunchable(false);
			return netlinkDefinition;
		} catch (SQLException e) {
			throw new DatabaseRuntimeException(NetlinkLogMessages._0033, e);
		}
	}

	public void cleanupLocalCache(NetlinkDefinition netlinkDefinition) {

		try {
			DeleteBuilder<NetlinkDefinition, Integer> deleteBuilder = netlinkDefinitionDAO.deleteBuilder();
			deleteBuilder.where().not().idEq(netlinkDefinition.getId());
			deleteBuilder.delete();
		} catch (SQLException ex) {
			log.catching(ex);
		}
	}

	public List<ValidatedResource> getUnusedValidatedResources(NetlinkDefinition netlinkDefinition) {

		try {
			List<ValidatedResource> otherValidatedResources = resourcesDAO.queryBuilder()
					                                                  .where().ne("NETLINKDEFINITION_ID", netlinkDefinition.getId()).query();
			List<ValidatedResource> expired = new ArrayList<ValidatedResource>();
			for (ValidatedResource validatedResourceOld : otherValidatedResources) {
				boolean found = false;
				for (ValidatedResource validatedResourceNew : netlinkDefinition.getValidatedResources()) {
					if (validatedResourceNew.getLocalPath().equals(validatedResourceOld.getLocalPath())) {
						found = true;
						break;
					}
				}
				if (!found) {
					expired.add(validatedResourceOld);
				}
			}
			return expired;
		} catch (SQLException ex) {
			log.catching(ex);
		}
		return null;
	}

	public void writeToDatabase(NetlinkDefinition netlinkDefinition) {

		try {
			netlinkDefinitionDAO.createOrUpdate(netlinkDefinition);
		} catch (SQLException ex) {
			log.catching(ex);
		}
	}

	public void storeValidatedResource(ValidatedResource validatedResource) {

		try {
			resourcesDAO.createOrUpdate(validatedResource);
		} catch (SQLException ex) {
			log.catching(ex);
		}
	}

	public void cleanupOldConfigurations(NetlinkDefinition netlinkDefinition) {

		try {
			List<NetlinkDefinition> otherNetlinkDefinitions = netlinkDefinitionDAO.queryBuilder()
					                                                  .where().ne("ID", netlinkDefinition.getId()).query();
			for (NetlinkDefinition expired : otherNetlinkDefinitions) {
				log.info(NetlinkLogMessages._003B, expired.getFileTimestamp());
				netlinkDefinitionDAO.delete(expired);
			}
		} catch (SQLException ex) {
			log.catching(ex);
		}
	}

	/**
	 * <p>load the most recent configuration from the database, which is younger than the referenceConfiguration parameter.</p>
	 *
	 * @param referenceConfiguration
	 * @return null if referenceConfiguration is the most recent one.
	 */
	public NetlinkDefinition loadMostRecentConfiguration(NetlinkDefinition referenceConfiguration) {

		try {
			long numberOfConfigurations = netlinkDefinitionDAO.countOf();
			if (numberOfConfigurations > 0) {
				long max = netlinkDefinitionDAO.queryRawValue("select max(filetimestamp) from netlink where launchable=1");
				if (max > 0) {
					return netlinkDefinitionDAO.queryBuilder().where().eq("filetimestamp", max).queryForFirst();
				}
			}
		} catch (SQLException ex) {
			log.catching(ex);
		}
		return null;
	}
}
