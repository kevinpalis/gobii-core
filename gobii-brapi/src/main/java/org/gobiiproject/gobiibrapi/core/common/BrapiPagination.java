package org.gobiiproject.gobiibrapi.core.common;

/**
 * Created by Phil on 9/6/2016.
 */
public class BrapiPagination {


    public BrapiPagination() {}

    public BrapiPagination(
                           Integer totalCount,
                           Integer pageSize,
                           Integer totalPages,
                           Integer currentPage
    ) {
        this.totalCount= totalCount;
        this.pageSize= pageSize;
        this.totalPages= totalPages;
        this.currentPage= currentPage;

    }

    private Integer totalCount;
    private Integer pageSize;
    private Integer totalPages;
    private Integer currentPage;


    public Integer getTotalCount() {
        return totalCount;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }
}
