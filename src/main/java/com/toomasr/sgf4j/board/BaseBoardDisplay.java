package com.toomasr.sgf4j.board;

import java.util.*;

/**
 * Created by fsp on 16-9-26.
 */
public class BaseBoardDisplay implements IBoardDisplay {
    private int size;
    private Square[][] vBoard;
    private List<IBoardClickListener> boardClickListeners = new ArrayList<>();
    public BaseBoardDisplay(int size){
        this.size=size;
        initEmptyBoard();
    }

    public void initEmptyBoard() {
        vBoard=new Square[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                vBoard[i][j] = new Square(StoneState.EMPTY, i, j);
            }
        }
    }

    @Override
    public void addStone(Square square) {
        vBoard[square.x][square.y]=square;
    }

    @Override
    public void addStones(List<Square> squares) {
        for(int i=0; i<squares.size(); i++){
            addStone(squares.get(i));
        }
    }

    @Override
    public void removeStone(Square square) {
        removeStone(square.x, square.y);
    }

    @Override
    public void removeStone(int x, int y) {
        vBoard[x][y].setColor(StoneState.EMPTY);
    }

    @Override
    public void removeStones(Group group) {
        for (Iterator<Square> ite = group.stones.iterator(); ite.hasNext();) {
            Square square = ite.next();
            removeStone(square.x, square.y);
        }
    }

    @Override
    public void removeStones(List<Square> squares) {
        for(int i=0; i<squares.size(); i++){
            removeStone(squares.get(i));
        }
    }

    @Override
    public void addBoardClickListener(IBoardClickListener boardClickListener) {
        if(!boardClickListeners.contains(boardClickListener)){
            boardClickListeners.add(boardClickListener);
        }
    }

    @Override
    public void boardClick(int x, int y) {
        for (Iterator<IBoardClickListener> ite = boardClickListeners.iterator(); ite.hasNext();) {
            IBoardClickListener boardListener = ite.next();
            boardListener.boardClick(x, y);
        }
    }

    public String printBoard() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vBoard.length; i++) {
            for (int j = 0; j < vBoard[i].length; j++) {
                sb.append(vBoard[i][j]);
            }
            sb.append("\r\n");
        }
        return sb.toString();
    }

    public Set<Group> findDistinctGroups(StoneState color) {
        Set<Square> alreadyChecked = new HashSet<Square>();

        Set<Group> groups = new HashSet<Group>();
        Group activeGroup = new Group();
        for (int i = 0; i < vBoard.length; i++) {
            for (int j = 0; j < vBoard[i].length; j++) {
                // we found a group, lets expand this
                if (vBoard[i][j].isOfColor(color) && !alreadyChecked.contains(vBoard[i][j])) {
                    populateGroup(i, j, color, activeGroup);
                    alreadyChecked.addAll(activeGroup.stones);

                    groups.add(activeGroup);
                    activeGroup = new Group();
                }
                // alreadyChecked.add(new Square());
            }
        }
        return groups;
    }

    @Override
    public boolean isGroupDead(Group group) {
        return group.isDead(vBoard);
    }


    /*
     * Starts from a node and then finds all the connected stones with this group.
     * Basically populates by starting from a single node.
     */
    private void populateGroup(int i, int j, StoneState color, Group activeGroup) {
        if (vBoard[i][j].isOfColor(color) && !activeGroup.contains(vBoard[i][j])) {
            activeGroup.addStone(vBoard[i][j]);
            if (i - 1 > -1)
                populateGroup(i - 1, j, color, activeGroup);
            if (i + 1 < 19)
                populateGroup(i + 1, j, color, activeGroup);
            if (j - 1 > -1)
                populateGroup(i, j - 1, color, activeGroup);
            if (j + 1 < 19)
                populateGroup(i, j + 1, color, activeGroup);
        }
        else {
            return;
        }
    }

    public Square getSquare(int x, int y) {
        return vBoard[x][y];
    }
    public Square[][] getAllSquare(){return vBoard;}
}
