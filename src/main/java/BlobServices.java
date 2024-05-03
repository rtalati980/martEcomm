import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobItem;

@Service
public class BlobServices {
	@Autowired
    private BlobServiceClient blobServiceClient;

    @Value("${azure.storage.blob.container-name}")
    private String containerName;

    public List<String> listBlobs() {
        List<String> blobNames = new ArrayList<>();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        for (BlobItem blobItem : containerClient.listBlobs()) {
            blobNames.add(blobItem.getName());
        }
        return blobNames;
    }

    public void uploadBlob(String blobName, MultipartFile file) throws IOException {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        blobClient.upload(file.getInputStream(), file.getSize(), true);
    }

	/*
	 * public InputStream downloadBlob(String blobName) { BlobContainerClient
	 * containerClient = blobServiceClient.getBlobContainerClient(containerName);
	 * BlobClient blobClient = containerClient.getBlobClient(blobName); return
	 * blobClient.download().get; }
	 */

    public void deleteBlob(String blobName) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        containerClient.getBlobClient(blobName).delete();
    }
}