package com.kaii.dentix.domain.organization.domain;

import com.kaii.dentix.domain.subscriptionPlan.domain.SubscriptionPlan;
import com.kaii.dentix.global.common.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "organization")
@Where(clause = "deleted_at IS NULL")
public class Organization extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long organizationId;

    @Column(length = 100, nullable = false)
    private String organizationName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_plan_id", nullable = false)
    private SubscriptionPlan subscriptionPlan;

    @Column(name = "success_count", nullable = false)
    private Integer successCount; // 성공 응답 수

    @Column(name = "usage_reset_date")
    private LocalDateTime usageResetDate; // 리셋 예정일


    @Column(name = "deleted")
    private LocalDateTime deleted;

    //기관 정보 수정
    public void updateOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
    public void updateSubscriptionPlan(SubscriptionPlan plan){
        this.subscriptionPlan = plan;
    }
    //기관 삭제
    public void deleteOrganization() {
        this.deleted = LocalDateTime.now();
    }

    public void updateSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public void updateUsageResetDate(LocalDateTime usageResetDate) {
        this.usageResetDate = usageResetDate;
    }

    @PrePersist
    public void prePersist() {
        if (this.successCount == null) {
            this.successCount = 0;
        }
    }

}
