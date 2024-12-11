package org.dreaght.maze2schema.util;

import org.dreaght.snubsquaremaze.maze.Maze;
import org.dreaght.snubsquaremaze.maze.Point;
import org.dreaght.snubsquaremaze.maze.Wall;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MazeSchematicUtil {

    /**
     * Converts a given Maze to a Minecraft Schematic and saves it to the given filePath.
     * Each wall in the maze is converted to a line of blocks in the schematic.
     * The given scale is used to scale the coordinates of the maze.
     *
     * @param maze the Maze to convert
     * @param filePath the path to save the Minecraft Schematic
     * @param scale the scale to use when converting coordinates
     */
    public static void convertMazeToSchematic(Maze maze, String filePath, int scale) {
        List<MinecraftNBTUtil.Block> blocks = new ArrayList<>();

        for (Wall wall : maze.getWalls()) {
            if (!wall.isOpen()) {
                List<Point> points = wall.getPoints();
                Point p1 = points.get(0);
                Point p2 = points.get(1);

                // Scale and convert coordinates to integers
                int x1 = (int) (p1.getX() * scale);
                int y1 = (int) (p1.getY() * scale);
                int x2 = (int) (p2.getX() * scale);
                int y2 = (int) (p2.getY() * scale);

                // Add blocks along the line connecting the start and end points
                blocks.addAll(generateLineBlocks(x1, y1, x2, y2));
            }
        }

        try {
            MinecraftNBTUtil.saveBlocksToNBTFile(blocks, new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a list of blocks which form a line from the start point to the end point.
     * The Bresenham's line algorithm is used to generate the line.
     * The blocks are added to the list in the order they appear from start to end.
     *
     * @param x1 the x-coordinate of the start point
     * @param y1 the y-coordinate of the start point
     * @param x2 the x-coordinate of the end point
     * @param y2 the y-coordinate of the end point
     * @return a list of blocks which form a line from the start point to the end point
     */
    private static List<MinecraftNBTUtil.Block> generateLineBlocks(int x1, int y1, int x2, int y2) {
        List<MinecraftNBTUtil.Block> lineBlocks = new ArrayList<>();

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        int x = x1;
        int y = y1;

        while (true) {
            lineBlocks.add(new MinecraftNBTUtil.Block(x, 0, y));

            if (x == x2 && y == y2) {
                break;
            }

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }

        return lineBlocks;
    }
}
