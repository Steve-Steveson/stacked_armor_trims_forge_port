package io.github.apfelrauber.stacked_trims;

import net.minecraft.world.level.GameRules;

public class StackedTrimGameRules {

    public static GameRules.Key<GameRules.BooleanValue> ALLOW_DUPLICATE_TRIMS = GameRules
            .register("allowDuplicateTrims", GameRules.Category.MISC, GameRules.BooleanValue.create(false));
    public static GameRules.Key<GameRules.BooleanValue> OVERRIDE_MATCHING_TRIM_PATTERNS = GameRules
            .register("overrideMatchingTrimPatterns", GameRules.Category.MISC, GameRules.BooleanValue.create(true));
    public static GameRules.Key<GameRules.IntegerValue> MAX_TRIM_STACK = GameRules
            .register("maxTrimStack", GameRules.Category.MISC, GameRules.IntegerValue.create(6));

}
