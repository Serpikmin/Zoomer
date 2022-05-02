package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Trip extends Endpoint {

    /**
     * PATCH /trip/:_id
     * @param _id
     * @body distance, endTime, timeElapsed, totalCost
     * @return 200, 400, 404
     * Adds extra information to the trip with the given id when the 
     * trip is done. 
     */

    @Override
    public void handlePatch(HttpExchange r) throws IOException, JSONException {
        String body = Utils.convert(r.getRequestBody());
        JSONObject deserialized = new JSONObject(body);

        // Check if request url isn't malformed
        String[] splitUrl = r.getRequestURI().getPath().split("/");
        if (splitUrl.length != 3) {
            this.sendStatus(r, 400);
            return;
        }

        // Check that required params are present and of correct type
        if (!deserialized.has("distance") || !deserialized.has("endTime")
                || !deserialized.has("timeElapsed") || !deserialized.has("totalCost")) {
            this.sendStatus(r, 400);
            return;
        }

        if (deserialized.get("distance").getClass() != int.class) {
            this.sendStatus(r, 400);
            return;
        }

        if (deserialized.get("endTime").getClass() != int.class) {
            this.sendStatus(r, 400);
            return;
        }

        if (deserialized.get("timeElapsed").getClass() != int.class) {
            this.sendStatus(r, 400);
            return;
        }

        if (deserialized.get("totalCost").getClass() != String.class) {
            this.sendStatus(r, 400);
            return;
        }

        String id = splitUrl[2];
        int distance = deserialized.getInt("distance");
        int endTime = deserialized.getInt("endTime");
        int timeElapsed = deserialized.getInt("timeElapsed");
        String totalCost = deserialized.getString("totalCost");

        int result = this.dao.patchTrip(id, distance, endTime, timeElapsed, totalCost);
        if (result == 0) {
            this.sendStatus(r, 404);
        }
        else if (result == -2) {
            this.sendStatus(r, 500);
        }
        this.sendStatus(r, 200);
    }

    @Override
    public void handleDelete(HttpExchange r) throws IOException, JSONException {
        // Check if request url isn't malformed
        String[] splitUrl = r.getRequestURI().getPath().split("/");
        if (splitUrl.length != 2) {
            this.sendStatus(r, 400);
            return;
        }

        if (this.dao.deleteAll() == 1) {
            this.sendStatus(r, 200);
        }
        else {
            this.sendStatus(r, 500);
        }
    }
}
