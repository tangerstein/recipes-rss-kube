/*
 * Copyright 2012 Netflix, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.netflix.recipes.rss;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.inject.Singleton;
import com.netflix.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * RSSConfiguration follows a hierarchy as follows: <appId>-<env>.properties
 * (optional: <env>=local|dev|qa|prod) <appId>.properties (default values)
 * System Properties (-D)
 * 
 * JMX: All properties can be viewed and updated (on a per instance basis) here:
 * Config-com.netflix.config.jmx.BaseConfigMBean
 * 
 * @author Chris Fregly (chris@fregly.com)
 */
@Singleton
public class RSSConfiguration implements AppConfiguration {
	private static final Logger logger = LoggerFactory.getLogger(RSSConfiguration.class);
	private boolean initialized = false;

	public RSSConfiguration() {
		String appId = System.getProperty("archaius.deployment.applicationId");
		String env = System.getProperty("archaius.deployment.environment");

		if (Strings.isNullOrEmpty(appId)) {
			throw new RuntimeException("*** Configuration warning: -Darchaius.deployment.applicationId has not been set. ***");
		}

		if (Strings.isNullOrEmpty(env)) {
			env = System.getenv("APP_ENV");
			logger.warn("Configuration warning: -Darchaius.deployment.environment=<local|dev|qa|prod> has not been set.  Trying env variable APP_ENV");

			if (Strings.isNullOrEmpty(env)) {
				throw new RuntimeException("*** Configuration error:   environment should be set by setting either -Darchaius.deployment.environment=<local|dev|qa|prod> or the environment variable APP_ENV. ***");
			}

			System.setProperty("archaius.deployment.environment", env);
			logger.info("Set archaius.deployment.environment system property to [{}]", System.getProperty("archaius.deployment.environment"));
		}

		logger.info("ConfigurationManager.getDeploymentContext().getApplicationId() set to [{}]", ConfigurationManager.getDeploymentContext().getApplicationId());
		logger.info("ConfigurationManager.getDeploymentContext().getDeploymentEnvironment() set to [{}]", ConfigurationManager.getDeploymentContext().getDeploymentEnvironment());
	}

	@Override
	public String getString(String key, String defaultValue) {
		final DynamicStringProperty property = DynamicPropertyFactory.getInstance().getStringProperty(key, defaultValue);
		return property.get();
	}

	@Override
	public int getInt(String key, int defaultValue) {
		final DynamicIntProperty property = DynamicPropertyFactory.getInstance().getIntProperty(key, defaultValue);
		return property.get();
	}

	@Override
	public long getLong(String key, int defaultValue) {
		final DynamicLongProperty property = DynamicPropertyFactory.getInstance().getLongProperty(key, defaultValue);
		return property.get();
	}

	@Override
	public boolean getBoolean(String key, boolean defaultValue) {
		final DynamicBooleanProperty property = DynamicPropertyFactory.getInstance().getBooleanProperty(key, defaultValue);
		return property.get();
	}

	@Override
	@VisibleForTesting
	public void setOverrideProperty(String key, Object value) {
		Preconditions.checkState(initialized, "Must initialize RSSConfiguration before use.");
		((ConcurrentCompositeConfiguration) ConfigurationManager
				.getConfigInstance()).setOverrideProperty(key, value);
	}

	@PostConstruct
	@Override
	public void start() {
		initialize();
	}

	@Override
	public void close() {
	}

	private void initialize() {
		System.setProperty(DynamicPropertyFactory.ENABLE_JMX, "true");

		logger.info(
				"Initializing configuration environment for application [{}] and env [{}]",
				ConfigurationManager.getDeploymentContext().getApplicationId(),
				ConfigurationManager.getDeploymentContext()
						.getDeploymentEnvironment());

		try {
			ConfigurationManager
					.loadCascadedPropertiesFromResources(ConfigurationManager
							.getDeploymentContext().getApplicationId());
		} catch (IOException exc) {
			logger.error(
					"Cannot load cascaded properties for application [{}] and env [{}]",
					ConfigurationManager.getDeploymentContext()
							.getApplicationId(), ConfigurationManager
							.getDeploymentContext().getDeploymentEnvironment());
		}

		logger.info(
				"Initialized configuration environment for application [{}] and env [{}]",
				ConfigurationManager.getDeploymentContext().getApplicationId(),
				ConfigurationManager.getDeploymentContext()
						.getDeploymentEnvironment());

		initialized = true;
	}
}
