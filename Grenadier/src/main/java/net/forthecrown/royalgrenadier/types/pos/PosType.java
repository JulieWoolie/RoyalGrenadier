package net.forthecrown.royalgrenadier.types.pos;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.commands.arguments.coordinates.LocalCoordinates;

@Getter
@RequiredArgsConstructor
enum PosType {
    WORLD ('~'),
    LOCAL (LocalCoordinates.PREFIX_LOCAL_COORDINATE);

    private final char prefixChar;

    public PosType opposite() {
        return switch (this) {
            case LOCAL -> WORLD;
            case WORLD -> LOCAL;
        };
    }
}