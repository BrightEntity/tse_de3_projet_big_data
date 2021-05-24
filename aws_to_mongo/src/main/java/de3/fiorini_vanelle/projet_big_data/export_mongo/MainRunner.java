package de3.fiorini_vanelle.projet_big_data.export_mongo;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClientFactory;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.internal.MongoClientImpl;
import de3.fiorini_vanelle.projet_big_data.export_mongo.services.AwsService;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonObjectId;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

@Component
public class MainRunner implements CommandLineRunner {
    @Autowired
    private AwsService awsService;

    @Autowired
    private ClientSession mongoSession;

    @Autowired
    private MongoClient mongoClient;

    @Override
    public void run(String... args) throws Exception {

        List<HashMap<String, String>> predictions = awsService.readAwsCSVFile("projet-big-data", "predicted/predict4.csv");

        MongoCollection<Document> collection = mongoClient.getDatabase("predictionscv").getCollection("cvs");
        for(Map<String, String> nextLine : predictions) {
            mongoSession.startTransaction();
            try {
                Document object = collection.find(new BsonDocument("Id", new BsonInt32(parseInt(nextLine.get("Id"))))).first();
                object.append("Metier_Predit", nextLine.get("Metier_Predit"));
                collection.findOneAndReplace(mongoSession, new BsonDocument("Id", new BsonInt32(object.getInteger("Id"))), object);
                mongoSession.commitTransaction();
            } catch (Exception e) {
                mongoSession.abortTransaction();
                // On cache les erreurs
            }

        }
    }


}
