package io.github.md5sha256.pls.paper.argument.adapter;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import io.github.md5sha256.pls.paper.argument.ArgumentTypeAdapter;
import io.github.md5sha256.pls.paper.argument.ArgumentTypeAdapters;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.arguments.AngleArgument;
import net.minecraft.commands.arguments.ColorArgument;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.GameModeArgument;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.commands.arguments.HeightmapTypeArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.NbtTagArgument;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.ObjectiveCriteriaArgument;
import net.minecraft.commands.arguments.OperationArgument;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.commands.arguments.RangeArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import java.util.Arrays;

public class KnownArgumentTypes {

    private static final ArgumentTypeAdapters ADAPTERS = new ArgumentTypeAdapters();

    public static final ArgumentTypeAdapter<AngleArgument, AngleArgument.SingleAngle> ANGLE =
            register(
                    AngleArgument.class,
                    new ScalarDescriptionAdapter<>(
                            "Must be a yaw angle, measured in degrees with float number. -180.0 for due north, -90.0 for due east, 0.0 for due south, 90.0 for due west, to 179.9 for just west of due north, before wrapping back around to -180.0. Tilde notation can be used to specify a rotation relative to the execution yaw angle.")
            );

    public static final ArgumentTypeAdapter<BlockPosArgument, Coordinates> BLOCK_POS =
            register(
                    BlockPosArgument.class,
                    new ScalarDescriptionAdapter<>(
                            "A coordinate composed of 3 integers <x> <y> <z> representing a block's position.")
            );

    public static final ArgumentTypeAdapter<BlockPredicateArgument, BlockPredicateArgument.Result>
            BLOCK_PREDICATE = register(
            BlockPredicateArgument.class,
            new ScalarDescriptionAdapter<>("A block type that acts as predicate/filter.")
    );

    public static final ArgumentTypeAdapter<BlockStateArgument, BlockInput> BLOCK_STATE =
            register(
                    BlockStateArgument.class,
                    new ScalarDescriptionAdapter<>("A block's state.")
            );

    public static final ArgumentTypeAdapter<BoolArgumentType, Boolean> BOOL = register(
            BoolArgumentType.class,
            new ScalarDescriptionAdapter<>("A boolean.")
    );

    public static final ArgumentTypeAdapter<ColorArgument, ChatFormatting> COLOR = register(
            ColorArgument.class,
            new ScalarDescriptionAdapter<>("A color.")
    );

    public static final ArgumentTypeAdapter<ColumnPosArgument, Coordinates> COLUMN_POS = register(
            ColumnPosArgument.class,
            new ScalarDescriptionAdapter<>(
                    "A column coordinates composed of <x> and <z>, each of which must be an integer or tilde notation.")
    );

    public static final ArgumentTypeAdapter<ComponentArgument, Component> CHAT_COMPONENT = register(
            ComponentArgument.class,
            new ScalarDescriptionAdapter<>("A json string in Minecraft's rich text format.")
    );

    public static final ArgumentTypeAdapter<CompoundTagArgument, CompoundTag> NBT = register(
            CompoundTagArgument.class,
            new ScalarDescriptionAdapter<>("A string in Minecraft's NBT format.")
    );

    public static final ArgumentTypeAdapter<DimensionArgument, ResourceLocation> DIMENSION =
            register(
                    DimensionArgument.class,
                    new ScalarDescriptionAdapter<>("The name of a dimension/world.")
            );

    public static final ArgumentTypeAdapter<DoubleArgumentType, Double> DOUBLE =
            register(
                    DoubleArgumentType.class,
                    new ScalarDescriptionAdapter<>(
                            "A double.")
            );

    public static final ArgumentTypeAdapter<EntityAnchorArgument, EntityAnchorArgument.Anchor>
            ENTITY_ANCHOR = register(
            EntityAnchorArgument.class,
            new EnumArgumentAdapter<>("An anchor with respect to an entity's limbs.",
                    EntityAnchorArgument.Anchor.values(),
                    EnumArgumentAdapter::lowerCaseName)
    );

    public static final ArgumentTypeAdapter<FloatArgumentType, Float> FLOAT = register(
            FloatArgumentType.class,
            new ScalarDescriptionAdapter<>("A float.")
    );

    public static final ArgumentTypeAdapter<FunctionArgument, FunctionArgument.Result> FUNCTION =
            register(
                    FunctionArgument.class,
                    new ScalarDescriptionAdapter<>(
                            "A resource location which refers to a single function, or one prefixed with a #, which refers to a function tag.")
            );

    public static final ArgumentTypeAdapter<GameModeArgument, GameType> GAME_MODE = register(
            GameModeArgument.class,
            new EnumArgumentAdapter<>("A game mode.", GameType.values(), GameType::getName)
    );

    public static final ArgumentTypeAdapter<GameProfileArgument, GameProfileArgument.Result>
            GAME_PROFILE = register(
            GameProfileArgument.class,
            new ScalarDescriptionAdapter<>(
                    "A collection of game profiles (player profiles), which can be a player name (must be a real one if the server is in online mode), a player-type target selector, or a player's uuid.")
    );

