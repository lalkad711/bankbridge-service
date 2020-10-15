package io.bankbridge.handlers.impl;

import static io.bankbridge.handlers.BankBridgeRequestHandler.Constants.BANKS_CACHE;

import java.util.ArrayList;
import java.util.List;

import org.ehcache.CacheManager;

import io.bankbridge.handlers.BankBridgeRequestHandler;
import io.bankbridge.model.BankModel;
import lombok.extern.slf4j.Slf4j;
import spark.Request;
import spark.Response;
/**
 * Handler class for v1 Bank Bridge Service.
 * Returns response from the cache.
 * 
 * @see {@link BankBridgeRequestHandler}
 * @apiNote v1 Bank Bridge Service
 */
@Slf4j
public class CacheBasedBankBridgeHandlerImpl implements BankBridgeRequestHandler<BankModel> {

	private CacheManager cacheManager;

	public CacheBasedBankBridgeHandlerImpl(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@Override
	public List<BankModel> execute(Request request, Response response) {
		log.info("Servicing request in execute!");
		List<BankModel> result = new ArrayList<>();
		
		cacheManager.getCache(BANKS_CACHE, String.class, String.class).forEach(entry -> {
			BankModel map = new BankModel(entry.getKey(), entry.getValue());
			result.add(map);
		});
		
		return result;
	}

}
