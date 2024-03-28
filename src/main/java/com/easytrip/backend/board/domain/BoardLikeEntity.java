package com.easytrip.backend.board.domain;
<<<<<<< cinna

=======
>>>>>>> main
import com.easytrip.backend.member.domain.MemberEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "post_like")
@Builder(toBuilder = true)
public class BoardLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardLikeId;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private BoardEntity boardId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity memberId;

}
