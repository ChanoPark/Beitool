package com.beitool.beitool.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="announcement_board")
public class AnnouncementBoard {

    @Id @GeneratedValue
    @Column(name="post_id")
    private Long id;

    @JoinColumn(name="author_id")
    @ManyToOne(fetch=FetchType.LAZY)
    private Member member;

    @JoinColumn(name="store_id")
    @ManyToOne(fetch=FetchType.LAZY)
    private Store store;

    private String title;
    private String content;

    @Column(name="create_date")
    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    private LocalDateTime createdDate;
}
