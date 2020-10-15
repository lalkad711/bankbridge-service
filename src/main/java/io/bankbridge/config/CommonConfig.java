package io.bankbridge.config;

import static io.bankbridge.handlers.BankBridgeRequestHandler.Constants.BANKS_CACHE;
import static io.bankbridge.handlers.BankBridgeRequestHandler.Constants.MAPPER;

import java.io.IOException;
import java.util.Map;

import org.eclipse.jetty.client.HttpClient;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import com.fasterxml.jackson.core.type.TypeReference;

import io.bankbridge.model.BankModel;
import io.bankbridge.model.BankModelList;
import io.bankbridge.service.AsyncRemoteCallBankBridgeService;
import lombok.extern.slf4j.Slf4j;

/**
 * This is the configuration class, a one stop place that provides following :
 * <br/>
 * <ul>
 * <li>{@link CacheManager} instance for v1 services
 * <li>{@link HttpClient} instance for v2 - to be used for the remote calls
 * <li>{@link Map} configuration instance for v2 remote calls
 * </ul>
 *
 */
@Slf4j
public class CommonConfig {

	private static CacheManager cacheManager;
	private static Map<String, String> remoteCallsConfig;
	private static HttpClient httpClient;
	private static AsyncRemoteCallBankBridgeService asyncRemoteCallBankBridgeService;

	// Eager initialization of cache and remote calls config on load
	static {
		initializeConfigs();
	}

	private CommonConfig() {
	}

	public static void initializeConfigs() {
		configureHttpClient();
		initializeCacheManager();
		initializeRemoteCallsConfig();
		initializeAsyncRemoteCallBankBridgeService();
	}

	public static HttpClient getHttpClient() {
		return httpClient;
	}

	public static CacheManager getCacheManagerInstance() {
		if (cacheManager == null) {
			initializeCacheManager();
		}
		return cacheManager;
	}

	public static Map<String, String> getRemoteCallsConfigInstance() {
		if (remoteCallsConfig == null) {
			initializeRemoteCallsConfig();
		}
		return remoteCallsConfig;
	}

	public static AsyncRemoteCallBankBridgeService getAsyncRemoteCallBankBridgeService() {
		if (asyncRemoteCallBankBridgeService == null) {
			initializeAsyncRemoteCallBankBridgeService();
		}
		return asyncRemoteCallBankBridgeService;
	}

	/**
	 * Initializes the CacheManager instance of EhCache for v1 of the services.
	 * Creates a cache and stores the data from json files in it.
	 */
	private static void initializeCacheManager() {
		cacheManager = CacheManagerBuilder
				.newCacheManagerBuilder().withCache(BANKS_CACHE, CacheConfigurationBuilder
						.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(10)))
				.build();
		cacheManager.init();
		Cache<String, String> cache = cacheManager.getCache(BANKS_CACHE, String.class, String.class);
		try {
			BankModelList models = MAPPER.readValue(
					Thread.currentThread().getContextClassLoader().getResource("v1/banks-v1.json"),
					BankModelList.class);
			for (BankModel model : models.getBanks()) {
				cache.put(model.getId(), model.getName());
			}
		} catch (IOException ex) {
			log.error("Exception while parsing the banks-v1.json file : {}", ex.getMessage());
			throw new IllegalStateException("Failed to load bank-v1.json. v1 service might not work at all.", ex);
		}
	}

	/**
	 * Initializes the configuration map with the name and url's of the remote calls
	 * in <K, V> pair respectively. Loaded from the json file on the classpath for
	 * v2 of the services.
	 */
	private static void initializeRemoteCallsConfig() {
		try {
			remoteCallsConfig = MAPPER.readValue(
					Thread.currentThread().getContextClassLoader().getResource("v2/banks-v2.json"),
					new TypeReference<Map<String, String>>() {
					});
		} catch (IOException ex) {
			log.error("Exception while parsing the banks-v2.json file : {}", ex.getMessage());
			throw new IllegalStateException("Failed to load bank-v2.json. v2 service might not work at all.", ex);
		}
	}

	/**
	 * Initializes Jetty's Http Client instance and starts it for usage. Similar to
	 * creating a browser session.
	 */
	private static void configureHttpClient() {
		httpClient = new HttpClient();
		httpClient.setMaxConnectionsPerDestination(50); // max 200 concurrent connections to every address
		try {
			httpClient.start();
		} catch (Exception ex) {
			log.error("Exception while starting httpClient : {}", ex.getMessage());
			throw new IllegalStateException("Failed to start Http Client. v2 service might not work at all.", ex);
		}
	}

	/**
	 * Initializes Jetty's Http Client instance and starts it for usage. Similar to
	 * creating a browser session.
	 */
	private static void initializeAsyncRemoteCallBankBridgeService() {
		asyncRemoteCallBankBridgeService = new AsyncRemoteCallBankBridgeService(httpClient);
	}
}
