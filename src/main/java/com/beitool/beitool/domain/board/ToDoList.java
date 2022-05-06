package com.beitool.beitool.domain.board;

import com.beitool.beitool.domain.Member;
import com.beitool.beitool.domain.Store;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ToDoList 게시판 엔티티
 * 자영업자가 종업원에게 업무를 하달하고, 종업원은 완료 여부를 체크할 수 있다.
 * @author Chanos
 * @since 2022-05-04
 */
@Entity @DiscriminatorValue("ToDoList")
@Getter @Table(name="todolist_board")
@NoArgsConstructor
public class ToDoList extends BoardDomain{

    public ToDoList(String authorName, String content, LocalDateTime createdDate,
                Member author, Store store, String title, LocalDate jobDate, Member employee) {
        super(author, store, title, createdDate);
        this.authorName = authorName;
        this.content = content;
        this.jobDate = jobDate;
        this.employee = employee;
        this.isClear = false;
    }

    //업무완료 표시를 위한 메소드
    public void clearJob() {
        this.isClear = !this.isClear;
    }

    @Id
    @GeneratedValue
    @Column(name="announce_post_id")
    private Long id;

    @Column(name="author_name")
    private String authorName;

    private String content;

    @Column(name="is_clear")
    private boolean isClear;

    @ManyToOne(fetch=FetchType.LAZY)
    private Member employee;

    @Column(name="job_date")
    @JsonDeserialize(using= LocalDateDeserializer.class)
    @JsonSerialize(using= LocalDateSerializer.class)
    private LocalDate jobDate;
}
