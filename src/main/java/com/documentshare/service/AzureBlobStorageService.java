package com.documentshare.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


@Service
public class AzureBlobStorageService {

    @Value("${azure.storage.connection-string}")
    private String connectionString;

    @Value("${azure.storage.container-name}")
    private String containerName;

    private BlobServiceClient blobServiceClient;
    private BlobContainerClient containerClient;


    public void init() {
        this.blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        this.containerClient = blobServiceClient.getBlobContainerClient(containerName);

        // Create container if it doesn't exist
        if (!containerClient.exists()) {
            containerClient.create();
        }
    }


    public String uploadDocument(MultipartFile file, String userId) throws IOException {
        if (blobServiceClient == null) {
            init();
        }

        // Generate unique blob name
        String blobName = generateBlobName(file.getOriginalFilename(), userId);

        // Get blob client
        BlobClient blobClient = containerClient.getBlobClient(blobName);

        // Set content type
        BlobHttpHeaders headers = new BlobHttpHeaders()
                .setContentType(file.getContentType())
                .setContentDisposition("attachment; filename=\"" + file.getOriginalFilename() + "\"");

        // Upload file
        try (InputStream inputStream = file.getInputStream()) {
            blobClient.upload(inputStream, file.getSize(), true);
            blobClient.setHttpHeaders(headers);
        }

        return blobClient.getBlobUrl();
    }


    public String uploadDocument(byte[] fileBytes, String fileName, String contentType, String userId) {
        if (blobServiceClient == null) {
            init();
        }

        // Generate unique blob name
        String blobName = generateBlobName(fileName, userId);

        // Get blob client
        BlobClient blobClient = containerClient.getBlobClient(blobName);

        // Set content type
        BlobHttpHeaders headers = new BlobHttpHeaders()
                .setContentType(contentType)
                .setContentDisposition("attachment; filename=\"" + fileName + "\"");

        // Upload file
        blobClient.upload(new ByteArrayInputStream(fileBytes), fileBytes.length, true);
        blobClient.setHttpHeaders(headers);

        return blobClient.getBlobUrl();
    }


    public byte[] downloadDocument(String blobUrl) {
        if (blobServiceClient == null) {
            init();
        }

        // Extract blob name from URL
        String blobName = extractBlobNameFromUrl(blobUrl);
        BlobClient blobClient = containerClient.getBlobClient(blobName);

        if (!blobClient.exists()) {
            throw new RuntimeException("Document not found: " + blobUrl);
        }

        return blobClient.downloadContent().toBytes();
    }


    public String getDocumentContentType(String blobUrl) {
        if (blobServiceClient == null) {
            init();
        }

        String blobName = extractBlobNameFromUrl(blobUrl);
        BlobClient blobClient = containerClient.getBlobClient(blobName);

        if (!blobClient.exists()) {
            throw new RuntimeException("Document not found: " + blobUrl);
        }

        return blobClient.getProperties().getContentType();
    }


    public long getDocumentSize(String blobUrl) {
        if (blobServiceClient == null) {
            init();
        }

        String blobName = extractBlobNameFromUrl(blobUrl);
        BlobClient blobClient = containerClient.getBlobClient(blobName);

        if (!blobClient.exists()) {
            throw new RuntimeException("Document not found: " + blobUrl);
        }

        return blobClient.getProperties().getBlobSize();
    }


    public boolean deleteDocument(String blobUrl) {
        if (blobServiceClient == null) {
            init();
        }

        String blobName = extractBlobNameFromUrl(blobUrl);
        BlobClient blobClient = containerClient.getBlobClient(blobName);

        if (!blobClient.exists()) {
            return false;
        }

        blobClient.delete();
        return true;
    }


    public boolean documentExists(String blobUrl) {
        if (blobServiceClient == null) {
            init();
        }

        String blobName = extractBlobNameFromUrl(blobUrl);
        BlobClient blobClient = containerClient.getBlobClient(blobName);

        return blobClient.exists();
    }


    private String generateBlobName(String originalFileName, String userId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String fileExtension = getFileExtension(originalFileName);

        return String.format("documents/%s/%s_%s%s", userId, timestamp, uuid, fileExtension);
    }


    private String extractBlobNameFromUrl(String blobUrl) {
        // Remove the base URL and container name from the blob URL
        String baseUrl = blobServiceClient.getAccountUrl();
        String containerUrl = baseUrl + "/" + containerName + "/";

        if (blobUrl.startsWith(containerUrl)) {
            return blobUrl.substring(containerUrl.length());
        }

        throw new IllegalArgumentException("Invalid blob URL: " + blobUrl);
    }


    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.'));
    }


    public String getContentTypeFromFileName(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();

        switch (extension) {
            case ".pdf":
                return "application/pdf";
            case ".doc":
                return "application/msword";
            case ".docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case ".txt":
                return "text/plain";
            case ".csv":
                return "text/csv";
            case ".xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            case ".gif":
                return "image/gif";
            default:
                return "application/octet-stream";
        }
    }
}