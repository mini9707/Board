package com.example.board.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditRequestDto {
    private Long id;
    private String title;
    private String content;
}
