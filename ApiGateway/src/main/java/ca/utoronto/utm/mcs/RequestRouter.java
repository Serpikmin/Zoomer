package ca.utoronto.utm.mcs;

import java.io.IOException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

/** 
 * Everything you need in order to send and recieve httprequests to 
 * the microservices is given here. Do not use anything else to send 
 * and/or recieve http requests from other microservices. Any other 
 * imports are fine.
 */
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.io.OutputStream;    // Also given to you to send back your response
import java.time.Duration;
import java.util.HashMap;
import java.util.Objects;

public class RequestRouter implements HttpHandler {
	
    /**
     * You may add and/or initialize attributes here if you 
     * need.
     */
	public HashMap<Integer, String> errorMap;
	public HashMap<String, String> hostMap;
	public HttpClient client;

	public RequestRouter() {
		client = HttpClient.newBuilder()
				.version(HttpClient.Version.HTTP_1_1)
				.connectTimeout(Duration.ofSeconds(5))
				.build();
		errorMap = new HashMap<>();
		errorMap.put(200, "OK");
		errorMap.put(400, "BAD REQUEST");
		errorMap.put(401, "UNAUTHORIZED");
		errorMap.put(404, "NOT FOUND");
		errorMap.put(405, "METHOD NOT ALLOWED");
		errorMap.put(409, "CONFLICT");
		errorMap.put(500, "INTERNAL SERVER ERROR");
		hostMap = new HashMap<>();
		hostMap.put("location", "locationmicroservice");
		hostMap.put("user", "usermicroservice");
		hostMap.put("trip", "tripinfomicroservice");
	}

