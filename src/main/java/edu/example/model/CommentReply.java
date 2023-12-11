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
    @Column(name = "reply_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "reply_id")
    private Comment reply;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

}