    public static final ArgumentTypeAdapter<HeightmapTypeArgument, Heightmap.Types> HEIGHT_MAP =
            register(HeightmapTypeArgument.class, createHeightMapAdapter());

    public static final ArgumentTypeAdapter<IntegerArgumentType, Integer> INTEGER = register(
            IntegerArgumentType.class,
            new ScalarDescriptionAdapter<>("An integer.")
    );

    public static final ArgumentTypeAdapter<ItemArgument, ItemInput> ITEM = register(
            ItemArgument.class,
            new ScalarDescriptionAdapter<>("An item. The format is item_id{data_tags}, in which data tags can be omitted when not needed. The item_id is  is required, and it should be in the format of resource location. Data tags are inside the {} and are optional.")
    );

    public static final ArgumentTypeAdapter<ItemPredicateArgument, ItemPredicateArgument.Result> ITEM_PREDICATE = register(
            ItemPredicateArgument.class,
            new ScalarDescriptionAdapter<>("An item predicate/filter. The format is item_id{data_tags}, in which data tags can be omitted when not needed. The item_id is  is required, and it should be in the format of resource location. Data tags are inside the {} and are optional.")
    );

    public static final ArgumentTypeAdapter<LongArgumentType, Long> LONG = register(
            LongArgumentType.class,
            new ScalarDescriptionAdapter<>("A long.")
    );

    public static final ArgumentTypeAdapter<MessageArgument, MessageArgument.Message> MESSAGE =
            register(
                    MessageArgument.class,
                    new ScalarDescriptionAdapter<>("A plain text string. Can include spaces as well as target selectors. The game replaces entity selectors in the message with the list of selected entities' names, which is formatted as \"name1 and name2\" for two entities, or \"name1, name2, ... and nameN\" for N entities.")
            );

    public static final ArgumentTypeAdapter<NbtPathArgument, NbtPathArgument.NbtPath> NBT_PATH =
            register(
                    NbtPathArgument.class,
                    new ScalarDescriptionAdapter<>("An NBT path. A path has the general form node.….node, where each node declares what types of child tags can be chosen from previous tags.")
            );

    public static final ArgumentTypeAdapter<NbtTagArgument, Tag> NBT_TAG = register(
            NbtTagArgument.class,
            new ScalarDescriptionAdapter<>("An NBT tag of any type in StringifiedNBT (SNBT) format.")
    );

    public static final ArgumentTypeAdapter<ObjectiveArgument, String> OBJECTIVE = register(
            ObjectiveArgument.class,
            new ScalarDescriptionAdapter<>("A valid scoreboard objective name.")
    );

    public static final ArgumentTypeAdapter<ObjectiveCriteriaArgument, ObjectiveCriteria>
            OBJECTIVE_CRITERIA = register(
                    ObjectiveCriteriaArgument.class,
            new ScalarDescriptionAdapter<>("A scoreboard objective criterion.")
    );

    public static final ArgumentTypeAdapter<OperationArgument, OperationArgument.Operation>
    OPERATION = register(
            OperationArgument.class,
            new ScalarDescriptionAdapter<>("An arithmetic operator for /scoreboard.")
    );

    public static final ArgumentTypeAdapter<ParticleArgument, ParticleOptions> PARTICLE =
            register(
                    ParticleArgument.class,
                    new ScalarDescriptionAdapter<>("A resource location of a particle followed by particle parameters that are particle-specific.")
            );

    public static final ArgumentTypeAdapter<RangeArgument.Ints, MinMaxBounds.Ints> INT_RANGE =
            register(
                    RangeArgument.Ints.class,
                    new ScalarDescriptionAdapter<>("An integer within a certain bounds in the format min..max.")
            );

    public static final ArgumentTypeAdapter<RangeArgument.Floats, MinMaxBounds.Doubles> FLOAT_RANGE =
            register(
                    RangeArgument.Floats.class,
                    new ScalarDescriptionAdapter<>("A double within a certain bounds in the format min..max.")
            );

    private static <T extends ArgumentType<V>, V> ArgumentTypeAdapter<T, V> register(Class<T> argumentType,
                                                                                     ArgumentTypeAdapter<T, V> adapter) {
        ADAPTERS.withAdapter(argumentType, adapter);
        return adapter;
    }


    private static ArgumentTypeAdapter<HeightmapTypeArgument, Heightmap.Types> createHeightMapAdapter() {
        Heightmap.Types[] allowedEnumConstants = Arrays.stream(Heightmap.Types.values())
                .filter(Heightmap.Types::keepAfterWorldgen)
                .toArray(Heightmap.Types[]::new);
        return new EnumArgumentAdapter<>(
                "A height map type.",
                allowedEnumConstants,
                EnumArgumentAdapter::lowerCaseName
        );
    }

}
