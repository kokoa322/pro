package com.baesullin.pro.util;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 상속시 자동으로 컬럼화됨
@EntityListeners(AuditingEntityListener.class) //생성, 변경 시간 업데이트, DB로 null값이 난다면 properties 에서 추가 설정 해주자
public abstract class TimeStamped {

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;
}