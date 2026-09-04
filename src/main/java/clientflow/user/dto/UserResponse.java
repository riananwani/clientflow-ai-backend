package clientflow.user.dto;

import clientflow.user.Role;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private Boolean active;
    private LocalDateTime createdAt;
}