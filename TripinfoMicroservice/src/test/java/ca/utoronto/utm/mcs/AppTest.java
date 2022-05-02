package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mongodb.util.JSON;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Please write your tests in this class. 
 */
 
public class AppTest {

    private final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @BeforeEach
    public void init() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/location"))
                .DELETE()
                .build();
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/trip"))
                .DELETE()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        client.send(request2, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void exampleTest() {
        assertTrue(true);
    }

    @Test
    public void requestTripFail() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/trip/request"))
                .POST(HttpRequest.BodyPublishers.ofString("{\"uid\": \"4\"}"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void requestTripPass() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/location/user"))
                .POST(HttpRequest.BodyPublishers.ofString("{\"uid\": \"1\", \"is_driver\": false}"))
                .build();
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/location/user"))
                .POST(HttpRequest.BodyPublishers.ofString("{\"uid\": \"2\", \"is_driver\": true}"))
                .build();
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/trip/request"))
                .POST(HttpRequest.BodyPublishers.ofString("{\"uid\": \"1\", \"radius\": 5}"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void confirmTripFail() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/trip/confirm"))
                .POST(HttpRequest.BodyPublishers.ofString("{\"uid\": \"4\"}"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void confirmTripPass() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/trip/confirm"))
                .POST(HttpRequest.BodyPublishers.ofString("{\"driver\": \"3\", " +
                        "\"passenger\": \"2\", \"startTime\": 1645917102}"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void updateTripFail() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/trip"))
                .method("PATCH", (HttpRequest.BodyPublishers.ofString(
                        "{ distance: 3, endTime: 1645919897, timeElapsed: 40, totalCost: \"80.0\"")))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void updateTripPass() throws IOException, InterruptedException, JSONException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/trip/confirm"))
                .POST(HttpRequest.BodyPublishers.ofString("{\"driver\": \"3\", " +
                        "\"passenger\": \"2\", \"startTime\": 1645917102}"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject body = new JSONObject(response.body());
        String id = (String) new JSONObject(new JSONObject(body.get("data")).get("_id")).get("$oid");
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/trip/" + id))
                .method("PATCH", (HttpRequest.BodyPublishers.ofString(
                        "{ distance: 3, endTime: 1645919897, timeElapsed: 40, totalCost: \"80.0\"")))
                .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode());
    }

    @Test
    public void getPassengerTripsFail() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/trip/passenger"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void getPassengerTripsPass() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/trip/confirm"))
                .POST(HttpRequest.BodyPublishers.ofString("{\"driver\": \"3\", " +
                        "\"passenger\": \"2\", \"startTime\": 1645917102}"))
                .build();
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/trip/passenger/2"))
                .GET()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void getDriverTripsFail() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/trip/driver"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void getDriverTripsPass() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/trip/confirm"))
                .POST(HttpRequest.BodyPublishers.ofString("{\"driver\": \"3\", " +
                        "\"passenger\": \"2\", \"startTime\": 1645917102}"))
                .build();
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/trip/driver/3"))
                .GET()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void getDriverTimeFail() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/trip/driverTime"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void getDriverTimePass() throws IOException, InterruptedException, JSONException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/location/user"))
                .PUT(HttpRequest.BodyPublishers.ofString("{\"uid\": \"1\", \"is_driver\": false"))
                .build();
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/location/user"))
                .PUT(HttpRequest.BodyPublishers.ofString("{\"uid\": \"2\", \"is_driver\": true"))
                .build();
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/location/road"))
                .PUT(HttpRequest.BodyPublishers.ofString("{\"roadName\": \"street1\", \"hasTraffic\": false}"))
                .build();
        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/location/road"))
                .PUT(HttpRequest.BodyPublishers.ofString("{\"roadName\": \"street2\", \"hasTraffic\": false}"))
                .build();
        HttpRequest request5 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/location/1"))
                .method("PATCH", HttpRequest.BodyPublishers.ofString("{\"street\": \"street1\"}"))
                .build();
        HttpRequest request6 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/location/2"))
                .method("PATCH", HttpRequest.BodyPublishers.ofString("{\"street\": \"street2\"}"))
                .build();
        HttpRequest request7 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/location/hasRoute"))
                .POST(HttpRequest.BodyPublishers.ofString("{\"roadName1\": \"street1\", \"roadName2\": \"street2\"," +
                        "\"hasTraffic\": false, \"time\": 40}"))
                .build();
        HttpRequest request8 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/trip/confirm"))
                .POST(HttpRequest.BodyPublishers.ofString("{\"driver\": \"2\", " +
                        "\"passenger\": \"1\", \"startTime\": 1645917102}"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        client.send(request2, HttpResponse.BodyHandlers.ofString());
        client.send(request3, HttpResponse.BodyHandlers.ofString());
        client.send(request4, HttpResponse.BodyHandlers.ofString());
        client.send(request5, HttpResponse.BodyHandlers.ofString());
        client.send(request6, HttpResponse.BodyHandlers.ofString());
        client.send(request7, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response = client.send(request8, HttpResponse.BodyHandlers.ofString());
        JSONObject body = new JSONObject(response.body());
        String id = (String) new JSONObject(new JSONObject(body.get("data")).get("_id")).get("$oid");
        HttpRequest request9 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/trip/driverTime/" + id))
                .GET()
                .build();
        HttpResponse<String> response2 = client.send(request9, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode());
    }
}
