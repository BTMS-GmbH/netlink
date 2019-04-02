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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.OnStartupTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.RolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.net.URI;

/**
 * <p>Configuration of Log4j2. The view of log files makes it possible to define the position of the LOG file in the file system at a later time than the program start.</p>
 * <p>It is also possible to adjust the LOG configuration at runtime.</p>
 *
 * @author Oliver Dornauf
 * @since 1.0.0
 */
@Plugin(
		name = "BtmsJnlpLogFactory",
		category = ConfigurationFactory.CATEGORY
)
@Order(50)
public class Log4j2Configuration extends ConfigurationFactory {

	/**
	 * Field description
	 */
	private static final Log4j2Configuration instance = new Log4j2Configuration();

	private static final String CONSOLE_PATTERN = "%highlight{[%-5level] : %msg%n%throwable}{FATAL=red, ERROR=red, WARN=red, INFO=blue, DEBUG=white}";
	private static final String FILE_PATTERN = "%level %t %d %encode{%message}{CRLF}%n%throwable";

	//~--- fields -------------------------------------------------------------

	/**
	 * Field description
	 */
	private LoggerContext ctx;

	/**
	 * Field description
	 */
	private String logFileName;

	//~--- methods ------------------------------------------------------------

	/**
	 * Method description
	 */
	public static Log4j2Configuration getInstance() {
		return instance;
	}

	/**
	 * Method description
	 */
	public void activeConsoleLogging() {
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		PatternLayout consoleLayout = PatternLayout.newBuilder()
				                              .withPattern(CONSOLE_PATTERN)
				                              .withDisableAnsi(false)
				                              .build();
		ConsoleAppender consoleAppender = ConsoleAppender.newBuilder()
				                                  .withName("console")
				                                  .withLayout(consoleLayout)
				                                  .build();

		consoleAppender.start();

		Logger logger = context.getRootLogger();
		logger.addAppender(consoleAppender);
		context.updateLoggers();
	}

	/**
	 * Method description
	 */
	public void setLogFileName(String logFileName) {
		this.logFileName = logFileName;
		createFileAppender();
	}

	//~--- get methods --------------------------------------------------------

	public void createFileAppender() {
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		Configuration configuration = context.getConfiguration();
		PatternLayout fileLayout = PatternLayout.newBuilder()
				                           .withPattern(FILE_PATTERN)
				                           .withDisableAnsi(true)
				                           .build();
		StringBuilder filePattern = new StringBuilder(logFileName);
		int pos = filePattern.indexOf(".log");

		filePattern.replace(pos, filePattern.length(), "-%i.log");

		TriggeringPolicy policy = OnStartupTriggeringPolicy.createPolicy(0);
		RolloverStrategy strategy = DefaultRolloverStrategy.newBuilder()
				                            .withConfig(configuration)
				                            .withMin("1")
				                            .withMax("7")
				                            .build();
		RollingFileAppender rollingAppender = RollingFileAppender.newBuilder()
				                                      .withFileName(logFileName)
				                                      .withFilePattern(filePattern.toString())
				                                      .withAppend(true)
				                                      .withLocking(false)
				                                      .withName("FILE")
				                                      .withPolicy(policy)
				                                      .withStrategy(strategy)
				                                      .withImmediateFlush(true)
				                                      .withIgnoreExceptions(true)
				                                      .withBufferedIo(true)
				                                      .withBufferSize(8092)
				                                      .withLayout(fileLayout)
				                                      .withAdvertise(false)
				                                      .build();

		rollingAppender.start();

		Logger logger = context.getRootLogger();
		logger.addAppender(rollingAppender);
		context.updateLoggers();
	}

	@Override
	protected String[] getSupportedTypes() {
		return new String[]{"*"};
	}

	@Override
	public Configuration getConfiguration(final LoggerContext loggerContext, final ConfigurationSource source) {
		return getConfiguration(loggerContext, source.toString(), null);
	}

	//~--- set methods --------------------------------------------------------

	static Configuration createConfiguration(final String name, ConfigurationBuilder<BuiltConfiguration> builder) {

		builder.setConfigurationName(name);
		builder.setStatusLevel(Level.ERROR);
		builder.add(builder.newFilter("ThresholdFilter", Filter.Result.ACCEPT, Filter.Result.NEUTRAL).addAttribute("level", Level.WARN));
		AppenderComponentBuilder console = builder.newAppender("stdout", "Console");
		builder.add(console);
		RootLoggerComponentBuilder rootLogger = builder.newRootLogger(Level.ERROR);
		rootLogger.add(builder.newAppenderRef("stdout"));
		builder.add(builder.newLogger("gmbh.btms", Level.INFO));
		builder.add(rootLogger);
		return builder.build();
	}

	//~--- get methods --------------------------------------------------------

	@Override
	public Configuration getConfiguration(final LoggerContext loggerContext, final String name,
	                                      final URI configLocation) {
		ConfigurationBuilder<BuiltConfiguration> builder = newConfigurationBuilder();

		return createConfiguration(name, builder);
	}
}
