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
import org.json.JSONException;
import java.io.IOException;

public class Drivetime extends Endpoint {

    /**
     * GET /trip/driverTime/:_id
     * @param _id
     * @return 200, 400, 404, 500
     * Get time taken to get from driver to passenger on the trip with
     * the given _id. Time should be obtained from navigation endpoint
     * in location microservice.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        // TODO
        // Check if request url isn't malformed
        String[] splitUrl = r.getRequestURI().getPath().split("/");
        if (splitUrl.length != 4) {
            this.sendStatus(r, 400);
            return;
        }

        // check if uid given is integer, return 400 if not
        String id = splitUrl[3];

        // TODO: The functionality
        this.sendStatus(r, 500);
    }
}
