package com.documentshare.service;

import com.documentshare.dto.DocumentDto;
import com.documentshare.entity.DocumentEntity;
import com.documentshare.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Transactional
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private AzureBlobStorageService azureBlobStorageService;


    public DocumentDto.DocumentResponse uploadDocument(MultipartFile file, DocumentDto.UploadDocumentRequest uploadRequest, String userId, String userName) throws IOException {
        // Validate file
        validateFile(file);

        // Upload to Azure Blob Storage
        String blobUrl = azureBlobStorageService.uploadDocument(file, userId);

        // Generate shareable link
        String shareableLink = generateShareableLink();

        // Create document entity
        DocumentEntity document = new DocumentEntity();
        document.setName(uploadRequest.getName());
        document.setDescription(uploadRequest.getDescription());
        document.setFileName(file.getOriginalFilename());
        document.setFileType(getFileExtension(file.getOriginalFilename()));
        document.setFileSize(file.getSize());
        document.setOwnerId(userId);
        document.setOwnerName(userName);
        document.setBlobUrl(blobUrl);
        document.setShareableLink(shareableLink);
        document.setTeamShared(uploadRequest.isTeamShared());

        if (uploadRequest.getSharedWithUsers() != null) {
            document.setSharedWithUsers(uploadRequest.getSharedWithUsers());
        }

        // Save document
        DocumentEntity savedDocument = documentRepository.save(document);
        return convertToDocumentResponse(savedDocument);
    }


    @Transactional(readOnly = true)
    public Optional<DocumentDto.DocumentResponse> getDocumentById(String id) {
        return documentRepository.findById(id)
                .map(this::convertToDocumentResponse);
    }


    @Transactional(readOnly = true)
    public Optional<DocumentDto.DocumentResponse> getDocumentByShareableLink(String shareableLink) {
        return documentRepository.findByShareableLink(shareableLink)
                .map(this::convertToDocumentResponse);
    }


    @Transactional(readOnly = true)
    public List<DocumentDto.DocumentResponse> getUserDocuments(String userId) {
        return documentRepository.findByOwnerIdOrderByCreatedAtDesc(userId).stream()
                .map(this::convertToDocumentResponse)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public Page<DocumentDto.DocumentResponse> getUserDocuments(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return documentRepository.findByOwnerId(userId, pageable)
                .map(this::convertToDocumentResponse);
    }


    @Transactional(readOnly = true)
    public List<DocumentDto.DocumentResponse> getTeamDocuments() {
        return documentRepository.findByTeamSharedTrueOrderByCreatedAtDesc().stream()
                .map(this::convertToDocumentResponse)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public Page<DocumentDto.DocumentResponse> getTeamDocuments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return documentRepository.findByTeamSharedTrue(pageable)
                .map(this::convertToDocumentResponse);
    }


    @Transactional(readOnly = true)
    public List<DocumentDto.DocumentResponse> getDocumentsSharedWithUser(String userId) {
        return documentRepository.findBySharedWithUsersContaining(userId).stream()
                .map(this::convertToDocumentResponse)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<DocumentDto.DocumentResponse> searchDocuments(String searchTerm) {
        return documentRepository.searchDocuments(searchTerm).stream()
                .map(this::convertToDocumentResponse)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public Page<DocumentDto.DocumentResponse> searchDocuments(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return documentRepository.searchDocuments(searchTerm, pageable)
                .map(this::convertToDocumentResponse);
    }


    public Optional<DocumentDto.DocumentResponse> updateDocument(String id, DocumentDto.UpdateDocumentRequest updateRequest) {
        return documentRepository.findById(id)
                .map(document -> {
                    if (updateRequest.getName() != null) {
                        document.setName(updateRequest.getName());
                    }

                    if (updateRequest.getDescription() != null) {
                        document.setDescription(updateRequest.getDescription());
                    }

                    if (updateRequest.getTeamShared() != null) {
                        document.setTeamShared(updateRequest.getTeamShared());
                    }

                    if (updateRequest.getSharedWithUsers() != null) {
                        document.setSharedWithUsers(updateRequest.getSharedWithUsers());
                    }

                    document.setUpdatedAt(LocalDateTime.now());
                    DocumentEntity savedDocument = documentRepository.save(document);
                    return convertToDocumentResponse(savedDocument);
                });
    }


    public Optional<DocumentDto.DocumentResponse> shareDocument(String id, DocumentDto.ShareDocumentRequest shareRequest) {
        return documentRepository.findById(id)
                .map(document -> {
                    System.err.println("Before update - teamShared: " + document.isTeamShared());
                    System.err.println("Request isTeamShared: " + shareRequest.isTeamShared());

                    document.setTeamShared(shareRequest.isTeamShared());

                    System.err.println("After setTeamShared - teamShared: " + document.isTeamShared());

                    if (shareRequest.getSharedWithUsers() != null) {
                        document.setSharedWithUsers(shareRequest.getSharedWithUsers());
                    }

                    document.setUpdatedAt(LocalDateTime.now());
                    DocumentEntity savedDocument = documentRepository.save(document);

                    System.err.println("After save - teamShared: " + savedDocument.isTeamShared());

                    return convertToDocumentResponse(savedDocument);
                });
    }


    public boolean deleteDocument(String id, String userId, boolean isAdmin) {
        return documentRepository.findById(id)
                .map(document -> {
                    // Check if user can delete this document
                    if (!isAdmin && !document.isOwnedBy(userId)) {
                        throw new RuntimeException("You can only delete your own documents");
                    }

                    // Delete from Azure Blob Storage
                    azureBlobStorageService.deleteDocument(document.getBlobUrl());

                    // Delete from database
                    documentRepository.deleteById(id);
                    return true;
                })
                .orElse(false);
    }


    public boolean deleteTeamDocument(String id, String adminUserId) {
        return documentRepository.findById(id)
                .map(document -> {
                    // Only allow deletion of team-shared documents
                    if (!document.isTeamShared()) {
                        throw new RuntimeException("Only team-shared documents can be deleted by admin");
                    }

                    // Delete from Azure Blob Storage
                    azureBlobStorageService.deleteDocument(document.getBlobUrl());

                    // Delete from database
                    documentRepository.deleteById(id);
                    return true;
                })
                .orElse(false);
    }


    @Transactional(readOnly = true)
    public List<DocumentDto.DocumentResponse> getAllDocuments() {
        return documentRepository.findAll().stream()
                .map(this::convertToDocumentResponse)
                .collect(Collectors.toList());
    }


    public DocumentDto.DocumentDownloadResponse downloadDocument(String id, String userId) {
        return documentRepository.findById(id)
                .map(document -> {
                    // Check access permissions
                    if (!document.isOwnedBy(userId) &&
                            !document.isTeamShared() &&
                            !document.isSharedWithUser(userId)) {
                        throw new RuntimeException("Access denied");
                    }

                    try {
                        // Download from Azure Blob Storage
                        byte[] fileContent = azureBlobStorageService.downloadDocument(document.getBlobUrl());
                        String contentType = azureBlobStorageService.getDocumentContentType(document.getBlobUrl());

                        // Update last accessed time
                        document.updateLastAccessed();
                        documentRepository.save(document);

                        return new DocumentDto.DocumentDownloadResponse(
                                fileContent,
                                document.getFileName(),
                                contentType,
                                document.getFileSize()
                        );
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to download document", e);
                    }
                })
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }


    public DocumentDto.DocumentDownloadResponse downloadDocumentByLink(String shareableLink) {
        return documentRepository.findByShareableLink(shareableLink)
                .map(document -> {
                    try {
                        // Download from Azure Blob Storage
                        byte[] fileContent = azureBlobStorageService.downloadDocument(document.getBlobUrl());
                        String contentType = azureBlobStorageService.getDocumentContentType(document.getBlobUrl());

                        // Update last accessed time
                        document.updateLastAccessed();
                        documentRepository.save(document);

                        return new DocumentDto.DocumentDownloadResponse(
                                fileContent,
                                document.getFileName(),
                                contentType,
                                document.getFileSize()
                        );
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to download document", e);
                    }
                })
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }


    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        if (file.getSize() > 25 * 1024 * 1024) { // 25MB limit
            throw new RuntimeException("File size exceeds 25MB limit");
        }

        String fileExtension = getFileExtension(file.getOriginalFilename()).toLowerCase();
        if (!isAllowedFileType(fileExtension)) {
            throw new RuntimeException("File type not allowed");
        }
    }


    private boolean isAllowedFileType(String fileExtension) {
        String[] allowedTypes = {".pdf", ".doc", ".docx", ".txt", ".csv", ".xlsx", ".jpg", ".jpeg", ".png", ".gif"};
        for (String allowedType : allowedTypes) {
            if (allowedType.equals(fileExtension)) {
                return true;
            }
        }
        return false;
    }


    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.'));
    }


    private String generateShareableLink() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    private DocumentDto.DocumentResponse convertToDocumentResponse(DocumentEntity document) {
        DocumentDto.DocumentResponse response = new DocumentDto.DocumentResponse(
                document.getId(),
                document.getName(),
                document.getDescription(),
                document.getFileName(),
                document.getFileType(),
                document.getFileSize(),
                document.getOwnerId(),
                document.getOwnerName(),
                document.getBlobUrl(),
                document.getShareableLink(),
                document.isTeamShared(),
                document.getSharedWithUsers(),
                document.getCreatedAt(),
                document.getUpdatedAt(),
                document.getLastAccessedAt()
        );

        response.setFormattedFileSize(document.getFormattedFileSize());
        return response;
       }
}