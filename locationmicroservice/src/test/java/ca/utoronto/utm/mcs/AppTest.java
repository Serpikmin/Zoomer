package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @BeforeEach
    public void init() throws IOException, InterruptedException {
        // Clear the database
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/location"))
                .DELETE()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void exampleTest() {
        assertTrue(true);
    }

    @Test
    public void nearbyDriverPass() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/location/user"))
                .PUT(HttpRequest.BodyPublishers.ofString("{ uid: \"1\", is_driver: true }"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/location/user"))
                .PUT(HttpRequest.BodyPublishers.ofString("{ uid: \"2\", is_driver: false }"))
                .build();
        client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/location/1"))
                .method("PATCH", (HttpRequest.BodyPublishers.ofString(
                        "{ longitude: 1.23, latitude: 0.41, street: \"Erin Mills\" }")))
                .build();
        client.send(request3, HttpResponse.BodyHandlers.ofString());
        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/location/nearbyDriver/2?radius=5"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request4, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void nearbyDriverFail() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/location/nearbyDriver?radius=5"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void navigationPass() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/location/user"))
                .PUT(HttpRequest.BodyPublishers.ofString("{ uid: \"1\", is_driver: true }"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/location/user"))
                .PUT(HttpRequest.BodyPublishers.ofString("{ uid: \"2\", is_driver: false }"))
                .build();
        client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/location/1"))
                .method("PATCH", (HttpRequest.BodyPublishers.ofString(
                        "{ longitude: 1.23, latitude: 0.41, street: \"Erin Mills\" }")))
                .build();
        client.send(request3, HttpResponse.BodyHandlers.ofString());
        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/location/1"))
                .method("PATCH", (HttpRequest.BodyPublishers.ofString(
                        "{ longitude: 3.19, latitude: 4.85, street: \"Mavis\" }")))
                .build();
        client.send(request4, HttpResponse.BodyHandlers.ofString());
        HttpRequest request5 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/location/hasRoute"))
                .POST(HttpRequest.BodyPublishers.ofString("{ " +
                        "roadName1: \"Erin Mills\", roadName2: \"Mavis\", " +
                        "hasTraffic: false, time: 10 }"))
                .build();
        client.send(request5, HttpResponse.BodyHandlers.ofString());
        HttpRequest request6 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/location/navigation/1?passengerUid=2"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request6, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void navigationFail() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/location/navigation?passengerUid=4"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

}
