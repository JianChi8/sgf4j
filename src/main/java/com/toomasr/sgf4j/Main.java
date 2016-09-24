package com.toomasr.sgf4j;

import com.toomasr.sgf4j.parser.Game;
import com.toomasr.sgf4j.parser.GameNode;

import java.nio.file.Paths;

/**
 * Created by fsp on 16-9-24.
 */
public class Main {
    public static void main(String[] args){
        Game game = Sgf.createFromPath(Paths.get("TM.sgf"));

        for(String key : game.getProperties().keySet()){
            System.out.println(key + ":" + game.getProperty(key));
        }

        GameNode node = game.getRootNode();
        do {
            System.out.println(node);
        }
        while ((node = node.getNextNode()) != null);
        System.out.print("end");
    }
}
