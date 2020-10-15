package io.bankbridge.handler.impl;

import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.eclipse.jetty.client.HttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.bankbridge.handlers.impl.AsyncRemoteCallBankBridgeHandlerImpl;
import io.bankbridge.model.BankModel;
import io.bankbridge.service.AsyncRemoteCallBankBridgeService;
import spark.Request;
import spark.Response;

@RunWith(EasyMockRunner.class)
public class AsyncRemoteCallBankBridgeHandlerImplTest {

	@Mock
	private HttpClient httpClient;
	@Mock
	private Request req;
	@Mock
	private Response res;
	@Mock
	private org.eclipse.jetty.client.api.Request request;
	@Mock
	private org.eclipse.jetty.client.api.Response response;
	@Mock
	private AsyncRemoteCallBankBridgeService asyncRemoteCallBankBridgeService;
	
	private Map<String, String> remoteCallsConfig;
	
	private AsyncRemoteCallBankBridgeHandlerImpl handlerImpl;
	
	@Before
	public void setUp() {
		remoteCallsConfig = getRemoteCallsConfig();
		handlerImpl = new AsyncRemoteCallBankBridgeHandlerImpl(remoteCallsConfig, asyncRemoteCallBankBridgeService);
	}

	@Test
	public void executeIsSussessful_PositiveCase() {
		expect(asyncRemoteCallBankBridgeService.callBackForRemoteCalls(anyString(), anyString()))
		                                       .andReturn(CompletableFuture.completedFuture(getDataModel()));
	    replay(asyncRemoteCallBankBridgeService);
	    List<BankModel> result = handlerImpl.execute(req, res);
	    
	    verify(asyncRemoteCallBankBridgeService);
	    assertTrue(result.size() == 1);
	    assertEquals(getDataModel().getId(), "12345");
	    assertEquals(getDataModel().getName(), "BANK");
	}
	
	@Test(expected = CompletionException.class)
	public void executeFailsWhenOneOfTheCallsFail() {
		CompletableFuture<BankModel> future = new CompletableFuture<>();
		future.completeExceptionally(new RuntimeException());
		
		expect(asyncRemoteCallBankBridgeService.callBackForRemoteCalls(anyString(), anyString()))
		                                       .andReturn(future).times(1);
	    replay(asyncRemoteCallBankBridgeService);
	    handlerImpl.execute(req, res);
	    verify(asyncRemoteCallBankBridgeService);
	}
	
	private BankModel getDataModel() {
		return new BankModel("12345", "BANK");
	}

	private Map<String, String> getRemoteCallsConfig() {
		return Collections.singletonMap("BANK", "http://localhost");
	}
}
