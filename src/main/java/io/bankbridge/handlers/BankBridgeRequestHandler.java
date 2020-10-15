package io.bankbridge.handlers;

import java.util.List;

import org.eclipse.jetty.http.HttpParser.RequestHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import io.bankbridge.model.BankModelList;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Interface defining the contract for the Bank Bridge Services making sure that
 * the both v1 and v2 will return same response format.
 * 
 * Implements {@link RequestHandler}.
 * 
 * Making it functional provides and ability to define the implementation using
 * lambdas and provides a check for not adding any additional API to it.
 */
@FunctionalInterface
public interface BankBridgeRequestHandler<T> extends Route {

	/**
	 * Define custom implementation for the Bank Bridge service API. Internally
	 * invoked from the {@link #handle(Request, Response)}
	 * 
	 * @param request  The request object providing information about the HTTP
	 *                 request
	 * @param response The response object providing functionality for modifying the
	 *                 response
	 * @return List of type &lt;T&gt; containing bank details
	 */
	List<T> execute(Request request, Response response);

	/**
	 * Default method providing implementation for
	 * {@link Route#handle(Request, Response)}.
	 */
	default Object handle(Request request, Response response) throws Exception {
		List<T> bankList = execute(request, response);
		return Constants.MAPPER.writeValueAsString(bankList);
	}

	public final class Constants {
		private Constants() {
		}

		/**
		 * Since we are working with response of specific type i.e.
		 * {@link BankModelList} we can have the MAPPER defined here
		 */
		public static final ObjectMapper MAPPER = new ObjectMapper()
				.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
		
		//Cache name for the Bank Details read from json file
		public static final String BANKS_CACHE = "banks";
	}
}
