package org.dreaght.maze2schema.gui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class OpenHTMLInBrowser {

    public static void open() {
        try {
            // Open the HTML page served by the backend
            URI uri = new URI("http://localhost:9603/m2sdesigner.html");

            // Check if the Desktop API is supported and open the URL
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(uri);
            } else {
                System.out.println("Desktop is not supported.");
            }
        } catch (IOException | URISyntaxException e) {
            System.out.println("Error opening HTML page in browser: " + e.getMessage());
        }
    }
}
