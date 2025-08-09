package com.documentshare.controller;

import com.documentshare.dto.DocumentDto;
import com.documentshare.security.JwtAuthenticationFilter;
import com.documentshare.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/documents")
@Tag(name = "Documents", description = "Document management endpoints")
@CrossOrigin(origins = "*")
public class DocumentController {

    @Autowired
    private DocumentService documentService;


    private JwtAuthenticationFilter.UserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof JwtAuthenticationFilter.UserDetails) {
            return (JwtAuthenticationFilter.UserDetails) authentication.getDetails();
        }
        return null;
    }


    @PostMapping("/upload")
    @Operation(summary = "Upload a document", description = "Upload a new document")
    public ResponseEntity<DocumentDto.DocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "isTeamShared", defaultValue = "false") boolean isTeamShared,
            @RequestParam(value = "sharedWithUsers", required = false) String sharedWithUsers) {
        
        try {
            JwtAuthenticationFilter.UserDetails currentUser = getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(401).build();
            }
            
            DocumentDto.UploadDocumentRequest uploadRequest = new DocumentDto.UploadDocumentRequest();
            uploadRequest.setName(name);
            uploadRequest.setDescription(description);
            uploadRequest.setTeamShared(isTeamShared);
            
            DocumentDto.DocumentResponse response = documentService.uploadDocument(file, uploadRequest, currentUser.getUserId(), currentUser.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/my-files")
    @Operation(summary = "Get user's documents", description = "Get all documents owned by the current user")
    public ResponseEntity<java.util.List<DocumentDto.DocumentResponse>> getMyDocuments() {
        try {
            JwtAuthenticationFilter.UserDetails currentUser = getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(401).build();
            }
            
            java.util.List<DocumentDto.DocumentResponse> documents = documentService.getUserDocuments(currentUser.getUserId());
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/team-files")
    @Operation(summary = "Get team documents", description = "Get all team-shared documents")
    public ResponseEntity<java.util.List<DocumentDto.DocumentResponse>> getTeamDocuments() {
        try {
            java.util.List<DocumentDto.DocumentResponse> documents = documentService.getTeamDocuments();
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get document by ID", description = "Get document details by ID")
    public ResponseEntity<DocumentDto.DocumentResponse> getDocumentById(@PathVariable String id) {
        try {
            java.util.Optional<DocumentDto.DocumentResponse> document = documentService.getDocumentById(id);
            return document.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/{id}/download")
    @Operation(summary = "Download document", description = "Download document by ID")
    public ResponseEntity<ByteArrayResource> downloadDocument(@PathVariable String id) {
        try {
            JwtAuthenticationFilter.UserDetails currentUser = getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(401).build();
            }
            
            DocumentDto.DocumentDownloadResponse downloadResponse = documentService.downloadDocument(id, currentUser.getUserId());
            
            ByteArrayResource resource = new ByteArrayResource(downloadResponse.getFileContent());
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadResponse.getFileName() + "\"")
                    .contentType(MediaType.parseMediaType(downloadResponse.getContentType()))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/share/{shareableLink}")
    @Operation(summary = "Download document by shareable link", description = "Download document using shareable link")
    public ResponseEntity<ByteArrayResource> downloadDocumentByLink(@PathVariable String shareableLink) {
        try {
            DocumentDto.DocumentDownloadResponse downloadResponse = documentService.downloadDocumentByLink(shareableLink);
            
            ByteArrayResource resource = new ByteArrayResource(downloadResponse.getFileContent());
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadResponse.getFileName() + "\"")
                    .contentType(MediaType.parseMediaType(downloadResponse.getContentType()))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @PostMapping("/{id}/share")
    @Operation(summary = "Share document", description = "Share document with team or specific users")
    public ResponseEntity<DocumentDto.DocumentResponse> shareDocument(
            @PathVariable String id,
            @Valid @RequestBody DocumentDto.ShareDocumentRequest shareRequest) {
        try {
            java.util.Optional<DocumentDto.DocumentResponse> document = documentService.shareDocument(id, shareRequest);
            return document.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @PutMapping("/{id}")
    @Operation(summary = "Update document", description = "Update document details")
    public ResponseEntity<DocumentDto.DocumentResponse> updateDocument(
            @PathVariable String id,
            @Valid @RequestBody DocumentDto.UpdateDocumentRequest updateRequest) {
        try {
            java.util.Optional<DocumentDto.DocumentResponse> document = documentService.updateDocument(id, updateRequest);
            return document.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete document", description = "Delete document by ID")
    public ResponseEntity<Void> deleteDocument(@PathVariable String id) {
        try {
            JwtAuthenticationFilter.UserDetails currentUser = getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(401).build();
            }
            
            boolean isAdmin = currentUser.getRoles().contains("ADMIN");
            boolean deleted = documentService.deleteDocument(id, currentUser.getUserId(), isAdmin);
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @DeleteMapping("/admin/team/{id}")
    @Operation(summary = "Admin delete team document", description = "Delete team-shared document (Admin only)")
    public ResponseEntity<Void> adminDeleteTeamDocument(@PathVariable String id) {
        try {
            JwtAuthenticationFilter.UserDetails currentUser = getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(401).build();
            }
            
            // Check if user is admin
            if (!currentUser.getRoles().contains("ADMIN")) {
                return ResponseEntity.status(403).build();
            }
            
            boolean deleted = documentService.deleteTeamDocument(id, currentUser.getUserId());
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/admin/all")
    @Operation(summary = "Admin get all documents", description = "Get all documents in the system (Admin only)")
    public ResponseEntity<java.util.List<DocumentDto.DocumentResponse>> adminGetAllDocuments() {
        try {
            JwtAuthenticationFilter.UserDetails currentUser = getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(401).build();
            }
            
            // Check if user is admin
            if (!currentUser.getRoles().contains("ADMIN")) {
                return ResponseEntity.status(403).build();
            }
            
            java.util.List<DocumentDto.DocumentResponse> documents = documentService.getAllDocuments();
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/admin/team")
    @Operation(summary = "Admin get team documents", description = "Get all team-shared documents (Admin only)")
    public ResponseEntity<java.util.List<DocumentDto.DocumentResponse>> adminGetTeamDocuments() {
        try {
            JwtAuthenticationFilter.UserDetails currentUser = getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(401).build();
            }
            
            // Check if user is admin
            if (!currentUser.getRoles().contains("ADMIN")) {
                return ResponseEntity.status(403).build();
            }
            
            java.util.List<DocumentDto.DocumentResponse> documents = documentService.getTeamDocuments();
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/search")
    @Operation(summary = "Search documents", description = "Search documents by name or description")
    public ResponseEntity<java.util.List<DocumentDto.DocumentResponse>> searchDocuments(
            @RequestParam("q") String searchTerm) {
        try {
            java.util.List<DocumentDto.DocumentResponse> documents = documentService.searchDocuments(searchTerm);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 