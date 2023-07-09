package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class User {
    private Integer id;

    @Email
    @NotBlank
    @NotNull
    private String email;

    @Pattern(regexp = "^[^\\s]+$")
    @NotBlank
    private String login;

    private String name;

    @PastOrPresent
    private LocalDate birthday;
    private Set<Integer> friends;
}
