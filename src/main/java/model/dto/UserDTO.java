package model.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDTO {
    private int id;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

}
