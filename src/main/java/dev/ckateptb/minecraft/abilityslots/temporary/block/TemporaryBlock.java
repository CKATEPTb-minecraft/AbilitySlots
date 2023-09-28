package dev.ckateptb.minecraft.abilityslots.temporary.block;


import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.atom.scheduler.SyncScheduler;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TemporaryBlock {
    private final Location location;
    private static final String METADATA_KEY = "abilityslots_original_data";

    public static TemporaryBlock of(Block block) {
        return new TemporaryBlock(block.getLocation());
    }

    public void setType(Material material, long duration) {
        this.setBlockData(material.createBlockData(), duration);
    }

    public Location getLocation() {
        return this.location;
    }

    public Block getBlock() {
        return this.location.getBlock();
    }

    public boolean isTemporary() {
        Block block = this.getBlock();
        BlockState blockState = block.getState();
        return blockState.getMetadata(METADATA_KEY).stream().findFirst().isPresent();
    }

    public void setBlockData(BlockData blockData, long duration) {
        Mono.just(this.location)
                .map(location -> location.getBlock().getState())
                .zipWhen(state -> this.getOriginalData())
                .flatMap(tuple2 -> {
                    BlockState state = tuple2.getT1();
                    BlockData original = tuple2.getT2();
                    state.setBlockData(blockData);
                    state.update(true, false);
                    return Mono.just(state)
                            .delayElement(Duration.of(duration, ChronoUnit.MILLIS))
                            .map(delayed -> {
                                new SyncScheduler().schedule(() -> {
                                    if (delayed.getBlockData().equals(blockData)) {
                                        delayed.removeMetadata(METADATA_KEY, AbilitySlots.getPlugin());
                                        delayed.setBlockData(original);
                                        state.update(true, false);
                                    }
                                });
                                return delayed;
                            });
                })
                .subscribeOn(new SyncScheduler())
                .subscribe();
    }

    private Mono<BlockData> getOriginalData() {
        return Mono.just(this.location)
                .map(location -> location.getBlock().getState())
                .map(blockState -> {
                    MetadataValue metadataValue = blockState.getMetadata(METADATA_KEY).stream().findFirst().orElse(null);
                    if (metadataValue != null) {
                        return Bukkit.createBlockData(metadataValue.asString());
                    }
                    BlockData blockData = blockState.getBlockData();
                    blockState.setMetadata(METADATA_KEY, new FixedMetadataValue(AbilitySlots.getPlugin(), blockData.getAsString()));
                    return blockData;
                });
    }
}