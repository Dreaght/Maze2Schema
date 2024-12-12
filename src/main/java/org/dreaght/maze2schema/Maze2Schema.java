package org.dreaght.maze2schema;

import org.dreaght.maze2schema.gui.MazeBackend;
import org.dreaght.maze2schema.gui.OpenHTMLInBrowser;
import org.dreaght.maze2schema.util.SchemaMazeArguments;
import org.dreaght.maze2schema.util.MazeSchematicUtil;
import org.dreaght.snubsquaremaze.maze.Maze;

import java.io.IOException;

public class Maze2Schema {
    public static void main(String[] args) {
        SchemaMazeArguments arguments = new SchemaMazeArguments(args);

        if (arguments.isStartAsServer()) {
            try {
                new MazeBackend();

                OpenHTMLInBrowser.open();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Maze maze = new Maze(arguments);

        MazeSchematicUtil.convertMazeToSchematic(maze, arguments.getFilePath(), arguments.getScale());
    }
}
