package com.example.realestate.posts;

import com.example.realestate.catalog.Amenity;
import com.example.realestate.catalog.AmenityRepository;
import com.example.realestate.catalog.DistrictRepository;
import com.example.realestate.catalog.FurnishingLevel;
import com.example.realestate.catalog.FurnishingLevelRepository;
import com.example.realestate.catalog.Orientation;
import com.example.realestate.catalog.OrientationRepository;
import com.example.realestate.catalog.PropertyType;
import com.example.realestate.catalog.PropertyTypeRepository;
import com.example.realestate.catalog.ProvinceRepository;
import com.example.realestate.catalog.WardRepository;
import com.example.realestate.common.ResourceNotFoundException;
import com.example.realestate.posts.dto.PostFilter;
import com.example.realestate.posts.dto.PostRequest;
import com.example.realestate.posts.dto.PostResponse;
import com.example.realestate.posts.dto.PostSummary;
import com.example.realestate.users.User;
import com.example.realestate.users.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PropertyTypeRepository propertyTypeRepository;
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;
    private final OrientationRepository orientationRepository;
    private final FurnishingLevelRepository furnishingLevelRepository;
    private final AmenityRepository amenityRepository;
    private final PostViewService postViewService;

    public PostService(
        PostRepository postRepository,
        UserRepository userRepository,
        PropertyTypeRepository propertyTypeRepository,
        ProvinceRepository provinceRepository,
        DistrictRepository districtRepository,
        WardRepository wardRepository,
        OrientationRepository orientationRepository,
        FurnishingLevelRepository furnishingLevelRepository,
        AmenityRepository amenityRepository,
        PostViewService postViewService
    ) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.propertyTypeRepository = propertyTypeRepository;
        this.provinceRepository = provinceRepository;
        this.districtRepository = districtRepository;
        this.wardRepository = wardRepository;
        this.orientationRepository = orientationRepository;
        this.furnishingLevelRepository = furnishingLevelRepository;
        this.amenityRepository = amenityRepository;
        this.postViewService = postViewService;
    }

    @Transactional
    public PostResponse create(PostRequest request) {
        User owner = userRepository.findById(request.ownerId())
            .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
        PropertyType propertyType = propertyTypeRepository.findById(request.propertyTypeId())
            .orElseThrow(() -> new ResourceNotFoundException("Property type not found"));

        Post post = new Post();
        post.setOwner(owner);
        post.setTitle(request.title());
        post.setDescription(request.description());
        post.setPurpose(PostPurpose.valueOf(request.purpose().toUpperCase()));
        post.setPropertyType(propertyType);
        post.setProvince(request.provinceId() != null ? provinceRepository.findById(request.provinceId())
            .orElseThrow(() -> new ResourceNotFoundException("Province not found")) : null);
        post.setDistrict(request.districtId() != null ? districtRepository.findById(request.districtId())
            .orElseThrow(() -> new ResourceNotFoundException("District not found")) : null);
        post.setWard(request.wardId() != null ? wardRepository.findById(request.wardId())
            .orElseThrow(() -> new ResourceNotFoundException("Ward not found")) : null);
        post.setAddress(request.address());
        post.setPrice(request.price());
        post.setArea(request.area());
        post.setBedrooms(request.bedrooms());
        post.setBathrooms(request.bathrooms());
        post.setOrientation(request.orientationId() != null ? findOrientation(request.orientationId()) : null);
        post.setFurnishingLevel(request.furnishingLevelId() != null ? findFurnishing(request.furnishingLevelId()) : null);
        post.setLegalStatus(request.legalStatus());
        post.setLatitude(request.latitude());
        post.setLongitude(request.longitude());
        post.setStatus(PostStatus.PENDING);

        attachAmenities(post, request.amenityIds());

        Post saved = postRepository.save(post);
        return PostMapper.toResponse(saved);
    }

    @Transactional
    public PostResponse update(Long id, PostRequest request) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getOwner().getId().equals(request.ownerId())) {
            throw new IllegalArgumentException("Owner mismatch");
        }

        PropertyType propertyType = propertyTypeRepository.findById(request.propertyTypeId())
            .orElseThrow(() -> new ResourceNotFoundException("Property type not found"));

        post.setTitle(request.title());
        post.setDescription(request.description());
        post.setPurpose(PostPurpose.valueOf(request.purpose().toUpperCase()));
        post.setPropertyType(propertyType);
        post.setProvince(request.provinceId() != null ? provinceRepository.findById(request.provinceId())
            .orElseThrow(() -> new ResourceNotFoundException("Province not found")) : null);
        post.setDistrict(request.districtId() != null ? districtRepository.findById(request.districtId())
            .orElseThrow(() -> new ResourceNotFoundException("District not found")) : null);
        post.setWard(request.wardId() != null ? wardRepository.findById(request.wardId())
            .orElseThrow(() -> new ResourceNotFoundException("Ward not found")) : null);
        post.setAddress(request.address());
        post.setPrice(request.price());
        post.setArea(request.area());
        post.setBedrooms(request.bedrooms());
        post.setBathrooms(request.bathrooms());
        post.setOrientation(request.orientationId() != null ? findOrientation(request.orientationId()) : null);
        post.setFurnishingLevel(request.furnishingLevelId() != null ? findFurnishing(request.furnishingLevelId()) : null);
        post.setLegalStatus(request.legalStatus());
        post.setLatitude(request.latitude());
        post.setLongitude(request.longitude());

        attachAmenities(post, request.amenityIds());

        return PostMapper.toResponse(post);
    }

    @Transactional
    public void changeStatus(Long id, PostStatus status) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        post.setStatus(status);
    }

    @Transactional
    public void approvePost(Long id, java.time.Instant expiresAt, java.time.Instant featuredUntil) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        post.setStatus(PostStatus.APPROVED);
        post.setRejectReason(null);
        post.setExpiresAt(expiresAt);
        post.setFeaturedUntil(featuredUntil);
    }

    @Transactional
    public void rejectPost(Long id, String reason) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        post.setStatus(PostStatus.REJECTED);
        post.setRejectReason(reason);
    }

    public Page<PostSummary> search(PostFilter filter, Pageable pageable) {
        return postRepository.findAll(PostSpecifications.withFilter(filter), pageable)
            .map(PostMapper::toSummary);
    }

    @Transactional
    public PostResponse getPost(Long id, User viewer, String ipAddress) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        postViewService.registerView(post, viewer, ipAddress);
        return PostMapper.toResponse(post);
    }

    private Orientation findOrientation(Long id) {
        return orientationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Orientation not found"));
    }

    private FurnishingLevel findFurnishing(Long id) {
        return furnishingLevelRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Furnishing level not found"));
    }

    private void attachAmenities(Post post, Set<Long> amenityIds) {
        if (amenityIds == null || amenityIds.isEmpty()) {
            post.getAmenities().clear();
            return;
        }
        List<Amenity> amenities = amenityRepository.findAllById(amenityIds);
        Map<Long, Amenity> amenityMap = amenities.stream().collect(Collectors.toMap(Amenity::getId, a -> a));
        if (amenityMap.size() != amenityIds.size()) {
            throw new ResourceNotFoundException("One or more amenities not found");
        }
        post.getAmenities().clear();
        post.getAmenities().addAll(amenities);
    }
}
