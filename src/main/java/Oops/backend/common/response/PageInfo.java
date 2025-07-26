package Oops.backend.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageInfo {
    private int page;
    private int size;
    private boolean hasNext;
    private long totalElements;
    private int totalPages;
}