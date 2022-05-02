package ca.utoronto.utm.mcs;

import java.io.IOException;
import org.json.*;
import com.sun.net.httpserver.HttpExchange;

public class Navigation extends Endpoint {
    
    /**
     * GET /location/navigation/:driverUid?passengerUid=:passengerUid
     * @param driverUid, passengerUid
     * @return 200, 400, 404, 500
     * Get the shortest path from a driver to passenger weighted by the
     * travel_time attribute on the ROUTE_TO relationship.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        String[] params = r.getRequestURI().toString().split("/");
        if (params.length != 4 || params[3].isEmpty()) {
            this.sendStatus(r, 400);
            return;
    }
        // pull out uid and radius
        String[] uid_passenger = params[3].split("\\?");

        // pull out uid
        if (uid_passenger.length != 2){
            this.sendStatus(r, 400);
            return;
        }







    }}
