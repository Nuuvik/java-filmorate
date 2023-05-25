package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.annotation.ReleaseDate;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Film {
    protected int id;

    @NotBlank(message = "Название не может быть пустым")
    protected String name;

    @Size(max = 200, message = "Максимальная длина описания - 200 символов")
    protected String description;

    @ReleaseDate
    protected LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    protected int duration;

    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}


