package net.tricube.CraftConfig.api.v1.entries;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SelectionGridEntry extends BaseEntry {

    public boolean enabled;
    public boolean isBlackList;

    public SelectionGridEntry(String key, String label, boolean enabled) {
        this(key, label, enabled, null, null, 16);
    }

    public SelectionGridEntry(String key, String label, boolean enabled,
                               @Nullable ItemStack icon) {
        this(key, label, enabled, icon, null, 16);
    }

    public SelectionGridEntry(String key, String label, boolean enabled,
                               @Nullable Supplier<ItemStack> iconSupplier) {
        this(key, label, enabled, null, iconSupplier, 16);
    }

    public SelectionGridEntry(String key, String label, boolean enabled,
                               @Nullable ItemStack icon,
                               @Nullable Supplier<ItemStack> iconSupplier) {
        this(key, label, enabled, icon, iconSupplier, 16);
    }

    public SelectionGridEntry(String key, String label, boolean enabled,
                               @Nullable ItemStack icon,
                               @Nullable Supplier<ItemStack> iconSupplier,
                               int iconSize) {
        super(key, label, icon, iconSupplier, iconSize);
        this.enabled = enabled;
    }

    public SelectionGridEntry copy() {
        SelectionGridEntry c = new SelectionGridEntry(key, label, enabled,
                icon == null ? null : icon.copy(), iconSupplier, iconSize);
        c.isBlackList = this.isBlackList;
        return c;
    }

    public boolean enabled()     { return enabled; }
    public boolean isBlacklist() { return isBlackList; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SelectionGridEntry that)) return false;
        return enabled == that.enabled && Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() { return Objects.hash(key, enabled); }



    public static BlockBuilder  forBlocks() { return new BlockBuilder(); }
    public static ItemBuilder   forItems()  { return new ItemBuilder(); }
    public static CustomBuilder custom()    { return new CustomBuilder(); }

    public static class BlockBuilder
            extends BaseEntry.BlockBuilderBase<SelectionGridEntry, BlockBuilder> {

        private final Set<String> enabled = new HashSet<>();

        private BlockBuilder() {}

        public BlockBuilder enable(String registryName) { enabled.add(registryName); return this; }
        public BlockBuilder enableAll()                 { enabled.addAll(keys); return this; }

        @Override
        protected SelectionGridEntry createEntry(String name, String label,
                                                  @Nullable ItemStack icon, int iconSize) {
            return new SelectionGridEntry(name, label, enabled.contains(name), icon, null, iconSize);
        }
    }

    public static class ItemBuilder
            extends BaseEntry.ItemBuilderBase<SelectionGridEntry, ItemBuilder> {

        private final Set<String> enabled = new HashSet<>();

        private ItemBuilder() {}

        public ItemBuilder enable(String registryName) { enabled.add(registryName); return this; }
        public ItemBuilder enableAll()                 { enabled.addAll(keys); return this; }

        @Override
        protected SelectionGridEntry createEntry(String name, String label,
                                                  @Nullable ItemStack icon, int iconSize) {
            return new SelectionGridEntry(name, label, enabled.contains(name), icon, null, iconSize);
        }
    }

    public static class CustomBuilder {
        private final List<SelectionGridEntry> entries    = new ArrayList<>();
        private int                            defaultSize = 16;

        private CustomBuilder() {}

        public CustomBuilder iconSize(int size) { this.defaultSize = size; return this; }

        public CustomBuilder add(String key, String label, boolean enabled) {
            entries.add(new SelectionGridEntry(key, label, enabled, null, null, defaultSize));
            return this;
        }

        public CustomBuilder add(String key, String label, boolean enabled,
                                 @Nullable ItemStack icon) {
            entries.add(new SelectionGridEntry(key, label, enabled, icon, null, defaultSize));
            return this;
        }

        public CustomBuilder add(String key, String label, boolean enabled,
                                 @Nullable Supplier<ItemStack> iconSupplier) {
            entries.add(new SelectionGridEntry(key, label, enabled, null, iconSupplier, defaultSize));
            return this;
        }

        public List<SelectionGridEntry> build() { return new ArrayList<>(entries); }
    }

    public static List<String> getEnabledKeys(List<SelectionGridEntry> entries) {
        return entries.stream()
                .filter(SelectionGridEntry::enabled)
                .map(e -> e.key)
                .collect(Collectors.toList());
    }

    public static List<SelectionGridEntry> fromKeys(List<String> keys,
                                                     Collection<String> enabledKeys) {
        List<SelectionGridEntry> out = new ArrayList<>();
        for (String key : keys) {
            net.minecraft.resources.Identifier rl =
                    net.minecraft.resources.Identifier.tryParse(key);
            String label = rl != null ? toFriendly(rl.getPath()) : key;
            out.add(new SelectionGridEntry(key, label, enabledKeys.contains(key)));
        }
        return out;
    }
}
