package com.example.board.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.catalina.User;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.security.Timestamp;
import java.time.LocalDateTime;  // ctrl shift n

@Entity
@Getter
@Setter
@ToString
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false, length = 30)
    private String title;

    @Column (nullable = false, length = 500)
    private String content;

    @ManyToOne
    @JoinColumn (nullable = false)
    private Member member;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    @Column(nullable = true, updatable = true)
    private LocalDateTime editTime;

    @PrePersist //엔티티가 영속성 컨텍스트에 저장되기 전에 호출되는 콜백 메서드
    protected void onCreate() {
        this.createTime = LocalDateTime.now();
        this.editTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.editTime = LocalDateTime.now();
    }



    public Board(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Board(Long id, String title, String content) {

    }

    public Board() {

    }
}
