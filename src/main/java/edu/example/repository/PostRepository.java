package edu.example.repository;

import edu.example.model.Folder;
import edu.example.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>{

    List<Post> getPostsByFolderOrderByCreatedAt(Folder folder);

    Page<Post> findByFolderId(Long folderId, Pageable pageable);
}
