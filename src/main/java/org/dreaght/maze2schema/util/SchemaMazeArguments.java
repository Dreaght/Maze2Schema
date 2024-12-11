package org.dreaght.maze2schema.util;

import lombok.Getter;
import org.dreaght.snubsquaremaze.util.MazeArguments;

@Getter
public class SchemaMazeArguments extends MazeArguments {

    private String filePath = "maze.nbt";
    private int scale = 5;

    public SchemaMazeArguments(String[] args) {
        super(args);
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--output=")) {
                filePath = args[i].substring(9);
            } else if (args[i].startsWith("--scale=")) {
                scale = Integer.parseInt(args[i].substring(8));
            }
        }
    }
}
