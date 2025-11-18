package com.example.realestate.catalog;

import com.example.realestate.common.ResourceNotFoundException;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectRepository projectRepository;

    public ProjectController(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
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

    @GetMapping("/{id}")
    public Project detail(@PathVariable Long id) {
        return projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }
}
