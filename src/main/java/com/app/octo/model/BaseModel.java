package com.app.octo.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class) 
public abstract class BaseModel {

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false) 
    private Date createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false) 
    private Date updatedAt;
}
