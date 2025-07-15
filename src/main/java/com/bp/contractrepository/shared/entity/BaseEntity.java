package com.bp.contractrepository.shared.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Base Entity class for Contract Repository System
 * Provides common fields for all entities including audit trail
 *
 * Features:
 * - Auto-generated ID
 * - Audit fields (created/modified by/date)
 * - Optimistic locking with version
 * - Soft delete capability
 *
 * @author Contract Management Team
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @CreatedBy
    @Column(name = "created_by", length = 100, updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "last_modified_by", length = 100)
    private String lastModifiedBy;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "is_active")
    private Boolean isActive = true;

    /**
     * Pre-persist callback to set defaults
     */
    @PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
        if (isActive == null) {
            isActive = true;
        }
    }

    /**
     * Pre-update callback to update modification date
     */
    @PreUpdate
    protected void onUpdate() {
        lastModifiedDate = LocalDateTime.now();
    }

    /**
     * Soft delete - marks entity as inactive instead of physical deletion
     */
    public void softDelete() {
        this.isActive = false;
        this.lastModifiedDate = LocalDateTime.now();
    }

    /**
     * Restore soft deleted entity
     */
    public void restore() {
        this.isActive = true;
        this.lastModifiedDate = LocalDateTime.now();
    }

    /**
     * Check if entity is new (not yet persisted)
     */
    public boolean isNew() {
        return id == null;
    }

    /**
     * Equals implementation based on ID
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        BaseEntity that = (BaseEntity) obj;

        // If both entities are new (no ID), they are not equal
        if (id == null && that.id == null) {
            return false;
        }

        return Objects.equals(id, that.id);
    }

    /**
     * HashCode implementation based on ID
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * ToString implementation for debugging
     */
    @Override
    public String toString() {
        return String.format("%s{id=%d, createdDate=%s, isActive=%s}",
                getClass().getSimpleName(),
                id,
                createdDate,
                isActive);
    }
}