package model.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SignUpDTO {
    private String firstName;

    private String lastName;

    private String email;

    private String password;

}
