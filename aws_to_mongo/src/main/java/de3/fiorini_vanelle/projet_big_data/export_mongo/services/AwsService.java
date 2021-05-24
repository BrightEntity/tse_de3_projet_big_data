package de3.fiorini_vanelle.projet_big_data.export_mongo.services;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AwsService {
    private S3Client amazonS3;

    @Autowired
    public AwsService(S3Client amazonS3) {
        this.amazonS3 = amazonS3;
    }
    public List<HashMap<String, String>> readAwsCSVFile(String bucketName, String key) throws IOException, CsvException {
        S3Object object;
        String pdfFileInText = "";
        // AmazonS3 s3client = getAmazonS3ClientObject();
        InputStream objectData = amazonS3.getObject(builder -> builder.bucket(bucketName).key(key));
        Reader fr = new BufferedReader(new InputStreamReader(objectData));

        CSVReader document = new CSVReader(fr);

        List<String[]> csv_raw = document.readAll();

        String[] headers = csv_raw.get(0);
        List<HashMap<String, String>> output = new ArrayList<HashMap<String, String>>();
        for (int i = 1; i < csv_raw.size(); i++) {
            HashMap<String, String> row = new HashMap<String, String>();
            for (int j = 0; j < headers.length; j++) {
                row.put(headers[j].replaceAll("[^\\x00-\\x7F]", ""), csv_raw.get(i)[j]);
            }
            output.add(row);
        }

        return output;
    }
}
