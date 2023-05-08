package com.mel.meltributes.util

data class Attribute(val type: AttributeType?, val level: Int)

enum class AttributeType(val display: String, val id: String) {
    ARACHNO("Arachno", "arachno"),
    ARACHNO_RESISTANCE("Arachno Resistance", "arachno_resistance"),
    ATTACK_SPEED("Attack Speed", "attack_speed"),
    BLAZING("Blazing", "blazing"),
    BLAZING_FORTUNE("Blazing Fortune", "blazing_fortune"),
    BLAZING_RESISTANCE("Blazing Resistance", "blazing_resistance"),
    BREEZE("Breeze", "breeze"),
    COMBO("Combo", "combo"),
    DEADEYE("Deadeye", "deadeye"),
    DOMINANCE("Dominance", "dominance"),
    DOUBLE_HOOK("Double Hook", "double_hook"),
    ELITE("Elite", "elite"),
    ENDER("Ender", "ender"),
    ENDER_RESISTANCE("Ender Resistance", "ender_resistance"),
    EXPERIENCE("Experience", "experience"),
    FISHERMAN("Fisherman", "fisherman"),
    FISHING_EXPERIENCE("Fishing Experience", "fishing_experience"),
    FISHING_SPEED("Fishing Speed", "fishing_speed"),
    FORTITUDE("Fortitude", "fortitude"),
    HUNTER("Hunter", "hunter"),
    IGNITION("Ignition", "ignition"),
    INFECTION("Infection", "infection"),
    LIFE_RECOVERY("Life Recovery", "life_recovery"),
    LIFE_REGENERATION("Life Regeneration", "life_regeneration"),
    LIFELINE("Lifeline", "lifeline"),
    MAGIC_FIND("Magic Find", "magic_find"),
    MANA_POOL("Mana Pool", "mana_pool"),
    MANA_REGENERATION("Mana Regeneration", "mana_regeneration"),
    MANA_STEAL("Mana Steal", "mana_steal"),
    MIDAS_TOUCH("Midas Touch", "midas_touch"),
    SPEED("Speed", "speed"),
    TROPHY_HUNTER("Trophy Hunter", "trophy_hunter"),
    UNDEAD("Undead", "undead"),
    UNDEAD_RESISTANCE("Undead Resistance", "undead_resistance"),
    VETERAN("Veteran", "veteran"),
    VITALITY("Vitality", "mending"), // bastard
    WARRIOR("Warrior", "warrior");

    companion object {
        fun fromID(id: String) = AttributeType.values().find { it.id == id }
    }
}