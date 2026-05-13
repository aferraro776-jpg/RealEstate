package it.unical.progettoweb.dao;

import it.unical.progettoweb.dto.PostSummaryDto;

import java.util.List;

public interface SearchDao {
    List <PostSummaryDto> search(
            String transactionType,
            String realEstateType,
            Double minPrice,
            Double maxPrice,
            String sortBy,
            String sortDir
    );
}
