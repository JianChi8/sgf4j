package com.toomasr.sgf4j.board;

import java.util.List;
import java.util.Set;

/**
 * Created by fsp on 16-9-26.
 */
public interface IBoardDisplay {
    void addStone(Square square);
    void addStones(List<Square> squares);
    void removeStone(Square square);
    void removeStone(int x, int y);
    void removeStones(Group group);
    void removeStones(List<Square> squares);
    void addBoardClickListener(IBoardClickListener boardClickListener);
    void boardClick(int x, int y);
    String printBoard();
    void initEmptyBoard();
    Set<Group> findDistinctGroups(StoneState color);
    boolean isGroupDead(Group group);
    Square getSquare(int x, int y);
}