	@Override
	public void handle(HttpExchange r) throws IOException {
		r.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); // For CORS
		try {
			switch (r.getRequestMethod()) {
				case "OPTIONS" -> this.handleCors(r);
				case "GET" -> this.handleGet(r);
				case "PATCH" -> this.handlePatch(r);
				case "POST" -> this.handlePost(r);
				case "PUT" -> this.handlePut(r);
				case "DELETE" -> this.handleDelete(r);
				default -> this.sendStatus(r, 405);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeOutputStream(HttpExchange r, String response) throws IOException {
		OutputStream os = r.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

	public void sendResponse(HttpExchange r, JSONObject obj, int statusCode) throws JSONException, IOException {
		obj.put("status", errorMap.get(statusCode));
		String response = obj.toString();
		r.sendResponseHeaders(statusCode, response.length());
		this.writeOutputStream(r, response);
	}

	public void sendStatus(HttpExchange r, int statusCode) throws JSONException, IOException {
		JSONObject res = new JSONObject();
		res.put("status", errorMap.get(statusCode));
		String response = res.toString();
		r.sendResponseHeaders(statusCode, response.length());
		this.writeOutputStream(r, response);
	}

	public void handleCors(HttpExchange r) throws IOException {
		r.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
		r.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
		r.sendResponseHeaders(204, -1);
		return;
	}

	public void handleGet(HttpExchange r) throws IOException, JSONException, InterruptedException {
		// Check if request url isn't malformed
		String[] splitUrl = r.getRequestURI().getPath().split("/");
		if (splitUrl.length < 3)
		{
			this.sendStatus(r, 400);
			return;
		}

		String service = splitUrl[1];
		String endpoint1 = splitUrl[2];
		String endpoint2 = "";
		// Check that HTTP Method is valid for microservice
		if (splitUrl.length == 4)
		{
			endpoint2 = splitUrl[3];
		}
		else if (splitUrl.length == 5) {
			endpoint2 = splitUrl[3] + "/" + splitUrl[4];
		}

		// Send request to microservice
		System.out.println("http://" + hostMap.get(service) + ":8000/" + service + "/" + endpoint1 + "/"+ endpoint2);
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://" + hostMap.get(service) + ":8000/" + service + "/" + endpoint1 + "/"+ endpoint2))
				.GET()
				.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		// Retrieve the response
		JSONObject body = new JSONObject(response.body());
		this.sendResponse(r, body, response.statusCode());

	};

	public void handlePatch(HttpExchange r) throws IOException, JSONException, InterruptedException {
		// Check if request url isn't malformed
		String[] splitUrl = r.getRequestURI().getPath().split("/");
		if (splitUrl.length != 3)
		{
			this.sendStatus(r, 400);
			return;
		}
		String service = splitUrl[1];
		String endpoint = splitUrl[2];
		// Check that HTTP Method is valid for microservice
		if (!Objects.equals(service, "location") &&
				!Objects.equals(service, "user") &&
				!Objects.equals(service, "trip"))
		{
			this.sendStatus(r, 400);
			return;
		}

		// Send request to microservice
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://" + hostMap.get(service) + ":8000/" + service + "/" + endpoint))
				.method("PATCH", HttpRequest.BodyPublishers.ofString(Utils.convert(r.getRequestBody())))
				.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		// Retrieve the response
		JSONObject body = new JSONObject(response.body());
		this.sendResponse(r, body, response.statusCode());
	};

	public void handlePost(HttpExchange r) throws IOException, JSONException, InterruptedException {
		// Check if request url isn't malformed
		String[] splitUrl = r.getRequestURI().getPath().split("/");
		if (splitUrl.length != 3)
		{
			this.sendStatus(r, 400);
			return;
		}
		String service = splitUrl[1];
		String endpoint = splitUrl[2];
		// Check that HTTP Method is valid for microservice
		if (!Objects.equals(service, "location") &&
				!Objects.equals(service, "user") &&
				!Objects.equals(service, "trip"))
		{
			this.sendStatus(r, 400);
			return;
		}

		// Send request to microservice
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://" + hostMap.get(service) + ":8000/" + service + "/" + endpoint))
				.POST(HttpRequest.BodyPublishers.ofString(Utils.convert(r.getRequestBody())))
				.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		// Retrieve the response
		JSONObject body = new JSONObject(response.body());
		this.sendResponse(r, body, response.statusCode());
	};

	public void handlePut(HttpExchange r) throws IOException, JSONException, InterruptedException {
		// Check if request url isn't malformed
		String[] splitUrl = r.getRequestURI().getPath().split("/");
		if (splitUrl.length != 3)
		{
			this.sendStatus(r, 400);
			return;
		}
		String service = splitUrl[1];
		String endpoint = splitUrl[2];
		// Check that HTTP Method is valid for microservice
		if (!Objects.equals(service, "location"))
		{
			this.sendStatus(r, 400);
			return;
		}

		// Send request to microservice
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://" + hostMap.get(service) + ":8000/" + service + "/" + endpoint))
				.PUT(HttpRequest.BodyPublishers.ofString(Utils.convert(r.getRequestBody())))
				.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		// Retrieve the response
		JSONObject body = new JSONObject(response.body());
		this.sendResponse(r, body, response.statusCode());
	};

	public void handleDelete(HttpExchange r) throws IOException, JSONException, InterruptedException {
		// TODO Handle delete with body
		// Check if request url isn't malformed
		String[] splitUrl = r.getRequestURI().getPath().split("/");
		if (splitUrl.length < 2)
		{
			this.sendStatus(r, 400);
			return;
		}
		String service = splitUrl[1];
		String endpoint;
		// Check that HTTP Method is valid for microservice
		if (!Objects.equals(service, "location") &&
				!Objects.equals(service, "user") &&
				!Objects.equals(service, "trip"))
		{
			this.sendStatus(r, 400);
			return;
		}

		if (splitUrl.length < 3)
		{
			endpoint = "";
		}
		else
		{
			endpoint = splitUrl[2];
		}

		// Send request to Microservice
		System.out.println("URI: http://" + hostMap.get(service) + ":8000/" + service + "/" + endpoint);
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://" + hostMap.get(service) + ":8000/" + service + "/" + endpoint))
				.method("DELETE", HttpRequest.BodyPublishers.ofString(Utils.convert(r.getRequestBody())))
				.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		// Retrieve the response
		JSONObject body = new JSONObject(response.body());
		this.sendResponse(r, body, response.statusCode());
	};
}
