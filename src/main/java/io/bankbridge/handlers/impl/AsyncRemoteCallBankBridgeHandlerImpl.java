package io.bankbridge.handlers.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import io.bankbridge.handlers.BankBridgeRequestHandler;
import io.bankbridge.model.BankModel;
import io.bankbridge.service.AsyncRemoteCallBankBridgeService;
import lombok.extern.slf4j.Slf4j;
import spark.Request;
import spark.Response;

/**
 * Handler class for v2 BankBridge services that asynchronously make multiple
 * remote calls based on the passed configuration. Combines the result from of
 * the http calls and return an aggregate response.
 * 
 * @see {@link BankBridgeRequestHandler}
 * @apiNote v2 Bank Bridge Service
 *
 */
@Slf4j
public class AsyncRemoteCallBankBridgeHandlerImpl implements BankBridgeRequestHandler<BankModel> {

	private Map<String, String> remoteCallsConfig;
	private AsyncRemoteCallBankBridgeService asyncRemoteCallService;

	public AsyncRemoteCallBankBridgeHandlerImpl(Map<String, String> remoteCallsConfig,
			AsyncRemoteCallBankBridgeService remoteCallService) {
		this.remoteCallsConfig = remoteCallsConfig;
		this.asyncRemoteCallService = remoteCallService;
	}

	@Override
	public List<BankModel> execute(Request request, Response response) {
		log.info("Servicing request in execute!");

		// Asynchronously make all the remote calls
		List<CompletableFuture<BankModel>> result = remoteCallsConfig.entrySet().stream()
				.map(config -> asyncRemoteCallService.callBackForRemoteCalls(config.getKey(), config.getValue()))
				.collect(Collectors.toList());

		// Wait till all calls are completed - Will throw an exception if any of the
		// futures complete exceptionally
		CompletableFuture.allOf(result.toArray(new CompletableFuture<?>[result.size()])).join();

		List<BankModel> bankList = new ArrayList<>();

		/*
		 * Using future.join() as the remote call execution has been completed at this
		 * point and join will throw unchecked exception which is again unlikely if the
		 * execution has already reached at this point. Using future.get() would require
		 * handling of unchecked ExecutionException
		 */
		result.stream().forEach(future -> bankList.add(future.join()));
		return bankList;
	}

}
