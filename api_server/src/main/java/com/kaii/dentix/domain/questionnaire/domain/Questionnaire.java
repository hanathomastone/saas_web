package com.kaii.dentix.domain.questionnaire.domain;

import com.kaii.dentix.domain.oralStatus.domain.OralStatus;
import com.kaii.dentix.domain.userOralStatus.domain.UserOralStatus;
import com.kaii.dentix.global.common.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "questionnaire")
public class Questionnaire extends TimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionnaireId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String questionnaireVersion;

    @Column(name = "form", columnDefinition = "json")
    private String form;

    @OneToMany(mappedBy = "questionnaire", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    List<UserOralStatus> userOralStatusList;

    public Questionnaire(Long userId, String questionnaireVersion, String form, List<String> oralStatusTypeList) {
        this.userId = userId;
        this.questionnaireVersion = questionnaireVersion;
        this.form = form;
        userOralStatusList = oralStatusTypeList.stream()
            .map(oralStatusType -> new UserOralStatus(this, new OralStatus(oralStatusType)))
            .collect(Collectors.toList());
    }
}
