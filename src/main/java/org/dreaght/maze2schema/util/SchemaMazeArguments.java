package org.dreaght.maze2schema.util;

import lombok.Getter;
import org.dreaght.snubsquaremaze.util.MazeArguments;

import java.util.Arrays;

@Getter
public class SchemaMazeArguments extends MazeArguments {

    private String filePath = "out/maze.nbt";
    private int scale = 5;
    private boolean startAsServer = false;

    public SchemaMazeArguments(String[] args) {
        super(args);

        if (Arrays.asList(args).contains("--help")) {
            System.out.println("Usage:");
            System.out.println("  --output=\"out/maze.nbt\" - Output file path");
            System.out.println("  --width=20 - Maze width (not actual schematic size)");
            System.out.println("  --height=20 - Maze height (not actual schematic size)");
            System.out.println("  --scale=5 - Scale factor");
            System.out.println("  --help - Show this help message");
            System.exit(0);
        }

        if (Arrays.asList(args).contains("--gui")) {
            startAsServer = true;
        }

        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--output=")) {
                filePath = args[i].substring(9);
            } else if (args[i].startsWith("--scale=")) {
                scale = Integer.parseInt(args[i].substring(8));
            }
        }
    }
}
