package org.dreaght.maze2schema.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.*;

public class MinecraftNBTUtil {

    public static class Block {
        public int x, y, z;

        public Block(int x, int y, int z) {
            this.x = x;
            this.y = x;
            this.z = z;
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
        CompoundTag rootTag = new CompoundTag();

        // Define size based on the range of blocks
        int maxX = blocks.stream().mapToInt(b -> b.x).max().orElse(0) + 1;
        int maxY = blocks.stream().mapToInt(b -> b.y).max().orElse(0) + 1;
        int maxZ = blocks.stream().mapToInt(b -> b.z).max().orElse(0) + 1;

        ListTag<IntTag> sizeTag = new ListTag(IntTag.class);
        sizeTag.add(new IntTag(maxX));
        sizeTag.add(new IntTag(maxY));
        sizeTag.add(new IntTag(maxZ));
        rootTag.put("size", sizeTag);

        // Define entities (empty in this case)
        ListTag<CompoundTag> entitiesTag = new ListTag(CompoundTag.class);
        rootTag.put("entities", entitiesTag);

        // Add blocks
        ListTag<CompoundTag> blockList = new ListTag(CompoundTag.class);
        for (Block block : blocks) {
            CompoundTag blockTag = new CompoundTag();

            ListTag<IntTag> posTag = new ListTag(IntTag.class);
            posTag.add(new IntTag(block.x));
            posTag.add(new IntTag(block.y));
            posTag.add(new IntTag(block.z));
            blockTag.put("pos", posTag);

            blockTag.put("state", new IntTag(0));
            blockList.add(blockTag);
        }
        rootTag.put("blocks", blockList);

        // Define palette (single entry for stone)
        ListTag<CompoundTag> paletteTag = new ListTag(CompoundTag.class);
        CompoundTag stonePalette = new CompoundTag();
        stonePalette.put("Name", new StringTag("minecraft:stone"));
        paletteTag.add(stonePalette);
        rootTag.put("palette", paletteTag);

        // Define DataVersion
        rootTag.put("DataVersion", new IntTag(3955));

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
        NBTUtil.write(rootTag, file);
    }
}