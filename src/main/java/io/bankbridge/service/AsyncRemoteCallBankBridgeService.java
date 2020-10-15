package io.bankbridge.service;

import java.util.concurrent.CompletableFuture;

import org.eclipse.jetty.client.HttpClient;

import io.bankbridge.model.BankModel;
import io.bankbridge.response.listners.ContentResponseListner;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for making remote calls and processing of response. 
 *
 */
@Slf4j
public class AsyncRemoteCallBankBridgeService {
	
	private HttpClient httpClient;
	
	public AsyncRemoteCallBankBridgeService(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	/**
	 * Executes the remote api call asynchronously using Jetty's HttpClient
	 * 
	 * @param bankName Name of the bank whose api will be called.
	 * @param bankUrl  Api url.
	 * @return {@link CompletableFuture&lt;BankModel&gt;} Future object.
	 */
	public CompletableFuture<BankModel> callBackForRemoteCalls(String bankName, String bankUrl) {
		log.info("Remote call for bank : {}", bankName);
		CompletableFuture<BankModel> resultData = new CompletableFuture<>();
		httpClient.newRequest(bankUrl).send(new ContentResponseListner(resultData, bankName));
		return resultData;
	}
}
