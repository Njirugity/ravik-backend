package net.ravik_cms.ravik_backend.authorization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

@Component
@RequestScope
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProjectContext {
    private UUID userId;
    private UUID projectId;
}
