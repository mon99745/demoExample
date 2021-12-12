package com.exam.demoExample.domain.board;

import com.exam.demoExample.domain.BaseTimeEntity;
import com.exam.demoExample.domain.reply.Reply;
import com.exam.demoExample.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;


@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob // 대용량 데이터를 저장할 때 사용
    // 내용은 summernote라는 라이브러리를 이용할 것이기 때문에 @Lob으로 설정
    private String content;

    private int count; //조회수

    @ManyToOne(fetch = FetchType.EAGER)
    // 게시글을 작성할 때 누가 작성했는지 알아야 하기 때문에 User 테이블과 조인
    // Java코드로 객체를 선언하게 되면 ORM문법으로 알아서 조인
    // id값이 서로 있으니까 id값으로 foreign키를 생성
    @JoinColumn(name = "userId") // foreign키의 컬럼명 설정
    private User user;

    // 글 수정 메소드
    // JPA에서 udpate를 진행할 때는 영속성 컨텍스트에 있는 값과 비교를 해서 변경된 값이 있으면 그 변경된 값만 update 시켜준다.
    // 이것을 변경감지라 하여 더치체킹이라 부른다.
    // 즉, Entity 객체의 값만 변경시켜주면 더티체킹이 일어납니다. (Update 쿼리문을 날릴 필요가 없다!)
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    //무한 참조 (댓글 기능)
    @OrderBy("id desc") // 댓글 작성시 최근 순으로 볼 수 있도록 설정
    // 무한 참조 발생 해결방법

    // Board 테이블에 댓글리스트를 추가하겠다는 의미
    // DB에는 하나의 raw 데이터에 하나의 값만 들어갈 수 있다.
    // 만약 여러 개의 데이터가 들어간다면 원자성이 깨진다.
    // 그래서 replyList는 DB에 FK로 생성되면 안되기 때문에 mappedBy를 사용한다.
    //mppedBy : 연관관계의 주인이 아니므로 DB의 FK가 아니다 라는 뜻
    //@OneToMany의 디폴트 fetch는 Lazy입니다. 이것을 Eager로 변경
    @OneToMany(mappedBy = "board", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JsonIgnoreProperties({"board", "user"}) //JsonIgnoreProperties를 걸어주면 reply 안에서 호출을 또 할게 될때 board는 getter 호출 무시
    private List<Reply> replyList;
}