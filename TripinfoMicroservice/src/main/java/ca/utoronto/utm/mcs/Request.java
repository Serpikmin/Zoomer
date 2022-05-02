package ca.utoronto.utm.mcs;

/** 
 * Everything you need in order to send and recieve httprequests to 
 * other microservices is given here. Do not use anything else to send 
 * and/or recieve http requests from other microservices. Any other 
 * imports are fine.
 */
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;

public class Request extends Endpoint {

    /**
     * POST /trip/request
     * @body uid, radius
     * @return 200, 400, 404, 500
     * Returns a list of drivers within the specified radius 
     * using location microservice. List should be obtained
     * from navigation endpoint in location microservice
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException,JSONException{
        String body = Utils.convert(r.getRequestBody());
        JSONObject deserialized = new JSONObject(body);

        // Check if request url isn't malformed
        String[] splitUrl = r.getRequestURI().getPath().split("/");
        if (splitUrl.length != 3) {
            this.sendStatus(r, 400);
            return;
        }

        // Check that required params are present and of correct type
        if (!deserialized.has("uid") || !deserialized.has("radius")) {
            this.sendStatus(r, 400);
            return;
        }

        if (deserialized.get("uid").getClass() != String.class) {
            this.sendStatus(r, 400);
            return;
        }

        if (deserialized.get("radius").getClass() != int.class) {
            this.sendStatus(r, 400);
            return;
        }

        String uid = deserialized.getString("uid");
        int radius = deserialized.getInt("radius");
        if (radius < 0) {
            this.sendStatus(r, 400);
            return;
        }

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://locationmicroservice:8000/location/nearbyDriver/" + uid + "?radius=" + radius))
                .GET()
                .build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            this.sendStatus(r, 500);
            return;
        }
        if (response.statusCode() != 200) {
            this.sendStatus(r, response.statusCode());
            return;
        }

        JSONObject data = new JSONObject(new JSONObject(response.body()).get("data"));
        Iterator<String> keys = data.keys();
        ArrayList<String> ids = new ArrayList<>();

        while(keys.hasNext()) {
            String key = keys.next();
            ids.add(key);
        }
        JSONArray array = new JSONArray();
        for (String id : ids) {
            array.put(id);
        }

        JSONObject newdata = new JSONObject(array);
        this.sendResponse(r, newdata, 200);
    }
}
