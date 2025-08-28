package com.kaii.dentix.domain.subscriptionPlan.domain;

import com.kaii.dentix.global.common.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subscription_plan")
@Where(clause = "deleted IS NULL")
public class SubscriptionPlan extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 구독상품 플랜ID (PK)

    @Column(name = "plan_name", length = 45, nullable = false)
    private String planName; // 구독상품 플랜명

    @Column(name = "plan_cycle", length = 20, nullable = false)
    private String planCycle; // monthly, yearly

    @Column(name = "plan_sort", nullable = false)
    private Integer planSort; // 정렬 순서

    @Column(nullable = false)
    private Long price; // 구독상품 가격

    @Column(name = "max_success_responses", nullable = false)
    private Integer maxSuccessResponses; // 최대 성공 응답 수

    @Column(name = "deleted")
    private LocalDateTime deleted;

    /** Soft delete */
    public void deletePlan() {
        this.deleted = LocalDateTime.now();
    }
}
