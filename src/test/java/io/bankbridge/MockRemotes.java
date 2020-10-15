package io.bankbridge;

import static spark.Spark.get;
import static spark.Spark.port;

public class MockRemotes {
	
	public static void main(String[] args) {
		
		port(1234);

		get("/rbb", (request, response) -> "{\n" + 
				"\"bic\":\"1234\",\n" + 
				"\"country_code\":\"GB\",\n" + 
				"\"auth\":\"OAUTH\"\n" + 
				"}");
		get("/cs", (request, response) -> "{\n" + 
				"\"bic\":\"5678\",\n" + 
				"\"country_code\":\"CH\",\n" + 
				"\"auth\":\"OpenID\"\n" + 
				"}");
		get("/bes", (request, response) -> "{\n" + 
				"\"name\":\"Banco de espiritu santo\",\n" + 
				"\"country_code\":\"PT\",\n" + 
				"\"auth\":\"SSL\"\n" + 
				"}");
	}

}
