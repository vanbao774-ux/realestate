package com.example.realestate.admin;

import com.example.realestate.catalog.Project;
import com.example.realestate.catalog.ProjectRepository;
import com.example.realestate.catalog.ProjectSpecifications;
import com.example.realestate.catalog.ProvinceRepository;
import com.example.realestate.catalog.DistrictRepository;
import com.example.realestate.catalog.WardRepository;
import com.example.realestate.catalog.dto.ProjectRequest;
import com.example.realestate.common.ResourceNotFoundException;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/projects")
public class AdminProjectController {

    private final ProjectRepository projectRepository;
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;

    public AdminProjectController(
        ProjectRepository projectRepository,
        ProvinceRepository provinceRepository,
        DistrictRepository districtRepository,
        WardRepository wardRepository
    ) {
        this.projectRepository = projectRepository;
        this.provinceRepository = provinceRepository;
        this.districtRepository = districtRepository;
        this.wardRepository = wardRepository;
    }

    @GetMapping
    public Page<Project> list(
        @RequestParam Optional<Long> provinceId,
        @RequestParam Optional<Long> districtId,
        @RequestParam Optional<Long> wardId,
        @RequestParam Optional<String> status,
        @RequestParam Optional<BigDecimal> priceFrom,
        @RequestParam Optional<BigDecimal> priceTo,
        @RequestParam Optional<String> q,
        Pageable pageable
    ) {
        return projectRepository.findAll(
            ProjectSpecifications.filter(provinceId, districtId, wardId, status, priceFrom, priceTo, q),
            pageable
        );
    }

    @PostMapping
    public Project create(@Valid @RequestBody ProjectRequest request) {
        Project project = new Project();
        apply(project, request);
        return projectRepository.save(project);
    }

    @PutMapping("/{id}")
    public Project update(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        apply(project, request);
        return projectRepository.save(project);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        projectRepository.deleteById(id);
    }

    private void apply(Project project, ProjectRequest request) {
        project.setName(request.name());
        project.setInvestor(request.investor());
        project.setProvince(request.provinceId() != null ? provinceRepository.findById(request.provinceId())
            .orElseThrow(() -> new ResourceNotFoundException("Province not found")) : null);
        project.setDistrict(request.districtId() != null ? districtRepository.findById(request.districtId())
            .orElseThrow(() -> new ResourceNotFoundException("District not found")) : null);
        project.setWard(request.wardId() != null ? wardRepository.findById(request.wardId())
            .orElseThrow(() -> new ResourceNotFoundException("Ward not found")) : null);
        project.setAddress(request.address());
        project.setPriceFrom(request.priceFrom());
        project.setPriceTo(request.priceTo());
        project.setStatus(request.status());
        project.setLat(request.lat());
        project.setLng(request.lng());
        project.setDescription(request.description());
        project.setCoverUrl(request.coverUrl());
    }
}
