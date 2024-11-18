package com.cookie.global.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommonCodeGroup {

    @Id
    @Column(name = "group_code", length = 3)
    private String groupCode;

    @Column(name = "group_code_name", length = 20)
    private String groupCodeName;
}