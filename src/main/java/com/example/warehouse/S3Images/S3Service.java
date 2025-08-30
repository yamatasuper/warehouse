package com.example.warehouse.S3Images;

import java.io.IOException;
import org.springframework.stereotype.Service; // вместо org.jvnet.hk2.annotations.Service
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class S3Service {
  private final S3Client s3Client;
  private final S3Properties s3Properties;

  public S3Service(S3Client s3Client, S3Properties s3Properties) {
    this.s3Client = s3Client;
    this.s3Properties = s3Properties;
  }

  public String uploadFile(MultipartFile file, String key) throws IOException {
    try {
      s3Client.putObject(
          PutObjectRequest.builder().bucket(s3Properties.getBucketName()).key(key).build(),
          RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
      return key;
    } catch (S3Exception e) {
      throw new IOException("Failed to upload file to S3", e);
    }
  }

  public String getFileUrl(String key) {
    return String.format("%s/%s/%s", s3Properties.getEndpoint(), s3Properties.getBucketName(), key);
  }
}
