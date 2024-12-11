package org.dreaght.maze2schema;

import org.dreaght.maze2schema.util.SchemaMazeArguments;
import org.dreaght.maze2schema.util.MazeSchematicUtil;
import org.dreaght.snubsquaremaze.maze.Maze;

public class Maze2Schema {
    public static void main(String[] args) {
        SchemaMazeArguments arguments = new SchemaMazeArguments(args);
        Maze maze = new Maze(arguments);

        MazeSchematicUtil.convertMazeToSchematic(maze, arguments.getFilePath(), arguments.getScale());
    }
}
