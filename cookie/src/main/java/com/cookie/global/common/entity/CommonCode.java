package com.cookie.global.common.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommonCode {

    @Id
    @Column(name = "common_code", length = 5)
    private String commonCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_code", referencedColumnName = "group_code")
    private CommonCodeGroup commonCodeGroup;

    @Column(name = "code_name", length = 50)
    private String codeName;
}