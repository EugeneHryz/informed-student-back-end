package edu.example.controller;

import edu.example.dto.PageResponse;
import edu.example.dto.post.CreatePostRequestDto;
import edu.example.dto.post.PostByFolderRequestDto;
import edu.example.dto.post.PostResponseDto;
import edu.example.mapper.PostMapper;
import edu.example.model.Post;
import edu.example.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
    public PostResponseDto create(@RequestPart("post") @Valid CreatePostRequestDto createPostRequestDto,
                                  @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        Post post = postService.createPostWithFiles(
                createPostRequestDto.getFolderId(),
                createPostRequestDto.getText(),
                files
        );
        return postMapper.toPostResponseDto(post);
    }

    @DeleteMapping("/{id}")
    @Operation(description = "Delete post by id")
    public void delete(@PathVariable Long id) {
        postService.deletePost(id);
    }

    @GetMapping
    @Operation(description = "Receive post by id")
    public PostResponseDto get(@RequestParam Long id) {
        return postMapper.toPostResponseDto(postService.getPost(id));
    }

    @GetMapping("/filterByFolder")
    @Operation(description = "Receive posts by folder")
    public PageResponse<PostResponseDto> findPostsByCourse(@RequestBody @Valid PostByFolderRequestDto filter) {
        var result = postService.getPostsByFolder(filter.getPageNumber(), filter.getPageSize(), filter.getFolderId());

        var response = new PageResponse<PostResponseDto>();
        response.setPageSize(result.getSize());
        response.setPageNumber(result.getNumber());
        response.setTotalPages(result.getTotalPages());
        response.setTotalSize(result.getTotalElements());
        response.setContent(result.getContent().stream().map(postMapper::toPostResponseDto).toList());

        return response;
    }
}