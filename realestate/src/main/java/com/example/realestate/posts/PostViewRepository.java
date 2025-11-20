package com.example.realestate.posts;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.example.realestate.stats.DailyView;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostViewRepository extends JpaRepository<PostView, Long> {

    @Query("select pv from PostView pv where pv.post.id = :postId and ((:viewerId is null and pv.viewer is null) or pv.viewer.id = :viewerId) "
        + "and (:viewerIp is null or pv.viewerIp = :viewerIp) and function('date', pv.viewedAt) = function('date', :viewedAt)")
    Optional<PostView> findDailyView(
        @Param("postId") Long postId,
        @Param("viewerId") Long viewerId,
        @Param("viewerIp") String viewerIp,
        @Param("viewedAt") Instant viewedAt
    );

    @Query("select coalesce(sum(p.viewsTotal),0) from Post p where p.owner.id = :ownerId")
    Long sumViewsByOwner(@Param("ownerId") Long ownerId);

    @Query("select new com.example.realestate.stats.TopPostView(pv.post.id, pv.post.title, count(pv)) "
        + "from PostView pv where pv.post.owner.id = :ownerId and pv.viewedAt >= :since group by pv.post.id, pv.post.title order by count(pv) desc")
    List<com.example.realestate.stats.TopPostView> findTopPosts(
        @Param("ownerId") Long ownerId,
        @Param("since") Instant since,
        Pageable pageable
    );

    @Query("""
            select new com.example.realestate.stats.DailyView(
                cast(pv.viewedAt as date),
                count(pv)
            )
            from PostView pv
            where pv.post.owner.id = :ownerId
            and pv.viewedAt >= :since
            group by cast(pv.viewedAt as date)
            order by cast(pv.viewedAt as date)
            """)
    List<DailyView> aggregateViews(
            @Param("ownerId") Long ownerId,
            @Param("since") Instant since
    );
}
