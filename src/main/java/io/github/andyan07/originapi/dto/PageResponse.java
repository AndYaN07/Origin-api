package io.github.andyan07.originapi.dto;

import java.util.List;
import org.springframework.data.domain.Page;

/**
 * Página de resultados con un formato JSON estable.
 * Se evita devolver directamente Page de Spring, cuyo JSON no es un contrato estable.
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
