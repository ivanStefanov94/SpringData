package softuni.exam.instagraphlite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import softuni.exam.instagraphlite.models.entity.Picture;

import java.util.List;
import java.util.Optional;

@Repository
public interface PictureRepository extends JpaRepository<Picture, Integer> {
    boolean existsByPath(String path);
    Optional<Picture> findByPath(String path);

    @Query("SELECT p FROM Picture p WHERE p.size > 30000 ORDER BY p.size")
    List<Picture> exportPicturesWithSizeBiggerThan30000();
}
