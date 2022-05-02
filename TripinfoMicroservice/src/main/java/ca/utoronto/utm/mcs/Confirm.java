package ca.utoronto.utm.mcs;

import com.mongodb.client.FindIterable;
import com.mongodb.util.JSON;
import com.sun.net.httpserver.HttpExchange;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Confirm extends Endpoint {

    /**
     * POST /trip/confirm
     * @body driver, passenger, startTime
     * @return 200, 400
     * Adds trip info into the database after trip has been requested.
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        String body = Utils.convert(r.getRequestBody());
        JSONObject deserialized = new JSONObject(body);

        // Check if request url isn't malformed
        String[] splitUrl = r.getRequestURI().getPath().split("/");
        if (splitUrl.length != 3) {
            this.sendStatus(r, 400);
            return;
        }

        // Check that required params are present and of correct type
        if (!deserialized.has("driver") || !deserialized.has("passenger") || !deserialized.has("startTime")) {
            this.sendStatus(r, 400);
            return;
        }

        if (deserialized.get("driver").getClass() != String.class) {
            this.sendStatus(r, 400);
            return;
        }

        if (deserialized.get("passenger").getClass() != String.class) {
            this.sendStatus(r, 400);
            return;
        }

        if (deserialized.get("startTime").getClass() != int.class) {
            this.sendStatus(r, 400);
            return;
        }

        String driver = deserialized.getString("driver");
        String passenger = deserialized.getString("passenger");
        int startTime = deserialized.getInt("startTime");
        if (startTime < 0) {
            this.sendStatus(r, 400);
            return;
        }

        FindIterable<Document> documents = this.dao.postTrip(driver, passenger, startTime);
        JSONObject id = new JSONObject();
        id.put("$oid", documents.first());
        JSONObject data = new JSONObject();
        data.put("data", id);

        this.sendResponse(r, data, 200);
    }
}
