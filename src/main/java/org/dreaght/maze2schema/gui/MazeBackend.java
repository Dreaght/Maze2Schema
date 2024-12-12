package org.dreaght.maze2schema.gui;

import fi.iki.elonen.NanoHTTPD;
import org.dreaght.maze2schema.util.MazeSchematicUtil;
import org.dreaght.maze2schema.util.MinecraftNBTUtil;
import org.dreaght.snubsquaremaze.maze.Maze;
import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

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
            // Handle the maze request (same as before)
            int width = Integer.parseInt(session.getParms().get("width"));
            int height = Integer.parseInt(session.getParms().get("height"));
            int scale = Integer.parseInt(session.getParms().get("scale"));

            int[][] mazeArray = generateMazeArray(width, height, scale);

            JSONArray jsonArray = new JSONArray(mazeArray);

            response = newFixedLengthResponse(Response.Status.OK, "application/json", jsonArray.toString());

        } else if ("/m2sdesigner.html".equals(uri)) {
            // Serve the HTML file
            response = serveHtmlFile();
        }

        return response;
    }

    // Method to serve the m2sdesigner.html file
    private Response serveHtmlFile() {
        try {
            // Path to the HTML file inside the resources folder
            ClassLoader classLoader = MazeBackend.class.getClassLoader();
            URL resource = classLoader.getResource("m2sdesigner.html");
            if (resource == null) {
                return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/html", "File not found");
            }
            File htmlFile = new File(resource.getFile());

            if (htmlFile.exists()) {
                InputStream fileStream = new FileInputStream(htmlFile);
                long fileLength = htmlFile.length(); // Get the file size

                // Return the response with the correct content length using newFixedLengthResponse
                return newFixedLengthResponse(Response.Status.OK, "text/html", fileStream, fileLength);
            } else {
                return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/html", "File not found");
            }

        } catch (IOException e) {
            e.printStackTrace();
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/html", "Error loading HTML file");
        }
    }

    private String generateMazeSNBTString(int width, int height, int scale) throws IOException {
        Maze maze = new Maze(width, height);
        return MinecraftNBTUtil.getSNBT(MazeSchematicUtil.convertMazeToBlocks(maze, scale));
    }

    private int[][] generateMazeArray(int width, int height, int scale) {
        Maze maze = new Maze(width, height);
        List<MinecraftNBTUtil.Block> blocks = MazeSchematicUtil.convertMazeToBlocks(maze, scale);

        MinecraftNBTUtil.saveNBTToClipboard(blocks, false);

        // Calculate the minimum x and z to handle negative coordinates
        int minX = blocks.stream().mapToInt(b -> b.x).min().orElse(0);
        int minZ = blocks.stream().mapToInt(b -> b.z).min().orElse(0);

        // Apply an offset to ensure all indices are non-negative
        int offsetX = minX < 0 ? -minX : 0;
        int offsetZ = minZ < 0 ? -minZ : 0;

        // Find the max coordinates
        int maxX = blocks.stream().mapToInt(b -> b.x + offsetX).max().orElse(0);
        int maxZ = blocks.stream().mapToInt(b -> b.z + offsetZ).max().orElse(0);

        // Initialize the binary matrix with the correct size
        int[][] binaryMatrix = new int[maxX + 1][maxZ + 1];

        // Set the blocks in the binary matrix after applying the offset
        blocks.forEach(block -> binaryMatrix[block.x + offsetX][block.z + offsetZ] = 1);

        return binaryMatrix;
    }

}
