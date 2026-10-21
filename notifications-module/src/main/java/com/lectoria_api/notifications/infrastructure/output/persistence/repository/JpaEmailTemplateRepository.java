package com.lectoria_api.notifications.infrastructure.output.persistence.repository;

import com.lectoria_api.notifications.infrastructure.output.persistence.entities.EmailTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface JpaEmailTemplateRepository extends JpaRepository<EmailTemplateEntity, String>, JpaSpecificationExecutor<EmailTemplateEntity> {

    Optional<EmailTemplateEntity> findByTemplateName(String templateName);

}
