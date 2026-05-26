package com.people.hub.common.utilities;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PageableUtils {

    public static Pageable getPageable(
            int page,
            int size,
            String sortField,
            String sortOrder,
            Collection<String> allowedSortFields){
        if (!allowedSortFields.contains(sortField)) {
            sortField = "created_at";
        }
        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        return PageRequest.of(page, size, sort);
    }
}
