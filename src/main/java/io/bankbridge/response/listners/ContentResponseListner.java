package io.bankbridge.response.listners;

import static io.bankbridge.handlers.BankBridgeRequestHandler.Constants.MAPPER;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.BufferingResponseListener;

import io.bankbridge.model.BankModel;
import lombok.extern.slf4j.Slf4j;

/**
 * Content extractor for the remote api calls since all api return data of same
 * type {@link BankModel} a common extractor instance can be used. <br/>
 * A custom implementation of {@link BufferingResponseListener}.
 */
@Slf4j
public class ContentResponseListner extends BufferingResponseListener {

	private CompletableFuture<BankModel> resultDataFuture;
	private String bankName;

	public ContentResponseListner(CompletableFuture<BankModel> resultDataFuture, String bankName) {
		this.resultDataFuture = resultDataFuture;
		this.bankName = bankName;
	}

	@Override
	public void onFailure(org.eclipse.jetty.client.api.Response response, Throwable failure) {
		super.onFailure(response, failure);
		log.error("Error while connecting to path : {} and error : {}", response.getRequest().getPath(),
				failure.getMessage());
		// In case of failure complete exceptionally
		resultDataFuture.completeExceptionally(failure);
	}

	@Override
	public void onComplete(Result result) {
		try {
			BankModel data = MAPPER.readValue(this.getContent(), BankModel.class);
			data.setName(bankName);
			resultDataFuture.complete(data);
		} catch (IOException e) {
			log.error("Error while parsing the response from path : {} with status : {} and reason : {} ",
					result.getRequest().getPath(), result.getResponse().getStatus(), result.getResponse().getReason());
			// In case of exception complete exceptionally
			resultDataFuture.completeExceptionally(e);
		}
	}

}
