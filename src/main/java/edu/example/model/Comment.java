package edu.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.sql.Timestamp;

@Entity
@Table(name = "comment")
@Audited
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_seq")
    @SequenceGenerator(name = "comment_seq", sequenceName = "comment_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Post post;

    @Column(name = "creation_time")
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(name = "update_time")
    @UpdateTimestamp
    private Timestamp updatedAt;

    @Column
    private String text;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private User user;

    @Column(name = "anonymous", updatable = false)
    private boolean isAnonymous;
}
