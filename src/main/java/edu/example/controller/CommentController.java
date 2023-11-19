package edu.example.controller;

import edu.example.dto.PageResponse;
import edu.example.dto.comment.CommentByPostRequestDto;
import edu.example.dto.comment.CommentResponseDto;
import edu.example.dto.comment.CreateCommentRequestDto;
import edu.example.mapper.CommentMapper;
import edu.example.model.Comment;
import edu.example.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
@Tag(name = "Comment", description = "work with comment")
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @PostMapping
    @Operation(description = "Create comment")
    public CommentResponseDto create(@RequestBody @Valid CreateCommentRequestDto createCommentRequestDto) {
        Comment comment = commentService.createComment(
                createCommentRequestDto.getPostId(),
                createCommentRequestDto.getText()
        );
        return commentMapper.toCommentResponseDto(comment);
    }

    @DeleteMapping("/{id}")
    @Operation(description = "Delete comment by id")
    public void delete(@PathVariable Long id) {
        commentService.deleteComment(id);
    }

    @GetMapping
    @Operation(description = "Receive comment by id")
    public CommentResponseDto get(@RequestParam Long id) {
        return commentMapper.toCommentResponseDto(commentService.getComment(id));
    }

    @GetMapping("/filterByPost")
    @Operation(description = "Receive comments by post")
    public PageResponse<CommentResponseDto> findCommentsByPost(@RequestBody @Valid CommentByPostRequestDto filter) {
        var result = commentService.getCommentsByPost(filter.getPageNumber(), filter.getPageSize(), filter.getPostId());

        var response = new PageResponse<CommentResponseDto>();
        response.setPageSize(result.getSize());
        response.setPageNumber(result.getNumber());
        response.setTotalPages(result.getTotalPages());
        response.setTotalSize(result.getTotalElements());
        response.setContent(result.getContent().stream().map(commentMapper::toCommentResponseDto).toList());

        return response;
    }
}