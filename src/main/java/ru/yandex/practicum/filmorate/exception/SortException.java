package ru.yandex.practicum.filmorate.exception;

import net.bytebuddy.TypeCache;

public class SortException extends RuntimeException{

    public SortException (String message) {
        super(message);
    }
}
