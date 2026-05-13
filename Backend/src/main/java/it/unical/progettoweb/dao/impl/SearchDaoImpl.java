package it.unical.progettoweb.dao.impl;

import it.unical.progettoweb.dao.SearchDao;
import it.unical.progettoweb.dto.PostSummaryDto;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
@AllArgsConstructor
public class SearchDaoImpl implements SearchDao {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<PostSummaryDto> dtoRowMapper = (rs, rowNum) -> {
        PostSummaryDto dto = new PostSummaryDto();
        dto.setPostId(rs.getInt("postId"));
        dto.setTitle(rs.getString("title"));
        dto.setDescription(rs.getString("description"));
        dto.setCurrentPrice(rs.getDouble("currentPrice"));
        dto.setPreviousPrice(rs.getDouble("previousPrice"));
        dto.setTransactionType(rs.getString("transactionType"));
        dto.setAuction(rs.getBoolean("isAuction"));
        dto.setRealEstateId(rs.getInt("realEstateId"));
        dto.setRealEstateType(rs.getString("realEstateType"));
        dto.setSquareMeters(rs.getDouble("squareMeters"));
        dto.setAddress(rs.getString("address"));
        return dto;
    };

    @Override
    public List<PostSummaryDto> search(
            String transactionType,
            String realEstateType,
            Double minPrice,
            Double maxPrice,
            String sortBy,
            String sortDir) {

        StringBuilder sql = new StringBuilder("""
                SELECT
                    p.id                    AS "postId",
                    p.title                 AS "title",
                    p.description           AS "description",
                    p.current_price         AS "currentPrice",
                    p.previous_price        AS "previousPrice",
                    p.transaction_type      AS "transactionType",
                    p.is_auction            AS "isAuction",
                    r.id                    AS "realEstateId",
                    r.type                  AS "realEstateType",
                    r.square_metres         AS "squareMeters",
                    r.address               AS "address"
                FROM posts p
                JOIN real_estate r ON p.id_real_estate = r.id
                WHERE 1=1
                """);

        List<Object> params = new ArrayList<>();

        if (transactionType != null && !transactionType.isBlank()) {
            sql.append("AND p.transaction_type = ? ");
            params.add(transactionType);
        }

        if (realEstateType != null && !realEstateType.isBlank()) {
            sql.append("AND r.type = ? ");
            params.add(realEstateType);
        }

        if (minPrice != null) {
            sql.append("AND p.current_price >= ? ");
            params.add(minPrice);
        }

        if (maxPrice != null) {
            sql.append("AND p.current_price <= ? ");
            params.add(maxPrice);
        }

        String orderColumn = switch (sortBy != null ? sortBy.toLowerCase() : "") {
            case "squaremeters" -> "r.square_metres";
            case "title"        -> "p.title";
            default             -> "p.current_price";
        };

        String direction = "desc".equalsIgnoreCase(sortDir) ? "DESC" : "ASC";
        sql.append("ORDER BY ").append(orderColumn).append(" ").append(direction);

        return jdbcTemplate.query(sql.toString(), dtoRowMapper, params.toArray());
    }
}