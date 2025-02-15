package com.sp.refreshtoken.entity.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@MappedSuperclass
@EntityListeners(EntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public abstract class BaseEntity implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @JsonIgnore
    private String id;

    @Version
    @Column(name = "version")
    @JsonIgnore
    private Integer version;

    private String createdBy;

    private String updatedBy;


    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedDate;
}
