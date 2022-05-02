package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.sql.*;

public class Register extends Endpoint {

    /**
     * POST /user/register
     * @body name, email, password
     * @return 200, 400, 500
     * Register a user into the system using the given information.
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
        if (!deserialized.has("email") || !deserialized.has("name") || !deserialized.has("password")) {
            this.sendStatus(r, 400);
            return;
        }

        if (deserialized.get("email").getClass() != String.class) {
            this.sendStatus(r, 400);
            return;
        }

        if (deserialized.get("name").getClass() != String.class) {
            this.sendStatus(r, 400);
            return;
        }

        if (deserialized.get("password").getClass() != String.class) {
            this.sendStatus(r, 400);
            return;
        }

        String email = deserialized.getString("email");
        String name = deserialized.getString("name");
        String password = deserialized.getString("password");

        // Make query to check if user with given email exists, return 500 if error
        ResultSet rs1;
        boolean resultHasNext;
        try {
            rs1 = this.dao.getUsersFromEmail(email);
            resultHasNext = rs1.next();
        }
        catch (SQLException e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
            return;
        }

        // Check if user with given email exists, return 409 if so
        if (resultHasNext) {
            this.sendStatus(r, 409);
            return;
        }

        // Update db, return 500 if error
        try {
            this.dao.addUser(email, name, password);
        }
        catch (SQLException e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
            return;
        }

        // Make query to return uid of new user, return 500 if error
        ResultSet rs;

        try {
            rs = this.dao.getUsersFromEmail(email);
            resultHasNext = rs.next();
        }
        catch (SQLException e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
            return;
        }

        // Check if user was found, return 404 if not found (This shouldn't trigger since we just added the user)
        if (!resultHasNext) {
            this.sendStatus(r, 404);
            return;
        }

        int uid;

        try {
            uid = rs.getInt("uid");
        }
        catch (SQLException e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
            return;
        }

        JSONObject resp = new JSONObject();
        resp.put("uid", uid);

        this.sendResponse(r, resp, 200);
    }
}
