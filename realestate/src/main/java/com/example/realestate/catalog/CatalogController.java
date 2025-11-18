package com.example.realestate.catalog;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/property-types")
    public List<PropertyType> propertyTypes() {
        return catalogService.propertyTypes();
    }

    @GetMapping("/orientations")
    public List<Orientation> orientations() {
        return catalogService.orientations();
    }

    @GetMapping("/furnishing-levels")
    public List<FurnishingLevel> furnishingLevels() {
        return catalogService.furnishingLevels();
    }

    @GetMapping("/amenities")
    public List<Amenity> amenities() {
        return catalogService.amenities();
    }

    @GetMapping("/provinces")
    public List<Province> provinces() {
        return catalogService.provinces();
    }

    @GetMapping("/provinces/{provinceId}/districts")
    public List<District> districts(@PathVariable Long provinceId) {
        return catalogService.districts(provinceId);
    }

    @GetMapping("/districts/{districtId}/wards")
    public List<Ward> wards(@PathVariable Long districtId) {
        return catalogService.wards(districtId);
    }
}
