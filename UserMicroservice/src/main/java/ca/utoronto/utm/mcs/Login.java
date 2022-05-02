package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Login extends Endpoint {

    /**
     * POST /user/login
     * @body email, password
     * @return 200, 400, 401, 404, 500
     * Login a user into the system if the given information matches the 
     * information of the user in the database.
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
        if (!deserialized.has("email") || !deserialized.has("password")) {
            this.sendStatus(r, 400);
            return;
        }

        if (deserialized.get("email").getClass() != String.class) {
            this.sendStatus(r, 400);
            return;
        }

        if (deserialized.get("password").getClass() != String.class) {
            this.sendStatus(r, 400);
            return;
        }

        String email = deserialized.getString("email");
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

        // Check if user with given email exists, return 404 if not
        if (!resultHasNext) {
            this.sendStatus(r, 404);
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

        // Check if the provided password matches the stored user's password, return 401 otherwise
        try {
            if (!Objects.equals(password, rs.getString("password"))) {
                this.sendStatus(r, 401);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
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
