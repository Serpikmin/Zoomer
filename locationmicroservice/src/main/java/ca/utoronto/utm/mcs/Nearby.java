package ca.utoronto.utm.mcs;

import java.io.IOException;
import org.json.*;
import com.sun.net.httpserver.HttpExchange;
import java.util.List;

import org.json.*;
import com.sun.net.httpserver.HttpExchange;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

public class Nearby extends Endpoint {
    
    /**
     * GET /location/nearbyDriver/:uid?radius=:radius
     * @param uid, radius
     * @return 200, 400, 404, 500
     * Get drivers that are within a certain radius around a user.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {

        //check if we have correct inputs
        // if we don't have the right amount of inputs

        String[] params = r.getRequestURI().toString().split("/");
        if (params.length != 4 || params[3].isEmpty()) {
            this.sendStatus(r, 400);
            return;
        }
        // pull out uid and radius
        String[] rad_uid = params[3].split("\\?");

        // radius is missing
        if (rad_uid.length != 2){
            this.sendStatus(r, 400);
            return;
        }

        try{
            // get the uid
            String uid = rad_uid[0];

            // get the radius
            double radius = Double.parseDouble(rad_uid[1].substring(7));

            // send both of them into dao function to retrieve relevant longitude and latitude value
            Result result = this.dao.getUserLocationByUid(uid);

            // aim to retrieve longitude and latitude value of the user
            double longitude;
            double latitude;
            if (result.hasNext()) {
                Record user =  result.next();
                longitude = user.get("n.longitude").asDouble();
                latitude = user.get("n.latitude").asDouble();
            } else {
                this.sendStatus(r, 404);
                return;
            }

            // send longitude, latitude and radius
            Result result2 = this.dao.getNearbyDrivers(longitude, latitude, radius);
//            System.out.println(result2.list().toString());
//            System.out.println("I am def a list");
//            System.out.println(result2.list().size());
            // throw an exception to check

            List<Record> records = result2.list();
//            System.out.println(records.toString());
            Record curr;
            double longitude2;
            double latitude2;
            String street;
            String uid2;
//
//            // main thing to input query into
            JSONObject res = new JSONObject();
//
//            // populate res with status

//            System.out.println("status put");
//
//            // for all data
            JSONObject data = new JSONObject();
//
//            // for individual values
            JSONObject res2;
//
            for(int i = 0; i < records.size(); i++) {
                res2 = new JSONObject();
                curr = records.get(i);

                longitude2 = curr.get("n.longitude").asDouble();
                latitude2 = curr.get("n.latitude").asDouble();
                street = curr.get("n.street").asString();
                uid2 = curr.get("n.uid").asString();

                // keep populating data
                res2.put("longitude", longitude2);
                res2.put("latitude", latitude2);
                res2.put("street", street);
                data.put(uid2, res2);

            }
            res.put("data", data);

            this.sendResponse(r, res, 200);
            res.put("status", "OK");
        }

        catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
        // figure out the queries

    }
}



