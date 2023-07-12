package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidationException extends IllegalArgumentException {

    public ValidationException() {
        super("Не пройдена валидация");
        log.error("Не пройдена валидация");
    }
}
