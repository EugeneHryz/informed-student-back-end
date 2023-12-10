package edu.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comment_reply")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentReply {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_reply_seq")
    @SequenceGenerator(name = "comment_reply_seq", sequenceName = "comment_reply_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reply_id", nullable = false)
    private Comment reply;

}
