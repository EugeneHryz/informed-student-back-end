package edu.example.web.controller;

import edu.example.dto.PageResponse;
import edu.example.dto.post.CreatePostRequestDto;
import edu.example.dto.post.PostResponseDto;
import edu.example.mapper.PostMapper;
import edu.example.model.Post;
import edu.example.service.PostService;
import edu.example.web.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
@Tag(name = "Post", description = "work with post")
public class PostController {

    private final PostService postService;
    private final PostMapper postMapper;

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(description = "Create post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created successfully"),
            @ApiResponse(responseCode = "404", description = "Folder not found"),
            @ApiResponse(responseCode = "400", description = "Parsing / validation error")
    })
    public PostResponseDto create(@RequestPart("post") @Valid CreatePostRequestDto createPostRequestDto,
                                  @RequestPart(value = "files", required = false) List<MultipartFile> files,
                                  @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Post post = postService.createPostWithFiles(
                createPostRequestDto.getFolderId(),
                userDetails.getUser(),
                createPostRequestDto.getText(),
                files);

        return postMapper.toPostResponseDto(post);
    }

    @PreAuthorize("hasAuthority('MODERATOR') || @postSecurity.isAllowedToModifyPost(authentication, #id)")
    @DeleteMapping("/{id}")
    @Operation(description = "Delete post by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights / unauthorized")
    })
    public void delete(@PathVariable Long id) {
        postService.deletePost(id);
    }

    @GetMapping
    @Operation(description = "Receive post by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    public PostResponseDto get(@RequestParam Long id) {
        return postMapper.toPostResponseDto(postService.getPost(id));
    }

    @GetMapping("/filterByFolder")
    @Operation(description = "Receive posts by folder")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Folder not found")
    })
    public PageResponse<PostResponseDto> findPostsByFolder(@RequestParam Long folderId,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "5") int size) {
        var result = postService.getPostsByFolder(page, size, folderId);

        var response = new PageResponse<PostResponseDto>();
        response.setPageSize(result.getSize());
        response.setPageNumber(result.getNumber());
        response.setTotalPages(result.getTotalPages());
        response.setTotalSize(result.getTotalElements());
        response.setContent(result.getContent().stream().map(postMapper::toPostResponseDto).toList());

        return response;
    }
}