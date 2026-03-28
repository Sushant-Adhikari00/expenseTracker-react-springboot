package com.expensetracker.expenseTracker.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageResponse<T> {
    private List<T> content;
    private int page;           // current page (0-based)
    private int size;           // items per page
    private long totalElements; // total records in DB
    private int totalPages;     // total pages available
    private boolean first;      // is this the first page?
    private boolean last;       // is this the last page?
}