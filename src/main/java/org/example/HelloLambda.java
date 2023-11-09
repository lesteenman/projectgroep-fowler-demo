package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

public class HelloLambda implements RequestHandler<Map<String, Object>, Map<String, String>> {

    private final S3Client s3Client = S3Client.builder().build();

    public String handleRequest(String name){
        return "Hello, "+name+"!";
    }

    @Override
    public Map<String, String> handleRequest(Map<String, Object> input, Context context) {
        System.out.println("input: " + input);
        System.out.println("context: " + context);
        System.out.println("bucket name: " + System.getenv("BucketName"));

        List<String> bucketObjects = listBucketObjects(s3Client, System.getenv("BucketName"));
        final String objectsList = bucketObjects.stream()
            .collect(Collectors.joining(", "));

        return Map.of(
            "message", "Hello, " + input.getOrDefault("name", "world"),
            "bucket contents", objectsList
        );
    }

    public static List<String> listBucketObjects(S3Client s3, String bucketName) {
        final List<String> bucketObjects = new ArrayList<>();

        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                .builder()
                .bucket(bucketName)
                .build();

            ListObjectsResponse res = s3.listObjects(listObjects);
            List<S3Object> objects = res.contents();

            for (S3Object myValue : objects) {
                bucketObjects.add(myValue.key());

                System.out.print("\n The name of the key is " + myValue.key());
                System.out.print("\n The object is " + calKb(myValue.size()) + " KBs");
                System.out.print("\n The owner is " + myValue.owner());
            }

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return bucketObjects;
    }

    //convert bytes to kbs.
    private static long calKb(Long val) {
        return val/1024;
    }
}
