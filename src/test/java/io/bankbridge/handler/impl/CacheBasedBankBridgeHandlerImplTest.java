package io.bankbridge.handler.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.bankbridge.handlers.impl.CacheBasedBankBridgeHandlerImpl;
import io.bankbridge.model.BankModel;
import spark.Request;
import spark.Response;

@RunWith(EasyMockRunner.class)
public class CacheBasedBankBridgeHandlerImplTest {
	
	private CacheManager cacheManager;
	private static final String BANKS_CACHE = "banks";
	
	@Mock
	private Request req;
	@Mock
	private Response res;
	
	@Before
	public void setUp() {
		initializeCacheManagerForBanks();
	}
	
	@After
	public void tearDown() {
		cacheManager.close();
		cacheManager = null;
	}
	
	@Test
	public void shouldReturnValidResponseWhenGetWithData() {
		Map<String, String> testDataMap = new HashMap<>();
		testDataMap.put("1234", "Bank of ZZZZ");
		testDataMap.put("5678", "Royal Bank of YYYY");
		addTestDataToCache(testDataMap);
		
		CacheBasedBankBridgeHandlerImpl cacheBasedHandler = new CacheBasedBankBridgeHandlerImpl(cacheManager);
		List<BankModel> bankList = cacheBasedHandler.execute(req, res);
		assertEquals(2, bankList.size());
		bankList.stream().forEach(bank -> {
			assertTrue(testDataMap.containsKey(bank.getId()));
			assertTrue(testDataMap.containsValue(bank.getName()));
		});
	}
	
	@Test
	public void shouldReturnEmptyListWhenNoData() {
		CacheBasedBankBridgeHandlerImpl cacheBasedHandler = new CacheBasedBankBridgeHandlerImpl(cacheManager);
		List<BankModel> bankList = cacheBasedHandler.execute(req, res);
		assertEquals(0, bankList.size());
	}
	
	private void initializeCacheManagerForBanks() {
		cacheManager = CacheManagerBuilder
				.newCacheManagerBuilder().withCache(BANKS_CACHE, CacheConfigurationBuilder
						.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(10)))
				.build();
		cacheManager.init();
	}
	
	private void addTestDataToCache(Map<String, String> testDataMap) {
		Cache<String, String> cache = cacheManager.getCache(BANKS_CACHE, String.class, String.class);
		cache.putAll(testDataMap);
	}

}
