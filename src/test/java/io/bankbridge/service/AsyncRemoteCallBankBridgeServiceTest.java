package io.bankbridge.service;

import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.newCapture;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.nio.ByteBuffer.wrap;

import org.easymock.Capture;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.bankbridge.model.BankModel;
import io.bankbridge.response.listners.ContentResponseListner;

@RunWith(EasyMockRunner.class)
public class AsyncRemoteCallBankBridgeServiceTest {

	@Mock
	private HttpClient httpClient;
	@Mock
	private Request request;
	@Mock
	private Response response;
	@Mock
	private Result result;
	
	private Capture<ContentResponseListner> argCapture;
	private AsyncRemoteCallBankBridgeService asyncRemoteCallBankBridgeService;
	private static final String RESPONSE_FROM_API_CALL = "{\n" + 
			"\"bic\":\"1234\",\n" + 
			"\"country_code\":\"GB\",\n" + 
			"\"auth\":\"OAUTH\"\n" + 
			"}";
	
	@Before
	public void setUp() {
		asyncRemoteCallBankBridgeService = new AsyncRemoteCallBankBridgeService(httpClient);
		argCapture = newCapture();
	}
	
	@Test
	public void callBackForRemoteCallsPositiveTest() throws InterruptedException, ExecutionException {
		expect(httpClient.newRequest(anyString())).andReturn(request).times(1);
		request.send(capture(argCapture));
		expectLastCall().times(1);
		
		replay(request);
	    replay(httpClient);
	    
	    CompletableFuture<BankModel> bankFuture = asyncRemoteCallBankBridgeService.callBackForRemoteCalls("BankName", "URL");
	    
	    assertNotNull(argCapture.getValue());
	    assertTrue(argCapture.getValue() instanceof ContentResponseListner);
	    
	    argCapture.getValue().onContent(response, wrap(RESPONSE_FROM_API_CALL.getBytes()));
	    argCapture.getValue().onComplete(result);
	    
	    assertEquals("1234", bankFuture.get().getId());
	    assertEquals("BankName", bankFuture.get().getName());
	    
	    verify(httpClient);
	    verify(request);
	}
	
	@Test(expected = ExecutionException.class)
	public void callBackForRemoteCalls_On_ExceptionTest() throws InterruptedException, ExecutionException {
		expect(httpClient.newRequest(anyString())).andReturn(request).times(1);
		expect(result.getRequest()).andReturn(request);
		expect(request.getPath()).andReturn("/bankName");
		expect(result.getResponse()).andReturn(response).times(2);
		expect(response.getStatus()).andReturn(500);
		expect(response.getReason()).andReturn("Error reason");
		
		request.send(capture(argCapture));
		expectLastCall().times(1);
		
		replay(request);
		replay(response);
		replay(result);
	    replay(httpClient);
	    
	    CompletableFuture<BankModel> bankFuture = asyncRemoteCallBankBridgeService.callBackForRemoteCalls("BankName", "URL");
	    
	    assertNotNull(argCapture.getValue());
	    assertTrue(argCapture.getValue() instanceof ContentResponseListner);
	    
	    argCapture.getValue().onContent(response, wrap("UnParsableString".getBytes()));
	    argCapture.getValue().onComplete(result);
	    
	    verify(httpClient);
	    verify(request);
	    verify(response);
	    verify(result);
	    
	    bankFuture.get();
	}
	
	@Test(expected = ExecutionException.class)
	public void callBackForRemoteCalls_On_FailureTest() throws InterruptedException, ExecutionException {
		expect(httpClient.newRequest(anyString())).andReturn(request).times(1);
		expect(response.getRequest()).andReturn(request);
		expect(request.getPath()).andReturn("/bankName");
		
		request.send(capture(argCapture));
		expectLastCall().times(1);
		
		replay(request);
		replay(response);
	    replay(httpClient);
	    
	    CompletableFuture<BankModel> bankFuture = asyncRemoteCallBankBridgeService.callBackForRemoteCalls("BankName", "URL");
	    
	    assertNotNull(argCapture.getValue());
	    assertTrue(argCapture.getValue() instanceof ContentResponseListner);
	    
	    argCapture.getValue().onFailure(response, new RuntimeException());
	    
	    verify(httpClient);
	    verify(request);
	    verify(response);
	    
	    bankFuture.get();
	}
}
