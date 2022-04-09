package com.hotnerds.post.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.validation.constraints.Min;

@Getter
public class PageInfo {

    @Min(0)
    private int page;

    @JsonProperty("size")
    @Min(1)
    private int pageSize;

    @Builder
    public PageInfo(int page, int size) {
        this.page = page;
        this.pageSize = size;
    }

    public Pageable toPageable() {
        return PageRequest.of(page, pageSize);
    }
}