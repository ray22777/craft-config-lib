package net.ray.CraftConfig.api.v1.entries;

import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.resources.Identifier;

//?if >=1.21.2{
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
//?}
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class BaseEntry {

    public final String key;
    public final String label;

    public @Nullable ItemStack icon;

    public @Nullable Supplier<ItemStack> iconSupplier;

    public final int iconSize;

    protected BaseEntry(String key, String label,
                        @Nullable ItemStack icon,
                        @Nullable Supplier<ItemStack> iconSupplier,
                        int iconSize) {
        this.key          = key;
        this.label        = label;
        this.icon         = icon;
        this.iconSupplier = iconSupplier;
        this.iconSize     = Math.max(8, Math.min(64, iconSize));
    }

    public @Nullable ItemStack getIcon() {
        if (icon != null) return icon;
        if (iconSupplier != null) {
            icon = iconSupplier.get();
            iconSupplier = null;
        }
        return icon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntry that)) return false;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() { return Objects.hash(key); }

    public static String toFriendly(String path) {
        String[] parts = path.split("_");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (!p.isEmpty()) {
                sb.append(Character.toUpperCase(p.charAt(0)));
                sb.append(p.substring(1)).append(' ');
            }
        }
        return sb.toString().trim();
    }


    public static List<String> getAllBlockKeys() {
        return BuiltInRegistries.BLOCK.keySet().stream().map(Object::toString).toList();
    }

    public static List<String> getAllItemKeys() {
        return BuiltInRegistries.ITEM.keySet().stream().map(Object::toString).toList();
    }

    public static List<String> getAllEntityKeys() {
        return BuiltInRegistries.ENTITY_TYPE.keySet().stream().map(Object::toString).toList();
    }
    public abstract static class BlockBuilderBase<E extends BaseEntry, B extends BlockBuilderBase<E, B>> {
        protected final List<String>        keys              = new ArrayList<>();
        protected Predicate<Identifier> filter          = rl -> true;
        protected int                       iconSize          = 16;
        protected boolean                   sortAlphabetically = false;

        @SuppressWarnings("unchecked")
        public B add(String registryName)               { keys.add(registryName); return (B) this; }
        @SuppressWarnings("unchecked")
        public B addAll(Collection<String> names)       { keys.addAll(names); return (B) this; }
        @SuppressWarnings("unchecked")
        public B filter(Predicate<Identifier> p)  { this.filter = p; return (B) this; }
        @SuppressWarnings("unchecked")
        public B fromMod(String modId)                  { return filter(rl -> rl.getNamespace().equals(modId)); }
        @SuppressWarnings("unchecked")
        public B fromTag(net.minecraft.tags.TagKey<Block> tag) {
			//? if <=1.21.1 {
						/*return filter(rl -> BuiltInRegistries.BLOCK.get(rl).builtInRegistryHolder().is(tag));
			*///?} else {
				return filter(rl -> {
					Block block = BuiltInRegistries.BLOCK.get(rl).get().value();
					return block.builtInRegistryHolder().is(TagKey.create(Registries.BLOCK,
							//?if>=1.21.11{
							tag.registry().identifier()
							//?}else{
							/*tag.identifier()
							*///?}

					));
				});
			//?}
        }
        @SuppressWarnings("unchecked")
        public B iconSize(int size)                     { this.iconSize = size; return (B) this; }
        @SuppressWarnings("unchecked")
        public B sortAlphabetically()                   { this.sortAlphabetically = true; return (B) this; }
        protected abstract E createEntry(String name, String label,
                                         @Nullable ItemStack icon, int iconSize);

        public List<E> build() {
            List<E> out = new ArrayList<>();
            for (String name : keys) {
                Identifier rl = Identifier.tryParse(name);
                if (rl == null || !filter.test(rl)) continue;
				//? if <=1.21.1 {
					/*Block b = BuiltInRegistries.BLOCK.get(rl);
				*///?} else {
					Block b = BuiltInRegistries.BLOCK.get(rl).get().value();
				//?}

                ItemStack icon = (b != null && b.asItem() != null)
                        ? new ItemStack(b.asItem()) : null;
                out.add(createEntry(name, toFriendly(rl.getPath()), icon, iconSize));
            }
            if (sortAlphabetically)
                out.sort(Comparator.comparing(e -> e.label.toLowerCase(Locale.ROOT)));
            return out;
        }
    }

    public abstract static class ItemBuilderBase<E extends BaseEntry, B extends ItemBuilderBase<E, B>> {
        protected final List<String>          keys              = new ArrayList<>();
        protected Predicate<Identifier> filter            = rl -> true;
        protected int                         iconSize          = 16;
        protected boolean                     sortAlphabetically = false;

        @SuppressWarnings("unchecked")
        public B add(String registryName)               { keys.add(registryName); return (B) this; }
        @SuppressWarnings("unchecked")
        public B addAll(Collection<String> names)       { keys.addAll(names); return (B) this; }
        @SuppressWarnings("unchecked")
        public B filter(Predicate<Identifier> p)  { this.filter = p; return (B) this; }
        @SuppressWarnings("unchecked")
        public B fromMod(String modId)                  { return filter(rl -> rl.getNamespace().equals(modId)); }
        @SuppressWarnings("unchecked")
        public B iconSize(int size)                     { this.iconSize = size; return (B) this; }
        @SuppressWarnings("unchecked")
        public B sortAlphabetically()                   { this.sortAlphabetically = true; return (B) this; }

        protected abstract E createEntry(String name, String label,
                                         @Nullable ItemStack icon, int iconSize);

        public List<E> build() {
            List<E> out = new ArrayList<>();
            for (String name : keys) {
                Identifier rl = Identifier.tryParse(name);
                if (rl == null || !filter.test(rl)) continue;
				//? if <=1.21.1 {
				/*Item it = BuiltInRegistries.ITEM.get(rl);
				*///?} else {
				Item it = BuiltInRegistries.ITEM.get(rl).get().value();
				//?}

                ItemStack icon = (it != null) ? new ItemStack(it) : null;
                out.add(createEntry(name, toFriendly(rl.getPath()), icon, iconSize));
            }
            if (sortAlphabetically)
                out.sort(Comparator.comparing(e -> e.label.toLowerCase(Locale.ROOT)));
            return out;
        }
    }
}
