package it.unical.progettoweb.service;

import it.unical.progettoweb.dao.impl.*;
import it.unical.progettoweb.dto.request.*;
import it.unical.progettoweb.dto.response.*;
import it.unical.progettoweb.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RealEstateService {

    private final ApartmentDao apartmentDao;
    private final VillaDao villaDao;
    private final GarageDao garageDao;
    private final BuildingLotDao buildingLotDao;
    private final NonBuildingLotDao nonBuildingLotDao;
    private final RealEstateDaoImpl realEstateDao;
    private final GeocodingService geocodingService;
    private final Random random = new Random();

    public RealEstateDto save(RealEstateRequest dto) {
        return switch (dto) {
            case ApartmentRequest d -> {
                Apartment e = new Apartment();
                mapCommon(e, d);
                enrichWithCoordinates(d, e);
                e.setFloor(d.getFloor());
                e.setHasElevator(d.getHasElevator());
                yield toDto(apartmentDao.save(e));
            }
            case VillaRequest d -> {
                Villa e = new Villa();
                mapCommon(e, d);
                enrichWithCoordinates(d, e);
                e.setHasGarden(d.getHasGarden());
                e.setHasPool(d.getHasPool());
                e.setNumberOfFloors(d.getNumberOfFloors());
                yield toDto(villaDao.save(e));
            }
            case GarageRequest d -> {
                Garage e = new Garage();
                mapCommon(e, d);
                enrichWithCoordinates(d, e);
                e.setWidth(d.getWidth());
                e.setHeight(d.getHeight());
                e.setIsElectric(d.getIsElectric());
                yield toDto(garageDao.save(e));
            }
            case BuildingLotRequest d -> {
                BuildingLot e = new BuildingLot();
                mapCommon(e, d);
                enrichWithCoordinates(d, e);
                e.setCubature(d.getCubature());
                e.setPlannedUse(d.getPlannedUse());
                yield toDto(buildingLotDao.save(e));
            }
            case NonBuildingLotRequest d -> {
                NonBuildingLot e = new NonBuildingLot();
                mapCommon(e, d);
                enrichWithCoordinates(d, e);
                e.setCropType(d.getCropType());
                yield toDto(nonBuildingLotDao.save(e));
            }
            default -> throw new IllegalArgumentException("Tipo non supportato");
        };
    }

    public List<Object> findAll() {
        List<RealEstate> realEstates = realEstateDao.findAll();
        List<Object> results = new ArrayList<>();
        for (RealEstate e : realEstates) {
            results.add(toDto(e));
        }
        return results;
    }

    public Object update(int id, RealEstateRequest dto) {
        return switch (dto) {
            case ApartmentRequest d -> {
                Apartment e = new Apartment();
                mapCommon(e, d);
                enrichWithCoordinates(d, e);
                e.setId(id);
                e.setFloor(d.getFloor());
                e.setHasElevator(d.getHasElevator());
                yield toDto(apartmentDao.update(e));
            }
            case VillaRequest d -> {
                Villa e = new Villa();
                mapCommon(e, d);
                enrichWithCoordinates(d, e);
                e.setId(id);
                e.setHasGarden(d.getHasGarden());
                e.setHasPool(d.getHasPool());
                e.setNumberOfFloors(d.getNumberOfFloors());
                yield toDto(villaDao.update(e));
            }
            case GarageRequest d -> {
                Garage e = new Garage();
                mapCommon(e, d);
                enrichWithCoordinates(d, e);
                e.setId(id);
                e.setWidth(d.getWidth());
                e.setHeight(d.getHeight());
                e.setIsElectric(d.getIsElectric());
                yield toDto(garageDao.update(e));
            }
            case BuildingLotRequest d -> {
                BuildingLot e = new BuildingLot();
                mapCommon(e, d);
                enrichWithCoordinates(d, e);
                e.setId(id);
                e.setCubature(d.getCubature());
                e.setPlannedUse(d.getPlannedUse());
                yield toDto(buildingLotDao.update(e));
            }
            case NonBuildingLotRequest d -> {
                NonBuildingLot e = new NonBuildingLot();
                mapCommon(e, d);
                enrichWithCoordinates(d, e);
                e.setId(id);
                e.setCropType(d.getCropType());
                yield toDto(nonBuildingLotDao.update(e));
            }
            default -> throw new IllegalArgumentException("Tipo non supportato");
        };
    }

    public void delete(int id) {
        Optional<RealEstate> realEstate = realEstateDao.findById(id);
        if (realEstate.isEmpty())
            throw new RuntimeException("RealEstate non trovato");
        realEstateDao.delete(id);
    }

    public Object findById(int id) {
        RealEstate realEstate = realEstateDao.findById(id)
                .orElseThrow(() -> new RuntimeException("RealEstate non trovato"));
        return toDto(realEstate);
    }

    private void enrichWithCoordinates(RealEstateRequest dto, RealEstate entity) {
        double[] coords = geocodingService.geocodifica(
                dto.getStreet(),
                dto.getCivicNumber(),
                dto.getCity(),
                dto.getCap(),
                dto.getProvince()
        );
        entity.setLatit(coords[0]);
        entity.setLongit(coords[1]);
        entity.setAddress(
                dto.getStreet() + " " + dto.getCivicNumber() + ", " +
                        dto.getCap() + " " + dto.getCity() + " (" + dto.getProvince() + ")"
        );
    }

    private void mapCommon(RealEstate entity, RealEstateRequest dto) {
        entity.setId(generateRealEstateId());
        entity.setType(dto.getType());
        entity.setTitle(dto.getTitle());
        entity.setNumberOfRooms(dto.getNumberOfRooms());
        entity.setDescription(dto.getDescription());
        entity.setSquareMetres(dto.getSquareMetres());
    }

    private RealEstateDto toDto(RealEstate e) {
        return switch (e) {
            case Apartment a -> new ApartmentDto(
                    a.getId(), a.getTitle(), a.getNumberOfRooms(), a.getDescription(),
                    a.getSquareMetres(), a.getLatit(), a.getLongit(), a.getAddress(),
                    a.getCreatedAt(), a.getType(),
                    a.getFloor(), a.getHasElevator());

            case Villa v -> new VillaDto(
                    v.getId(), v.getTitle(), v.getNumberOfRooms(), v.getDescription(),
                    v.getSquareMetres(), v.getLatit(), v.getLongit(), v.getAddress(),
                    v.getCreatedAt(), v.getType(),
                    v.getHasGarden(), v.getHasPool(), v.getNumberOfFloors());

            case Garage g -> new GarageDto(
                    g.getId(), g.getTitle(), g.getNumberOfRooms(), g.getDescription(),
                    g.getSquareMetres(), g.getLatit(), g.getLongit(), g.getAddress(),
                    g.getCreatedAt(), g.getType(),
                    g.getWidth(), g.getHeight(), g.getIsElectric());

            case BuildingLot b -> new BuildingLotDto(
                    b.getId(), b.getTitle(), b.getNumberOfRooms(), b.getDescription(),
                    b.getSquareMetres(), b.getLatit(), b.getLongit(), b.getAddress(),
                    b.getCreatedAt(), b.getType(),
                    b.getCubature(), b.getPlannedUse());

            case NonBuildingLot n -> new NonBuildingLotDto(
                    n.getId(), n.getTitle(), n.getNumberOfRooms(), n.getDescription(),
                    n.getSquareMetres(), n.getLatit(), n.getLongit(), n.getAddress(),
                    n.getCreatedAt(), n.getType(),
                    n.getCropType());

            default -> new RealEstateDto(
                    e.getId(), e.getTitle(), e.getNumberOfRooms(), e.getDescription(),
                    e.getSquareMetres(), e.getLatit(), e.getLongit(), e.getAddress(),
                    e.getCreatedAt(), e.getType());
        };
    }

    private int generateRealEstateId() {
        int id;
        do {
            id = random.nextInt(89999) + 10000;
        } while (realEstateDao.findById(id).isPresent());
        return id;
    }
}