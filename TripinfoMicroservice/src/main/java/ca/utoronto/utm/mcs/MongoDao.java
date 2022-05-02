package ca.utoronto.utm.mcs;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.client.*;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.conversions.Bson;

import javax.print.Doc;

public class MongoDao {
	
	public MongoCollection<Document> collection;

	private String uriDb;

	public MongoDao() {
        // Connect to the mongodb database and create the database and collection. 
        // Use Dotenv like in the DAOs of the other microservices.
		Dotenv dotenv = Dotenv.load();
		String addr = dotenv.get("MONGODB_ADDR");
		String username = "root";
		String password = "123456";
		uriDb = String.format("mongodb://%s:%s@%s:27017", username, password, addr);


		MongoClient mongoClient = MongoClients.create(uriDb);
		String dbName = "trips";
		MongoDatabase database = mongoClient.getDatabase(dbName);
        this.collection = database.getCollection(dbName);
	}

	// *** implement database operations here *** //

	public FindIterable<Document> getTripFromId(String id) {
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("_id", id);
		return collection.find(searchQuery);
	}

	public FindIterable<Document> getPassengerTrips(int pid) {
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("passenger", pid);
		return collection.find(searchQuery);
	}

	public FindIterable<Document> getDriverTrips(int did) {
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("driver", did);
		return collection.find(searchQuery);
	}

	public FindIterable<Document> postTrip(String driver, String passenger, int startTime) {
		Document doc = new Document();
		doc.put("driver", driver);
		doc.put("passenger", passenger);
		doc.put("startTime", startTime);

		this.collection.insertOne(doc);

		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("driver", driver);
		searchQuery.put("passenger", passenger);
		searchQuery.put("startTime", startTime);

		return collection.find(searchQuery);
	}

	public int patchTrip(String tripid, int distance, int endTime, int timeElapsed, String totalCost) {
		FindIterable<Document> trip = this.getTripFromId(tripid);
		if (trip.first() == null)
		{
			return 0;  // Not found, return 404
		}
		Bson updates = Updates.combine(
				Updates.set("distance", distance),
				Updates.set("endTime", endTime),
				Updates.set("timeElapsed", timeElapsed),
				Updates.set("totalCost", totalCost));
		UpdateOptions options = new UpdateOptions().upsert(true);
		try {
			collection.updateOne(trip.first(), updates, options);
		} catch (Exception e) {
			return -2;  // Mongo error, return 500
		}
		return 1;  // Success, return 200
	}

	public int deleteAll() {
		try {
			collection.deleteMany(new Document());
			return 1;
		} catch (Exception e) {
			return -1;
		}
	}
}
