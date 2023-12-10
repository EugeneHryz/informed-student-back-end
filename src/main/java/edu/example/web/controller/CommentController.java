package edu.example.web.controller;

import edu.example.dto.PageResponse;
import edu.example.dto.comment.CommentResponseDto;
import edu.example.dto.comment.CreateCommentReplyRequestDto;
import edu.example.dto.comment.CreateCommentRequestDto;
import edu.example.mapper.CommentMapper;
import edu.example.model.Comment;
import edu.example.service.CommentService;
import edu.example.web.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @ApiResponse(responseCode = "403", description = "Insufficient rights / unauthorized"),
            @ApiResponse(responseCode = "400", description = "Parsing / validation error")
    })
    public CommentResponseDto create(@RequestBody @Valid CreateCommentRequestDto createCommentRequestDto,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Comment comment = commentService.createComment(createCommentRequestDto,
                userDetails.getUser());
        return commentMapper.toCommentResponseDto(comment);
    }

    @PostMapping("/reply")
    @Operation(description = "Add reply to a comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Added successfully"),
            @ApiResponse(responseCode = "404", description = "Comment not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights / unauthorized"),
            @ApiResponse(responseCode = "400", description = "Parsing / validation error")
    })
    public CommentResponseDto addReplyToComment(@RequestBody @Valid CreateCommentReplyRequestDto dto,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Comment reply = commentService.addReplyToComment(dto, userDetails.getUser());
        return commentMapper.toCommentResponseDto(reply);
    }

    @GetMapping("/reply")
    @Operation(description = "Receive replies to a comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Сomment not found")
    })
    public PageResponse<CommentResponseDto> getCommentReplies(@RequestParam Long commentId,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "5") int size) {
        var result = commentService.getCommentReplies(commentId, page, size);
        return PageResponse.of(result, commentMapper::toCommentResponseDto);
    }

    @PreAuthorize("hasAuthority('MODERATOR') || @commentSecurity.isAllowedToModifyComment(authentication, #id)")
    @DeleteMapping("/{id}")
    @Operation(description = "Delete comment by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Comment not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights / unauthorized"),
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
        Comment comment = commentService.getComment(id);
        Integer numberOfReplies = commentService.countNumberOfReplies(comment.getId());
        return commentMapper.toCommentResponseDto(comment, numberOfReplies);
    }

    @GetMapping("/filterByPost")
    @Operation(description = "Receive comments by post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    public PageResponse<CommentResponseDto> getCommentsByPost(@RequestParam Long postId,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "5") int size) {
        var result = commentService.getCommentsByPost(page, size, postId);
        return PageResponse.of(result, c -> {
            Integer numberOfReplies = commentService.countNumberOfReplies(c.getId());
            return commentMapper.toCommentResponseDto(c, numberOfReplies);
        });
    }
}