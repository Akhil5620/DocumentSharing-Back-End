package com.documentshare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Set;


public class DocumentDto {

    // Request DTOs
    @Schema(description = "Request DTO for uploading a document")
    public static class UploadDocumentRequest {
        @NotBlank(message = "Document name is required")
        @Size(max = 255, message = "Document name cannot exceed 255 characters")
        @Schema(description = "Name of the document", example = "Project Proposal")
        private String name;

        @Size(max = 1000, message = "Description cannot exceed 1000 characters")
        @Schema(description = "Document description", example = "Q4 2024 project proposal document")
        private String description;

        @Schema(description = "Whether the document is shared with the team", example = "false")
        private boolean isTeamShared = false;

        @Schema(description = "List of user IDs to share the document with", example = "[\"user1\", \"user2\"]")
        private Set<String> sharedWithUsers;

        // Constructors
        public UploadDocumentRequest() {}

        public UploadDocumentRequest(String name, String description) {
            this.name = name;
            this.description = description;
        }

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isTeamShared() {
            return isTeamShared;
        }

        public void setTeamShared(boolean teamShared) {
            isTeamShared = teamShared;
        }

        public Set<String> getSharedWithUsers() {
            return sharedWithUsers;
        }

        public void setSharedWithUsers(Set<String> sharedWithUsers) {
            this.sharedWithUsers = sharedWithUsers;
        }
    }

    @Schema(description = "Request DTO for updating a document")
    public static class UpdateDocumentRequest {
        @Size(max = 255, message = "Document name cannot exceed 255 characters")
        @Schema(description = "Name of the document", example = "Updated Project Proposal")
        private String name;

        @Size(max = 1000, message = "Description cannot exceed 1000 characters")
        @Schema(description = "Document description", example = "Updated Q4 2024 project proposal document")
        private String description;

        @Schema(description = "Whether the document is shared with the team", example = "true")
        private Boolean isTeamShared;

        @Schema(description = "List of user IDs to share the document with", example = "[\"user1\", \"user2\", \"user3\"]")
        private Set<String> sharedWithUsers;

        // Constructors
        public UpdateDocumentRequest() {}

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Boolean getTeamShared() {
            return isTeamShared;
        }

        public void setTeamShared(Boolean teamShared) {
            isTeamShared = teamShared;
        }

        public Set<String> getSharedWithUsers() {
            return sharedWithUsers;
        }

        public void setSharedWithUsers(Set<String> sharedWithUsers) {
            this.sharedWithUsers = sharedWithUsers;
        }
    }

    @Schema(description = "Request DTO for sharing a document")
    public static class ShareDocumentRequest {
        @Schema(description = "ID of the document to share (optional, can be provided in URL path)", example = "507f1f77bcf86cd799439011")
        private String documentId;

        @Schema(description = "Whether the document is shared with the team", example = "true")
        private boolean isTeamShared = false;

        @Schema(description = "List of user IDs to share the document with", example = "[\"user1\", \"user2\"]")
        private Set<String> sharedWithUsers;

        // Constructors
        public ShareDocumentRequest() {}

        public ShareDocumentRequest(String documentId, boolean isTeamShared) {
            this.documentId = documentId;
            this.isTeamShared = isTeamShared;
        }

        // Getters and Setters
        public String getDocumentId() {
            return documentId;
        }

        public void setDocumentId(String documentId) {
            this.documentId = documentId;
        }

        public boolean isTeamShared() {
            return isTeamShared;
        }

        public void setTeamShared(boolean teamShared) {
            isTeamShared = teamShared;
        }

        public Set<String> getSharedWithUsers() {
            return sharedWithUsers;
        }

        public void setSharedWithUsers(Set<String> sharedWithUsers) {
            this.sharedWithUsers = sharedWithUsers;
        }
    }

    // Response DTOs
    @Schema(description = "Response DTO for document information")
    public static class DocumentResponse {
        @Schema(description = "Unique document ID", example = "507f1f77bcf86cd799439011")
        private String id;

        @Schema(description = "Name of the document", example = "Project Proposal")
        private String name;

        @Schema(description = "Document description", example = "Q4 2024 project proposal document")
        private String description;

        @Schema(description = "Original file name", example = "project_proposal.pdf")
        private String fileName;

        @Schema(description = "File type/extension", example = "pdf")
        private String fileType;

        @Schema(description = "File size in bytes", example = "1048576")
        private Long fileSize;

        @Schema(description = "Formatted file size", example = "1.0 MB")
        private String formattedFileSize;

        @Schema(description = "ID of the document owner", example = "507f1f77bcf86cd799439012")
        private String ownerId;

        @Schema(description = "Name of the document owner", example = "John Doe")
        private String ownerName;

        @Schema(description = "Azure Blob Storage URL", example = "https://storage.blob.core.windows.net/container/document.pdf")
        private String blobUrl;

        @Schema(description = "Shareable link for the document", example = "https://api.example.com/documents/share/abc123")
        private String shareableLink;

        @Schema(description = "Whether the document is shared with the team", example = "true")
        private boolean isTeamShared;

        @Schema(description = "List of user IDs the document is shared with", example = "[\"user1\", \"user2\"]")
        private Set<String> sharedWithUsers;

        @Schema(description = "Document creation timestamp", example = "2024-01-15T10:30:00")
        private LocalDateTime createdAt;

        @Schema(description = "Last update timestamp", example = "2024-01-15T10:30:00")
        private LocalDateTime updatedAt;

        @Schema(description = "Last access timestamp", example = "2024-01-15T10:30:00")
        private LocalDateTime lastAccessedAt;

