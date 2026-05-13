package it.unical.progettoweb.dao;

import it.unical.progettoweb.model.RealEstate;
import java.util.List;

public interface RealEstateDao<T extends RealEstate> extends Dao<T, Integer> {
    List<T> findAllOrderBySquareMetresAsc();
    List<T> findAllOrderBySquareMetresDesc();
    List<T> findAllOrderByPriceAsc();
    List<T> findAllOrderByPriceDesc();
}