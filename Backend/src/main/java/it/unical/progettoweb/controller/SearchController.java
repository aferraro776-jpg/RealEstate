package it.unical.progettoweb.controller;

import it.unical.progettoweb.dao.SearchDao;
import it.unical.progettoweb.dto.PostSummaryDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
@RequestMapping("/api/search")

public class SearchController {

    private final SearchDao searchDao;

    public SearchController(SearchDao searchDao) {
        this.searchDao = searchDao;
    }

    @GetMapping
    public ResponseEntity<List<PostSummaryDto>> search(
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) String realEstateType,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false, defaultValue = "price") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDir
    ) {
        List<PostSummaryDto> results = searchDao.search(
                transactionType, realEstateType, minPrice, maxPrice, sortBy, sortDir
        );
        return ResponseEntity.ok(results);
    }
}
