package com.bol.game.domain.model;


import com.bol.game.domain.valueObjects.Name;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Player {
    public int[] smallPits;
    public int largePit;
    public Name name;

    public boolean pitsAreEmpty() {
        for (int pit : smallPits)
            if (pit != 0) return false;
        return true;
    }
}
