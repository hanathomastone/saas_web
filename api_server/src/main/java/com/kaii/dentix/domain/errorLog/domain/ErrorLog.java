package com.kaii.dentix.domain.errorLog.domain;

import com.kaii.dentix.domain.type.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import java.util.Date;

@Entity
@Table(name = "errorLog")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@DynamicInsert
public class ErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long errorLogId;

    private Long tokenUserId;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum")
    private UserRole tokenUserRole;
    
    private String requestName;

    private String requestUrl;

    @Column(columnDefinition = "json")
    private String header;

    @Column(columnDefinition = "json")
    private String requestBody;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String errorLogMessage;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false)
    private Date created;

    @Override
    public String toString() {
        return "{"
            + "\"errorLogId\":" + errorLogId
            + ", \"tokenUserId\":" + tokenUserId
            + ", \"tokenUserRole\":\"" + tokenUserRole + "\""
            + ", \"requestName\":\"" + requestName + "\""
            + ", \"requestUrl\":\"" + requestUrl + "\""
            + ", \"header\":\"" + header + "\""
            + ", \"requestBody\":\"" + requestBody + "\""
            + ", \"errorLogMessage\":\"" + errorLogMessage + "\""
            + ", \"created\":\"" + created + "\""
            + "}";
    }
}
