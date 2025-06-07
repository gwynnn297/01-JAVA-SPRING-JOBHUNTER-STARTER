package vn.hoidanit.jobhunter.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqLoginDTO {
    @NotBlank(message = "username không được bỏ trống")
    private String username;
    @NotBlank(message = "password không được bỏ trống")
    private String password;

}
