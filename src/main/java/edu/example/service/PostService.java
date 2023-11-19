package edu.example.service;

import edu.example.exception.EntityNotFoundException;
import edu.example.model.FileModel;
import edu.example.model.Post;
import edu.example.repository.PostRepository;
import edu.example.repository.exception.FileWriteException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;


@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final FolderService folderService;
    private final FileStorageService fileStorageService;

    public Page<Post> getPostsByFolder(int pageNumber, int pageSize, long folderId) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(
                Sort.Order.desc("createdAt")
        ));

        return postRepository.findByFolderId(folderId, pageable);
    }

    public Post createPostWithFiles(Long folderId, String text, List<MultipartFile> files) {
        var post = new Post();
        post.setFolder(folderService.getFolder(folderId));
        post.setText(text);
        List<FileModel> fileModels = new ArrayList<>();
        if (nonNull(files) && !files.isEmpty()) {
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

    public Post getPost(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post not found"));
    }

    public Post createPost(Long folderId, String text) {
        var post = new Post();
        post.setFolder(folderService.getFolder(folderId));
        post.setText(text);
        return postRepository.save(post);
    }

    public Post updatePost(Long id, Long folder, String text) {
        var post = new Post();
        post.setId(id);
        post.setFolder(folderService.getFolder(folder));
        post.setText(text);
        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post not found"));
        postRepository.deleteById(id);
    }
}