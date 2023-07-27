package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class Director {

    private Integer id;
    @NotBlank(message = "Не заполнено имя режиссёра")
    private String name;
}
