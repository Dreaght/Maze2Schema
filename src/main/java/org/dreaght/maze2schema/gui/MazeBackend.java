package org.dreaght.maze2schema.gui;

import fi.iki.elonen.NanoHTTPD;
import org.dreaght.maze2schema.util.MazeSchematicUtil;
import org.dreaght.maze2schema.util.MinecraftNBTUtil;
import org.dreaght.snubsquaremaze.maze.Maze;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MazeBackend extends NanoHTTPD {
    public MazeBackend() throws IOException {
        super(9603); // Run on port 8080
        start(SOCKET_READ_TIMEOUT, false);
        System.out.println("Server is running at http://localhost:9603");
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();

        // CORS headers
        Response response = newFixedLengthResponse(Response.Status.OK, "application/json", "");
        response.addHeader("Access-Control-Allow-Origin", "*"); // Allow all origins
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");

        if ("/get-maze".equals(uri)) {
            // Get arguments: width, height, scale
            int width = Integer.parseInt(session.getParms().get("width"));
            int height = Integer.parseInt(session.getParms().get("height"));
            int scale = Integer.parseInt(session.getParms().get("scale"));

            String maze;
            try {
                maze = generateMaze(width, height, scale);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println(maze);

            // Convert the maze string to an InputStream
            InputStream inputStream = new ByteArrayInputStream(maze.getBytes());

            // Set the maze data as InputStream
            response.setData(inputStream);
        }

        return response;
    }



    private String generateMaze(int width, int height, int scale) throws IOException {
        Maze maze = new Maze(width, height);
        return MinecraftNBTUtil.getSNBT(MazeSchematicUtil.convertMazeToBlocks(maze, scale));
    }
}
