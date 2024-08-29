package com.example.board.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.security.Timestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String username;

    @Column
    private String displayName;

    @Column(nullable = false, length = 15)
    private String password;

    @Column(nullable= false, updatable = false)
    private LocalDateTime createTime;

    @Column(nullable = true, updatable = true)
    private LocalDateTime editTime;

    @PrePersist //엔티티가 영속성 컨텍스트에 저장되기 전에 호출되는 콜백 메서드
    protected void onCreate() {
        this.createTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.editTime = LocalDateTime.now();
    }

    public Member() {}

    public Member(String username, String displayName, String password) {
        this.username = username;
        this.displayName = displayName;
        this.password = password;
    }
}
