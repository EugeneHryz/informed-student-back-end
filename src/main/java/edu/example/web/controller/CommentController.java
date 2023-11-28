package edu.example.web.controller;

import edu.example.dto.PageResponse;
import edu.example.dto.comment.CommentResponseDto;
import edu.example.dto.comment.CreateCommentRequestDto;
import edu.example.mapper.CommentMapper;
import edu.example.model.Comment;
import edu.example.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
@Tag(name = "Comment", description = "Work with comment")
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @PostMapping
    @Operation(description = "Create comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights / unauthorised"),
            @ApiResponse(responseCode = "400", description = "Parsing / validation error")
    })
    public CommentResponseDto create(@RequestBody @Valid CreateCommentRequestDto createCommentRequestDto) {
        Comment comment = commentService.createComment(
                createCommentRequestDto.getPostId(),
                createCommentRequestDto.getText()
        );
        return commentMapper.toCommentResponseDto(comment);
    }

    @DeleteMapping("/{id}")
    @Operation(description = "Delete comment by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Comment not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights / unauthorised"),
    })
    public void delete(@PathVariable Long id) {
        commentService.deleteComment(id);
    }

    @GetMapping
    @Operation(description = "Receive comment by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    public CommentResponseDto get(@RequestParam Long id) {
        return commentMapper.toCommentResponseDto(commentService.getComment(id));
    }

    @GetMapping("/filterByPost")
    @Operation(description = "Receive comments by post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    public PageResponse<CommentResponseDto> findCommentsByPost(@RequestParam Long postId,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "5") int size) {
        var result = commentService.getCommentsByPost(page, size, postId);

        var response = new PageResponse<CommentResponseDto>();
        response.setPageSize(result.getSize());
        response.setPageNumber(result.getNumber());
        response.setTotalPages(result.getTotalPages());
        response.setTotalSize(result.getTotalElements());
        response.setContent(result.getContent().stream().map(commentMapper::toCommentResponseDto).toList());

        return response;
    }
}