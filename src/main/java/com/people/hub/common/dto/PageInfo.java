package com.people.hub.common.dto;

import lombok.Data;

@Data
public class PageInfo {
    private int pageNumber;
    private int pageSize;
    private long totalRecords;
    private int totalPages;

    public  PageInfo(int pageNumber, int pageSize, long totalRecords) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalRecords = totalRecords;
        this.totalPages = pageSize==0 ? 0 : (int) Math.ceil((double) totalRecords / pageSize) -1;
    }
}
