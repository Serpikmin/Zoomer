package ca.utoronto.utm.mcs;

import com.mongodb.client.FindIterable;
import com.sun.net.httpserver.HttpExchange;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Passenger extends Endpoint {

    /**
     * GET /trip/passenger/:uid
     * @param uid
     * @return 200, 400, 404
     * Get all trips the passenger with the given uid has.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException,JSONException{
        // TODO
        // Check if request url isn't malformed
        String[] splitUrl = r.getRequestURI().getPath().split("/");
        if (splitUrl.length != 4) {
            this.sendStatus(r, 400);
            return;
        }

        // check if uid given is integer, return 400 if not
        String pidString = splitUrl[3];
        int pid;
        try {
            pid = Integer.parseInt(pidString);
        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 400);
            return;
        }

        try {
            FindIterable<Document> cursor = this.dao.getPassengerTrips(pid);
            if (cursor != null) {
                JSONObject var = new JSONObject();
                var.put("data", Utils.findIterableToJSONArray(cursor));
                this.sendResponse(r, var, 200);
                return;
            }
            this.sendStatus(r, 404);
        } catch (Exception e) {
            this.sendStatus(r, 500);
        }
    }
}
