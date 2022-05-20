package com.beitool.beitool.api.service;

import com.beitool.beitool.domain.board.ToDoList;

import java.util.Comparator;

/**
 * ToDoList의 게시글 목록을 마감일로 정렬하기 위한 Comparator.
 * @author Chanos
 * @since 2022-05-18
 */
public class ToDoListComparator implements Comparator<ToDoList> {
    @Override
    public int compare(ToDoList t1, ToDoList t2) {
        return t1.getJobDate().compareTo(t2.getJobDate());
    }
}