        // Constructors
        public DocumentResponse() {}

        public DocumentResponse(String id, String name, String description, String fileName, String fileType,
                              Long fileSize, String ownerId, String ownerName, String blobUrl, String shareableLink,
                              boolean isTeamShared, Set<String> sharedWithUsers, LocalDateTime createdAt,
                              LocalDateTime updatedAt, LocalDateTime lastAccessedAt) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.fileName = fileName;
            this.fileType = fileType;
            this.fileSize = fileSize;
            this.ownerId = ownerId;
            this.ownerName = ownerName;
            this.blobUrl = blobUrl;
            this.shareableLink = shareableLink;
            this.isTeamShared = isTeamShared;
            this.sharedWithUsers = sharedWithUsers;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.lastAccessedAt = lastAccessedAt;
        }

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public Long getFileSize() {
            return fileSize;
        }

        public void setFileSize(Long fileSize) {
            this.fileSize = fileSize;
        }

        public String getFormattedFileSize() {
            return formattedFileSize;
        }

        public void setFormattedFileSize(String formattedFileSize) {
            this.formattedFileSize = formattedFileSize;
        }

        public String getOwnerId() {
            return ownerId;
        }

        public void setOwnerId(String ownerId) {
            this.ownerId = ownerId;
        }

        public String getOwnerName() {
            return ownerName;
        }

        public void setOwnerName(String ownerName) {
            this.ownerName = ownerName;
        }

        public String getBlobUrl() {
            return blobUrl;
        }

        public void setBlobUrl(String blobUrl) {
            this.blobUrl = blobUrl;
        }

        public String getShareableLink() {
            return shareableLink;
        }

        public void setShareableLink(String shareableLink) {
            this.shareableLink = shareableLink;
        }

        public boolean isTeamShared() {
            return isTeamShared;
        }

        public void setTeamShared(boolean teamShared) {
            isTeamShared = teamShared;
        }

        public Set<String> getSharedWithUsers() {
            return sharedWithUsers;
        }

        public void setSharedWithUsers(Set<String> sharedWithUsers) {
            this.sharedWithUsers = sharedWithUsers;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }

        public LocalDateTime getLastAccessedAt() {
            return lastAccessedAt;
        }

        public void setLastAccessedAt(LocalDateTime lastAccessedAt) {
            this.lastAccessedAt = lastAccessedAt;
        }
    }

    @Schema(description = "Request DTO for searching documents")
    public static class DocumentSearchRequest {
        @Schema(description = "Search term for document name or description", example = "project")
        private String searchTerm;

        @Schema(description = "File type filter", example = "pdf")
        private String fileType;

        @Schema(description = "Minimum file size in bytes", example = "1024")
        private Long minSize;

        @Schema(description = "Maximum file size in bytes", example = "10485760")
        private Long maxSize;

        @Schema(description = "Filter for team-shared documents", example = "true")
        private Boolean isTeamShared;

        @Schema(description = "Page number (0-based)", example = "0")
        private Integer page = 0;

        @Schema(description = "Page size", example = "10")
        private Integer size = 10;

        @Schema(description = "Sort field", example = "createdAt")
        private String sortBy = "createdAt";

        @Schema(description = "Sort direction (asc/desc)", example = "desc")
        private String sortDirection = "desc";

        // Constructors
        public DocumentSearchRequest() {}

        // Getters and Setters
        public String getSearchTerm() {
            return searchTerm;
        }

        public void setSearchTerm(String searchTerm) {
            this.searchTerm = searchTerm;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public Long getMinSize() {
            return minSize;
        }

        public void setMinSize(Long minSize) {
            this.minSize = minSize;
        }

        public Long getMaxSize() {
            return maxSize;
        }

        public void setMaxSize(Long maxSize) {
            this.maxSize = maxSize;
        }

        public Boolean getTeamShared() {
            return isTeamShared;
        }

        public void setTeamShared(Boolean teamShared) {
            isTeamShared = teamShared;
        }

        public Integer getPage() {
            return page;
        }

        public void setPage(Integer page) {
            this.page = page;
        }

        public Integer getSize() {
            return size;
        }

        public void setSize(Integer size) {
            this.size = size;
        }

        public String getSortBy() {
            return sortBy;
        }

        public void setSortBy(String sortBy) {
            this.sortBy = sortBy;
        }

        public String getSortDirection() {
            return sortDirection;
        }

        public void setSortDirection(String sortDirection) {
            this.sortDirection = sortDirection;
        }
    }

    @Schema(description = "Response DTO for document download")
    public static class DocumentDownloadResponse {
        @Schema(description = "File content as byte array", example = "[base64-encoded-content]")
        private byte[] fileContent;

        @Schema(description = "Original file name", example = "project_proposal.pdf")
        private String fileName;

        @Schema(description = "MIME content type", example = "application/pdf")
        private String contentType;

        @Schema(description = "File size in bytes", example = "1048576")
        private Long fileSize;

        // Constructors
        public DocumentDownloadResponse() {}

        public DocumentDownloadResponse(byte[] fileContent, String fileName, String contentType, Long fileSize) {
            this.fileContent = fileContent;
            this.fileName = fileName;
            this.contentType = contentType;
            this.fileSize = fileSize;
        }

        // Getters and Setters
        public byte[] getFileContent() {
            return fileContent;
        }

        public void setFileContent(byte[] fileContent) {
            this.fileContent = fileContent;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public Long getFileSize() {
            return fileSize;
        }

        public void setFileSize(Long fileSize) {
            this.fileSize = fileSize;
        }
    }
} 