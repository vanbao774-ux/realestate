package com.example.realestate.catalog;

import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {

    private final PropertyTypeRepository propertyTypeRepository;
    private final OrientationRepository orientationRepository;
    private final FurnishingLevelRepository furnishingLevelRepository;
    private final AmenityRepository amenityRepository;
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;

    public CatalogService(
        PropertyTypeRepository propertyTypeRepository,
        OrientationRepository orientationRepository,
        FurnishingLevelRepository furnishingLevelRepository,
        AmenityRepository amenityRepository,
        ProvinceRepository provinceRepository,
        DistrictRepository districtRepository,
        WardRepository wardRepository
    ) {
        this.propertyTypeRepository = propertyTypeRepository;
        this.orientationRepository = orientationRepository;
        this.furnishingLevelRepository = furnishingLevelRepository;
        this.amenityRepository = amenityRepository;
        this.provinceRepository = provinceRepository;
        this.districtRepository = districtRepository;
        this.wardRepository = wardRepository;
    }

    @Cacheable("propertyTypes")
    public List<PropertyType> propertyTypes() {
        return propertyTypeRepository.findAll();
    }

    @Cacheable("orientations")
    public List<Orientation> orientations() {
        return orientationRepository.findAll();
    }

    @Cacheable("furnishingLevels")
    public List<FurnishingLevel> furnishingLevels() {
        return furnishingLevelRepository.findAll();
    }

    @Cacheable("amenities")
    public List<Amenity> amenities() {
        return amenityRepository.findAll();
    }

    @Cacheable("provinces")
    public List<Province> provinces() {
        return provinceRepository.findAll();
    }

    public List<District> districts(Long provinceId) {
        return districtRepository.findByProvinceId(provinceId);
    }

    public List<Ward> wards(Long districtId) {
        return wardRepository.findByDistrictId(districtId);
    }
}
