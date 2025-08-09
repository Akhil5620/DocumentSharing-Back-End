package com.documentshare.repository;

import com.documentshare.entity.DocumentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface DocumentRepository extends MongoRepository<DocumentEntity, String> {

    
    Optional<DocumentEntity> findByShareableLink(String shareableLink);

    
    List<DocumentEntity> findByOwnerId(String ownerId);

    
    Page<DocumentEntity> findByOwnerId(String ownerId, Pageable pageable);

    
    List<DocumentEntity> findByOwnerIdOrderByCreatedAtDesc(String ownerId);

    
    List<DocumentEntity> findByTeamSharedTrue();

    
    Page<DocumentEntity> findByTeamSharedTrue(Pageable pageable);

    
    List<DocumentEntity> findByTeamSharedTrueOrderByCreatedAtDesc();

    
    @Query("{ 'sharedWithUsers': ?0 }")
    List<DocumentEntity> findBySharedWithUsersContaining(String userId);

    
    @Query("{ 'sharedWithUsers': ?0 }")
    Page<DocumentEntity> findBySharedWithUsersContaining(String userId, Pageable pageable);

    
    List<DocumentEntity> findByFileType(String fileType);

    
    Page<DocumentEntity> findByFileType(String fileType, Pageable pageable);

    
    List<DocumentEntity> findByNameContainingIgnoreCase(String name);

    
    Page<DocumentEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

    
    List<DocumentEntity> findByDescriptionContainingIgnoreCase(String description);

    
    Page<DocumentEntity> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);

    
    @Query("{ '$or': [ { 'name': { '$regex': ?0, '$options': 'i' } }, { 'description': { '$regex': ?0, '$options': 'i' } } ] }")
    List<DocumentEntity> searchDocuments(String searchTerm);

    
    @Query("{ '$or': [ { 'name': { '$regex': ?0, '$options': 'i' } }, { 'description': { '$regex': ?0, '$options': 'i' } } ] }")
    Page<DocumentEntity> searchDocuments(String searchTerm, Pageable pageable);

    
    @Query("{ 'fileSize': { '$gte': ?0, '$lte': ?1 } }")
    List<DocumentEntity> findByFileSizeBetween(Long minSize, Long maxSize);

    
    @Query("{ 'fileSize': { '$gte': ?0, '$lte': ?1 } }")
    Page<DocumentEntity> findByFileSizeBetween(Long minSize, Long maxSize, Pageable pageable);

    
    List<DocumentEntity> findByCreatedAtAfter(java.time.LocalDateTime date);

    
    Page<DocumentEntity> findByCreatedAtAfter(java.time.LocalDateTime date, Pageable pageable);

    
    List<DocumentEntity> findByOwnerIdAndFileType(String ownerId, String fileType);

    
    Page<DocumentEntity> findByOwnerIdAndFileType(String ownerId, String fileType, Pageable pageable);

    
    long countByOwnerId(String ownerId);

    
    long countByTeamSharedTrue();

    
    long countByFileType(String fileType);

    
    List<DocumentEntity> findAllByOrderByCreatedAtDesc();

    
    Page<DocumentEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
} 