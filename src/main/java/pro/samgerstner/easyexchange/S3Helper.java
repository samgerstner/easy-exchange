package pro.samgerstner.easyexchange;

import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

public class S3Helper
{
   @Value("${aws.access-key}")
   private String accessKey;

   @Value("${aws.secret-key}")
   private String secretKey;

   @Value("${aws.region}")
   private String region;

   @Value("${aws.bucket-name}")
   private String bucketName;

   private AwsCredentials credentials;

   private S3Client client;

   public S3Helper()
   {
      AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(this.credentials);
      Region awsRegion = Region.of(this.region);
      credentials = AwsBasicCredentials.create(this.accessKey, this.secretKey);
      client = S3Client.builder().credentialsProvider(credentialsProvider).region(awsRegion).build();
   }

   public PutObjectResponse createUploadSessionFolder(String guid)
   {
      String folderName = guid + "/";
      PutObjectRequest req = PutObjectRequest.builder().bucket(this.bucketName).key(folderName).build();
      return this.client.putObject(req, RequestBody.empty());
   }

   public DeleteObjectResponse deleteUploadSessionFolder(String guid)
   {
      String folderName = guid + "/";
      DeleteObjectRequest req = DeleteObjectRequest.builder().bucket(this.bucketName).key(folderName).build();
      return this.client.deleteObject(req);
   }

   public PutObjectResponse uploadSessionFile(String sessionGUID, String fileName, byte[] fileData)
   {
      String completeFileName = sessionGUID + "/" + fileName;
      PutObjectRequest req = PutObjectRequest.builder().bucket(this.bucketName).key(completeFileName).build();
      return this.client.putObject(req, RequestBody.fromBytes(fileData));
   }

   public byte[] downloadSessionFile(String sessionGUID, String fileName)
   {
      String completeFileName = sessionGUID + "/" + fileName;
      GetObjectRequest req = GetObjectRequest.builder().bucket(this.bucketName).key(completeFileName).build();
      ResponseBytes<GetObjectResponse> objectBytes = this.client.getObjectAsBytes(req);
      return objectBytes.asByteArray();
   }

   public DeleteObjectResponse deleteSessionFile(String sessionGUID, String fileName)
   {
      String completeFileName = sessionGUID + "/" + fileName;
      DeleteObjectRequest req = DeleteObjectRequest.builder().bucket(this.bucketName).key(completeFileName).build();
      return this.client.deleteObject(req);
   }
}