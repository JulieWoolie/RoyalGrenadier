package net.forthecrown.royalgrenadier.types.pos;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
class Coordinate {
    static final Coordinate EMPTY = new Coordinate(0F, true);

    private final double value;
    private final boolean relative;

    public double apply(double offset) {
        return relative ? (value + offset) : value;
    }
}