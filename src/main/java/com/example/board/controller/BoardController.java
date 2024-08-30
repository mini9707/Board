package com.example.board.controller;

import com.example.board.dto.DeleteRequestDto;
import com.example.board.dto.EditRequestDto;
import com.example.board.dto.PostRequestDto;
import com.example.board.entity.Board;
import com.example.board.entity.Member;
import com.example.board.repository.BoardRepository;
import com.example.board.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class BoardController {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    // 게시글 전부 가져오기
    @GetMapping("/")
    String board(Model model, HttpSession httpSession) {
        String username = (String) httpSession.getAttribute("username");
        if(username == null){
            return "/login";
        } else {
            List<Board> result = boardRepository.findAllByOrderByCreateTimeAsc();
            model.addAttribute("boards", result);
            return "board";
        }
    }

    // 글 작성페이지 요청
    @GetMapping("/write")
    String write(HttpSession httpSession){
        String username = (String) httpSession.getAttribute("username");
        if(username == null){
            return "/login";
        } else {
            return "write"; /// 안넣어도 ResponseBody 안붙이면 알아서 찾F아줌
        }
    }

    // 글 상세페이지
    @GetMapping("/detail/{id}")
    String boardDetail(@PathVariable("id") Long id, Model model, HttpSession httpSession) {
        Optional<Board> result = boardRepository.findById(id);
        if (result.isPresent()) {
            model.addAttribute("board", result.get());
            return "detail";
        } else {
            return "redirect:/";
        }
    }

    // 글 작성 요청
    @PostMapping("/post")
    RedirectView post(@RequestBody PostRequestDto postRequestDto, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return new RedirectView("/login");
        }

        Optional<Member> member = memberRepository.findByUsername(username);
        if (member.isPresent()) {
            Board board = new Board(postRequestDto.getTitle(), postRequestDto.getContent());
            board.setMember(member.get());
            boardRepository.save(board);
        }
        return new RedirectView("/");
    }

    // 글 수정 요청
    @PostMapping("/edit")
    ResponseEntity<Map<String, String>> edit(@RequestBody EditRequestDto editRequestDto, HttpSession session) {
        Optional<Board> optionalBoard = boardRepository.findById(editRequestDto.getId());
        if (optionalBoard.isPresent()) {
            if (optionalBoard.get().getMember().getUsername().equals(session.getAttribute("username"))) {
                Board board = optionalBoard.get();
                board.setTitle(editRequestDto.getTitle());
                board.setContent(editRequestDto.getContent());

                boardRepository.save(board);

                return ResponseEntity.ok(Collections.singletonMap("message", "수정이 완료되었습니다."));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("message", "본인이 작성한 글만 수정할 수 있습니다."));
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "게시글이 존재하지 않습니다."));
        }
    }

    // 글 삭제 요청
    @DeleteMapping("/post")
    public ResponseEntity<Map<String, String>> delete(@RequestBody DeleteRequestDto deleteRequestDto, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "Unauthorized"));
        }

        Optional<Board> optionalBoard = boardRepository.findById(deleteRequestDto.getId());
        if (optionalBoard.isPresent()) {
            Board board = optionalBoard.get();

            if (board.getMember().getUsername().equals(username)) {
                boardRepository.delete(board);

                return ResponseEntity.ok(Collections.singletonMap("message", "삭제가 완료되었습니다"));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("message", "Forbidden"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Board not found"));
        }
    }


}
