package adrianles.usww.domain.repository;

import adrianles.usww.domain.entity.MessageAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageAttachmentRepository extends JpaRepository<MessageAttachment, Integer> {
    Optional<MessageAttachment> findByMessageId(Integer messageId);
}
