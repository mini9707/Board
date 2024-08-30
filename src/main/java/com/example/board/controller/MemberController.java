package com.example.board.controller;

import com.example.board.dto.LoginRequestDto;
import com.example.board.dto.MemberRequestDto;
import com.example.board.entity.Member;
import com.example.board.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

    // 로그인 페이지 접속
    @GetMapping("/login")
    String login() {
        return "login";
    }

    // 회원가입 페이지 접속
    @GetMapping("/sign-up")
    String signUp() {
        return "signup";
    }

    // 로그아웃 요청
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.status(HttpStatus.OK).body("로그아웃 완료");
    }

    // 아이디 중복 체크
    @GetMapping("/check-username")
    public ResponseEntity<String> checkUsername(@RequestParam String username) {
        boolean exists = memberRepository.findByUsername(username).isPresent();
        if (exists) {
            return ResponseEntity.badRequest().body("이미 존재하는 아이디입니다.");
        } else {
            return ResponseEntity.ok("사용 가능한 아이디입니f다.");
        }
    }

    // 로그인 요청
    @PostMapping("/login")
    ResponseEntity<String> memberLogin(@RequestBody LoginRequestDto loginRequestDto, HttpSession session) {
        Optional<Member> member = memberRepository.findByUsername(loginRequestDto.getUsername());
        if (member.isPresent()) {
            if(Objects.equals(member.get().getPassword(), loginRequestDto.getPassword())) {
                session.setAttribute("username", loginRequestDto.getUsername());
                return ResponseEntity.status(HttpStatus.OK).body("success");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("unauthorized");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("unauthorized");
        }
    }

    // 회원가입 요청
    @PostMapping("/sign-up")
    public ResponseEntity<String> addMember(@RequestBody Member memberRequestDto) {
        if (memberRequestDto.getUsername().isEmpty()) {
            return ResponseEntity.badRequest().body("아이디를 입력해주세요.");
        }
        if (memberRequestDto.getDisplayName().isEmpty()) {
            return ResponseEntity.badRequest().body("이름을 입력해주세요.");
        }
        if (memberRequestDto.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("비밀번호를 입력해주세요.");
        }
        var result = memberRepository.findByUsername(memberRequestDto.getUsername());
        if (result.isEmpty()) {
            Member member = new Member(memberRequestDto.getUsername(), memberRequestDto.getDisplayName(), memberRequestDto.getPassword());
            memberRepository.save(member);
            return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("이미 존재하는 아이디입니다.");
        }
    }


}
