package net.steveson.stackedarmortrimsforgeport;

import net.minecraft.world.level.GameRules;

public class StackedArmorTrimsForgeGameRules {
//    public static GameRules.Key<net.minecraft.world.level.GameRules.IntegerValue> MAX_TRIM_STACK;
//    public static GameRules.Key<GameRules.BooleanValue> ALLOW_DUPLICATE_TRIMS;

    public static GameRules.Key<GameRules.BooleanValue> ALLOW_DUPLICATE_TRIMS = GameRules
            .register("allowDuplicateTrims", GameRules.Category.MISC, GameRules.BooleanValue.create(false));
    public static GameRules.Key<GameRules.IntegerValue> MAX_TRIM_STACK = GameRules
            .register("maxTrimStack", GameRules.Category.MISC, GameRules.IntegerValue.create(6));

//    public static void setupGamerules() {
//        MAX_TRIM_STACK = GameRuleRegistry.register("maxTrimStack", GameRules.Category.MISC, GameRuleFactory.createIntRule(100, 0, 1000));
//        ALLOW_DUPLICATE_TRIMS = GameRuleRegistry.register("allowDuplicateTrims", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));
//    }
}
