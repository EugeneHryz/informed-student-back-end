package edu.example.service;

import edu.example.exception.EntityNotFoundException;
import edu.example.model.FileModel;
import edu.example.model.Post;
import edu.example.model.User;
import edu.example.repository.PostRepository;
import edu.example.repository.exception.FileWriteException;
import edu.example.util.AllowedFileExtension;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final FolderService folderService;
    private final FileStorageService fileStorageService;

    /**
     * Get posts, sorted by creation time. If folderId is null, then we will get news posts. Otherwise -
     * study materials posts
     * @param pageNumber page number, starting with 0
     * @param pageSize size of elements on the page
     * @param folderId id of a folder (null to get news posts)
     * @return Page object with information such as total number of posts, total number of pages, etc.
     */
    public Page<Post> getPosts(int pageNumber, int pageSize, Long folderId) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(
                Sort.Order.desc("createdAt")
        ));

        if (Objects.nonNull(folderId)) {
            return postRepository.findByFolderId(folderId, pageable);
        }
        return postRepository.findByFolderIsNull(pageable);
    }

    /**
     * Creates a post and saves files to object storage. File representation in the database (FileModel)
     * is linked to a post. Post can be linked to a folder (study materials post) or it can be a standalone post (news post).
     * @param folderId id of a folder in which post is created
     * @param author author who creates the post
     * @param text text of the post
     * @param files files to upload and associate with a post
     * @return post object
     */
    public Post createPost(Long folderId, User author, String text, List<MultipartFile> files) {
        return createPostWithFiles(folderId, author, text, files);
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post not found"));
    }

    public Post createPost(Long folderId, String text, User author) {
        var post = new Post();
        post.setFolder(folderService.getFolder(folderId));
        post.setText(text);
        post.setUser(author);
        return postRepository.save(post);
    }

    public Post updatePost(Long id, Long folder, String text) {
        var post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        post.setFolder(folderService.getFolder(folder));
        post.setText(text);
        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post not found"));
        postRepository.deleteById(id);
    }

    private Post createPostWithFiles(Long folderId, User author, String text, List<MultipartFile> files) {
        var post = new Post();
        if (folderId != null) {
            post.setFolder(folderService.getFolder(folderId));
        }
        post.setText(text);
        post.setUser(author);

        List<FileModel> fileModels = new ArrayList<>();
        if (nonNull(files) && !files.isEmpty()) {
            for (var file : files) {
                try {
                    AllowedFileExtension.caseIgnoreValueOf(FilenameUtils.getExtension(file.getOriginalFilename()));
                } catch (Exception e) {
                    throw new ConstraintViolationException(String.format("File extension %s not allowed",
                            FilenameUtils.getExtension(file.getOriginalFilename())), null);
                }
            }
            try {
                var savedFiles = fileStorageService.save(files);
                for (var savedFile : savedFiles) {
                    var file = new FileModel();
                    file.setPost(post);
                    file.setOriginalName(savedFile.getOriginalName());
                    file.setSavedByName(savedFile.getSavedFilename());
                    fileModels.add(file);
                }
            } catch (FileWriteException e) {
                throw new RuntimeException(e);
            }
        }
        post.setFiles(fileModels);
        try {
            post = postRepository.save(post);
        } catch (Throwable e) {
            post.getFiles().forEach(it -> {
                try {
                    fileStorageService.delete(it.getSavedByName());
                } catch (FileWriteException ignored) {}
            });
        }
        return post;
    }
}