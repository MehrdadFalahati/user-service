package com.github.mehrdadfalahati.entity.meta;

import lombok.*;

@Getter
@RequiredArgsConstructor
public final class Pair<S, T> {
    private final S first;
    private final T second;

    public static <S, T> Pair<S, T> of(S first, T second) {
        return new Pair<>(first, second);
    }
}
