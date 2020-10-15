package io.bankbridge;

import static io.bankbridge.config.CommonConfig.getAsyncRemoteCallBankBridgeService;
import static io.bankbridge.config.CommonConfig.getCacheManagerInstance;
import static io.bankbridge.config.CommonConfig.getRemoteCallsConfigInstance;
import static spark.Spark.awaitInitialization;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.internalServerError;
import static spark.Spark.port;
import static spark.Spark.threadPool;

import java.util.concurrent.CompletionException;

import io.bankbridge.handlers.impl.AsyncRemoteCallBankBridgeHandlerImpl;
import io.bankbridge.handlers.impl.CacheBasedBankBridgeHandlerImpl;

/**
 * Main application class serving as the entry/start point for Bank Bridge
 * Service Services are being exposed over port no. 8080
 *
 */
public class BankBridgeServiceApp {

	public static void main(String[] args) {

		port(8080);
		threadPool(8, 2, 30000);

		// v1 Bank Bride services
		get("/v1/banks/all", new CacheBasedBankBridgeHandlerImpl(getCacheManagerInstance()));

		// v2 Bank Bride services
		get("/v2/banks/all", new AsyncRemoteCallBankBridgeHandlerImpl(getRemoteCallsConfigInstance(),
				getAsyncRemoteCallBankBridgeService()));

		// Using Route
		internalServerError((req, res) -> {
			res.type("application/json");
			return "{\"error_message\":\"Internal Server Error. Please try again later.\"}";
		});

		exception(CompletionException.class, (exception, request, response) -> {
			response.type("application/json");
			response.status(500);
			response.body("{\"error_message\":\"Something went wrong. Please try again later.\"}");
		});

		awaitInitialization(); // Wait for server to be initialized
		
	}

}
