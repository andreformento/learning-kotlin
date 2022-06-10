package com.andreformento.kalah.game;

import java.util.*;

public class Board {
    private Integer[] housesA;
    private Integer kalahA;

    private Integer[] housesB;
    private Integer kalahB;

    public Board(Integer housesSize, Integer numberOfSeeds) {
        housesA = Collections.nCopies(housesSize, numberOfSeeds).toArray(new Integer[]{});
        housesB = Collections.nCopies(housesSize, numberOfSeeds).toArray(new Integer[]{});
        kalahA = 0;
        kalahB = 0;
    }

    public void move(Integer house, String player) {
        

        if (Objects.equals(player, "A")) {

        }
    }

}
