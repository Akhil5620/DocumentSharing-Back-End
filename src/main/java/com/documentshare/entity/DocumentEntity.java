package com.documentshare.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Document(collection = "documents")
public class DocumentEntity {

    @Id
    private String id;

    @NotBlank(message = "Document name is required")
    @Size(max = 255, message = "Document name cannot exceed 255 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotBlank(message = "File name is required")
    private String fileName;

    @NotBlank(message = "File type is required")
    private String fileType;

    @NotNull(message = "File size is required")
    private Long fileSize;

    @NotBlank(message = "Owner ID is required")
    @Indexed
    private String ownerId;

    @NotBlank(message = "Owner name is required")
    private String ownerName;

    @NotBlank(message = "Azure blob URL is required")
    private String blobUrl;

    @NotBlank(message = "Shareable link is required")
    @Indexed(unique = true)
    private String shareableLink;

    @org.springframework.data.mongodb.core.mapping.Field("teamShared")
    private boolean teamShared = false;

    private Set<String> sharedWithUsers = new HashSet<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime lastAccessedAt;

    // Default constructor
    public DocumentEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.lastAccessedAt = LocalDateTime.now();
    }

    // Constructor with required fields
    public DocumentEntity(String name, String fileName, String fileType, Long fileSize, 
                   String ownerId, String ownerName, String blobUrl, String shareableLink) {
        this();
        this.name = name;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.blobUrl = blobUrl;
        this.shareableLink = shareableLink;
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
        return teamShared;
    }

    public void setTeamShared(boolean teamShared) {
        this.teamShared = teamShared;
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

    // Helper methods
    public void addSharedUser(String userId) {
        this.sharedWithUsers.add(userId);
    }

    public void removeSharedUser(String userId) {
        this.sharedWithUsers.remove(userId);
    }

    public boolean isSharedWithUser(String userId) {
        return this.sharedWithUsers.contains(userId);
    }

    public boolean isOwnedBy(String userId) {
        return this.ownerId.equals(userId);
    }

    public String getFormattedFileSize() {
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else if (fileSize < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", fileSize / (1024.0 * 1024.0 * 1024.0));
        }
    }

    public void updateLastAccessed() {
        this.lastAccessedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Document{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileSize=" + fileSize +
                ", ownerId='" + ownerId + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", blobUrl='" + blobUrl + '\'' +
                ", shareableLink='" + shareableLink + '\'' +
                ", teamShared=" + teamShared +
                ", sharedWithUsers=" + sharedWithUsers +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", lastAccessedAt=" + lastAccessedAt +
                '}';
    }
} 