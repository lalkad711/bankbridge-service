package io.bankbridge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.api.ContentResponse;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.bankbridge.config.CommonConfig;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class BankBridgeServiceStepDef {

	private static final String GET_ALL_BANKS_v1 = "http://localhost:8080/v1/banks/all";
	private static final String GET_ALL_BANKS_v2 = "http://localhost:8080/v2/banks/all";
	private ContentResponse responseV1;
	private ContentResponse responseV2;

	@Given("The client does a get on v1 bank bridge service")
	public void getV1All() throws InterruptedException, ExecutionException, TimeoutException {
		responseV1 = CommonConfig.getHttpClient().GET(GET_ALL_BANKS_v1);
	}

	@Then("The client gets back status code of {int} from v1")
	public void checkStatusCodeV1(int statusCode) {
		assertEquals(statusCode, responseV1.getStatus());
	}

	@And("Json reponse for v1 contains following bank names:$")
	public void checkResponseV1(DataTable table) throws JsonParseException, JsonMappingException, IOException {
		List<Map<String, String>> banks = table.asMaps(String.class, String.class);
		List<Map<String, String>> responseBanks = new ObjectMapper().readValue(responseV1.getContentAsString(),
				new TypeReference<List<Map<String, String>>>() {
				});
		assertReponseResults(banks, responseBanks);
	}

	@Given("The client does a get on v2 bank bridge service")
	public void getV2All() throws InterruptedException, ExecutionException, TimeoutException {
		responseV2 = CommonConfig.getHttpClient().GET(GET_ALL_BANKS_v2);
	}

	@Then("The client gets back status code of {int} from v2")
	public void checkStatusCodeV2(int statusCode) {
		assertEquals(statusCode, responseV2.getStatus());
	}

	@And("Json reponse for v2 contains following bank names:$")
	public void checkResponseV2(DataTable table) throws JsonParseException, JsonMappingException, IOException {
		List<Map<String, String>> banks = table.asMaps(String.class, String.class);
		List<Map<String, String>> responseBanks = new ObjectMapper().readValue(responseV2.getContentAsString(),
				new TypeReference<List<Map<String, String>>>() {
				});
		assertReponseResults(banks, responseBanks);
	}

	private void assertReponseResults(List<Map<String, String>> expected, List<Map<String, String>> actual) {
		assertEquals(expected.size(), actual.size());
		expected.forEach(map -> {
			assertTrue(actual.stream().anyMatch(actMap -> {
				if(actMap.get("id") == null && map.get("id").equals("null")) // "null" since cucumber sets empty string as "null"
					return true;
				else if(actMap.get("id") == null || map.get("id").equals("null"))
					return false;
				return actMap.get("id").equals(map.get("id")) && actMap.get("name").equals(map.get("name"));
			}));
		});
	}
}
