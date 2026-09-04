package clientflow.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private Boolean active;
}