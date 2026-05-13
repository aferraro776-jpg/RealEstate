package it.unical.progettoweb.mapper;

import it.unical.progettoweb.model.*;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RealEstateRowMapper implements RowMapper<RealEstate> {

    @Override
    public RealEstate mapRow(ResultSet rs, int rowNum) throws SQLException {
        String type = rs.getString("type");
        return switch (type) {
            case "APARTMENT"        -> mapApartment(rs);
            case "VILLA"            -> mapVilla(rs);
            case "GARAGE"           -> mapGarage(rs);
            case "BUILDING_LOT"     -> mapBuildingLot(rs);
            case "NON_BUILDING_LOT" -> mapNonBuildingLot(rs);
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        };
    }

    public void mapCommon(RealEstate r, ResultSet rs) throws SQLException {
        r.setId(rs.getInt("id"));
        r.setTitle(rs.getString("title"));
        r.setDescription(rs.getString("description"));
        r.setSquareMetres(rs.getDouble("square_metres"));
        r.setLatit(rs.getDouble("latit"));
        r.setLongit(rs.getDouble("longit"));
        r.setAddress(rs.getString("address"));
        r.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        r.setType(rs.getString("type"));
        r.setNumberOfRooms(rs.getInt("number_of_rooms"));
    }

    private Apartment mapApartment(ResultSet rs) throws SQLException {
        Apartment a = new Apartment();
        mapCommon(a, rs);
        a.setFloor(rs.getInt("floor"));
        a.setHasElevator(rs.getBoolean("has_elevator"));
        return a;
    }

    private Villa mapVilla(ResultSet rs) throws SQLException {
        Villa v = new Villa();
        mapCommon(v, rs);
        v.setHasGarden(rs.getBoolean("has_garden"));
        v.setHasPool(rs.getBoolean("has_pool"));
        v.setNumberOfFloors(rs.getInt("number_of_floors"));
        return v;
    }

    private Garage mapGarage(ResultSet rs) throws SQLException {
        Garage g = new Garage();
        mapCommon(g, rs);
        g.setWidth(rs.getDouble("width"));
        g.setHeight(rs.getDouble("height"));
        g.setIsElectric(rs.getBoolean("is_electric"));
        return g;
    }

    private BuildingLot mapBuildingLot(ResultSet rs) throws SQLException {
        BuildingLot b = new BuildingLot();
        mapCommon(b, rs);
        b.setCubature(rs.getDouble("cubature"));
        b.setPlannedUse(rs.getString("land_use"));
        return b;
    }

    private NonBuildingLot mapNonBuildingLot(ResultSet rs) throws SQLException {
        NonBuildingLot n = new NonBuildingLot();
        mapCommon(n, rs);
        n.setCropType(rs.getString("crop_type"));
        return n;
    }
}