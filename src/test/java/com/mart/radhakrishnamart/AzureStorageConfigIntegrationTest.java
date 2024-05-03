package com.mart.radhakrishnamart;
import static org.junit.jupiter.api.Assertions.*;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AzureStorageConfigIntegrationTest {

    @Autowired
    private BlobServiceClient blobServiceClient;

   @Value("${azure.storage.blob.container-name}")
    private String containerName;

    @Test
    public void testBlobServiceClientCreation() {
        assertNotNull(blobServiceClient, "BlobServiceClient should not be null");
    }

    @Test
    public void testBlobContainerExistence() {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        assertNotNull(containerClient, "Blob container should exist");
    }
}
