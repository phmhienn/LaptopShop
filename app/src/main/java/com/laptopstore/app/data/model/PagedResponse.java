package com.laptopstore.app.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PagedResponse<T> {
    
    @SerializedName("content")
    private List<T> content;
    
    @SerializedName("pageNo")
    private int pageNo;
    
    @SerializedName("pageSize")
    private int pageSize;
    
    @SerializedName("totalElements")
    private long totalElements;
    
    @SerializedName("totalPages")
    private int totalPages;
    
    @SerializedName("last")
    private boolean last;

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }
}
