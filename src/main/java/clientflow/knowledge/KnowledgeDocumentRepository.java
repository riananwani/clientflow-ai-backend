package clientflow.knowledge;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface KnowledgeDocumentRepository extends JpaRepository<KnowledgeDocument, Long> {
    List<KnowledgeDocument> findByProjectId(Long projectId);

    @Query("SELECT d FROM KnowledgeDocument d WHERE d.project.id = :projectId AND " +
            "(LOWER(d.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<KnowledgeDocument> searchByProjectIdAndKeyword(
            @Param("projectId") Long projectId,
            @Param("keyword") String keyword);
}