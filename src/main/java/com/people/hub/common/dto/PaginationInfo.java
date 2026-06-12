package com.people.hub.common.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Data;

@Data
public class PaginationInfo {
    @JsonSetter(nulls = Nulls.SKIP)
    private int page = 0;

    @JsonSetter(nulls = Nulls.SKIP)
    private int size = 10;

    @JsonSetter(nulls = Nulls.SKIP)
    private String sortField = "created_at";

    @JsonSetter(nulls = Nulls.SKIP)
    private String sortOrder = "desc";
}
