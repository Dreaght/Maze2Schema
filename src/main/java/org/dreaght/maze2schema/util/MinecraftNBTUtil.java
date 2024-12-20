package org.dreaght.maze2schema.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.StringTag;

public class MinecraftNBTUtil {

    public static class Block {
        public int x, y, z;

        public Block(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    /**
     * Saves the NBT representation of blocks to the system clipboard as a .nbt file.
     *
     * @param blocks list of blocks to encode
     * @param useThread whether to save the clipboard content in a separate thread
     * @throws IOException if serialization fails
     */
    public static void saveNBTToClipboard(List<Block> blocks, boolean useThread) {
        Runnable saveTask = () -> {
            Path tempFile = null;
            try {
                tempFile = Files.createTempFile("blocks", ".nbt");
                saveBlocksToNBTFile(blocks, tempFile.toString());
                File file = tempFile.toFile();

                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(new FileTransferable(file), null);

            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        if (useThread) {
            Thread thread = new Thread(saveTask);
            thread.setDaemon(true); // Ensure the thread does not prevent program termination
            thread.start();
        } else {
            saveTask.run();
        }
    }

    /**
     * Writes a list of blocks to a Minecraft NBT file.
     *
     * @param blocks list of blocks to write
     * @param path   directory path as a String where the file should be saved
     *
     * @throws IOException if there is a problem writing to the file
     */
    public static void saveBlocksToNBTFile(List<Block> blocks, String path) throws IOException {
        CompoundTag rootTag = createNBTTag(blocks);
        NamedTag namedTag = new NamedTag("", rootTag);

        // Generate the file
        Path outputPath = Path.of(path);
        File file = outputPath.toFile();
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            throw new IOException("Failed to create output directory: " + file.getParentFile().getAbsolutePath());
        }
        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("Failed to create output file: " + file.getAbsolutePath());
        }

        // Write to file
        NBTUtil.write(namedTag, file);
    }

    /**
     * Converts a list of blocks to an SNBT string representation.
     *
     * @param blocks list of blocks to encode
     * @return SNBT string representation of the NBT data
     */
    public static String getSNBT(List<Block> blocks) throws IOException {
        CompoundTag rootTag = createNBTTag(blocks);
        return SNBTUtil.toSNBT(rootTag); // Converts the CompoundTag to a string
    }

    /**
     * Creates the NBT tag structure for a given list of blocks.
     *
     * @param blocks list of blocks to encode
     * @return CompoundTag representing the NBT data
     */
    private static CompoundTag createNBTTag(List<Block> blocks) {
        CompoundTag rootTag = new CompoundTag();

        // Define size based on the range of blocks
        int maxX = blocks.stream().mapToInt(b -> b.x).max().orElse(0) + 1;
        int maxY = blocks.stream().mapToInt(b -> b.y).max().orElse(0) + 1;
        int maxZ = blocks.stream().mapToInt(b -> b.z).max().orElse(0) + 1;

        ListTag<IntTag> sizeTag = new ListTag<>(IntTag.class);
        sizeTag.add(new IntTag(maxX));
        sizeTag.add(new IntTag(maxY));
        sizeTag.add(new IntTag(maxZ));
        rootTag.put("size", sizeTag);

        // Define entities (empty in this case)
        ListTag<CompoundTag> entitiesTag = new ListTag<>(CompoundTag.class);
        rootTag.put("entities", entitiesTag);

        // Add blocks
        ListTag<CompoundTag> blockList = new ListTag<>(CompoundTag.class);
        for (Block block : blocks) {
            CompoundTag blockTag = new CompoundTag();

            ListTag<IntTag> posTag = new ListTag<>(IntTag.class);
            posTag.add(new IntTag(block.x));
            posTag.add(new IntTag(block.y));
            posTag.add(new IntTag(block.z));
            blockTag.put("pos", posTag);

            blockTag.put("state", new IntTag(0));
            blockList.add(blockTag);
        }
        rootTag.put("blocks", blockList);

        // Define palette (single entry for stone)
        ListTag<CompoundTag> paletteTag = new ListTag<>(CompoundTag.class);
        CompoundTag stonePalette = new CompoundTag();
        stonePalette.put("Name", new StringTag("minecraft:stone"));
        paletteTag.add(stonePalette);
        rootTag.put("palette", paletteTag);

        // Define DataVersion
        rootTag.put("DataVersion", new IntTag(3955));

        return rootTag;
    }

    private static class FileTransferable implements Transferable {
        private final File file;

        public FileTransferable(File file) {
            this.file = file;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.javaFileListFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.javaFileListFlavor.equals(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) {
            if (isDataFlavorSupported(flavor)) {
                return List.of(file);
            }
            return null;
        }
    }
}
