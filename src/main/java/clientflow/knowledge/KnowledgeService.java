package clientflow.knowledge;

import clientflow.knowledge.dto.DocumentResponse;
import clientflow.knowledge.dto.UploadDocumentRequest;
import clientflow.project.Project;
import clientflow.project.ProjectRepository;
import clientflow.user.User;
import clientflow.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KnowledgeService {

    private final KnowledgeDocumentRepository documentRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public DocumentResponse uploadDocument(Long projectId, UploadDocumentRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User uploadedBy = userRepository.findById(request.getUploadedById())
                .orElseThrow(() -> new RuntimeException("User not found"));

        KnowledgeDocument document = KnowledgeDocument.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .project(project)
                .uploadedBy(uploadedBy)
                .build();

        KnowledgeDocument saved = documentRepository.save(document);
        return mapToResponse(saved);
    }

    public List<DocumentResponse> getDocumentsByProject(Long projectId) {
        return documentRepository.findByProjectId(projectId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public DocumentResponse getDocumentById(Long id) {
        KnowledgeDocument document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        return mapToResponse(document);
    }

    public void deleteDocument(Long id) {
        if (!documentRepository.existsById(id)) {
            throw new RuntimeException("Document not found");
        }
        documentRepository.deleteById(id);
    }

    private DocumentResponse mapToResponse(KnowledgeDocument doc) {
        return DocumentResponse.builder()
                .id(doc.getId())
                .title(doc.getTitle())
                .content(doc.getContent())
                .projectId(doc.getProject().getId())
                .projectName(doc.getProject().getName())
                .uploadedById(doc.getUploadedBy().getId())
                .uploadedByName(doc.getUploadedBy().getName())
                .createdAt(doc.getCreatedAt())
                .build();
    }
}