package br.alkazuz.terrenos.utils;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public enum EntityName {

    ALLAY("Allay"),
    AREA_EFFECT_CLOUD("Area de Efeito de Poção"),
    ARMOR_STAND("Suporte para Armaduras"),
    AXOLOTL("Axolote"),
    ARROW("Flecha"),
    BAT("Morcego"),
    BEE("Abelha"),
    BLAZE("Blaze"),
    BOAT("Barco"),
    CAT("Gato"),
    CAVE_SPIDER("Aranha da Caverna"),
    CHICKEN("Galinha"),
    CHEST_BOAT("Barco com Baú"),
    COD("Bacalhau"),
    COMPLEX_PART("Desconhecido"),
    COW("Vaca"),
    CREEPER("Creeper"),
    DOLPHIN("Golfinho"),
    DONKEY("Burro"),
    DRAGON_FIREBALL("Bola de Fogo"),
    DROPPED_ITEM("Item dropado"),
    DROWNED("Afogado"),
    EGG("Ovo"),
    ELDER_GUARDIAN("Guardião Mestre"),
    ENDER_CRYSTAL("Cristal do End"),
    ENDER_DRAGON("Dragão do Fim"),
    ENDER_PEARL("Pérola do Fim"),
    ENDER_SIGNAL("Olho do Fim"),
    ENDERMAN("Enderman"),
    ENDERMITE("Endermite"),
    EVOKER("Invocador"),
    EVOKER_FANGS("Presas do Invocador"),
    EXPERIENCE_ORB("Orb de Experiência"),
    FALLING_BLOCK("Bloco Caindo"),
    FIREBALL("Bola de Fogo"),
    FIREWORK("Fogos de Artifício"),
    FISHING_HOOK("Isca da Vara de Pesca"),
    FOX("Raposa"),
    FROG("Sapo"),
    GHAST("Ghast"),
    GIANT("Zumbi Gigante"),
    GLOW_ITEM_FRAME("Moldura Brilhante"),
    GLOW_SQUID("Lula Brilhante"),
    GOAT("Cabra"),
    GUARDIAN("Guardião"),
    HORSE("Cavalo"),
    HOGLIN("Hoglin"),
    HUSK("Zumbi do Deserto"),
    ILLUSIONER("Ilusionista"),
    IRON_GOLEM("Golem de Ferro"),
    ITEM_FRAME("Moldura"),
    LEASH_HITCH("Desconhecido"),
    LIGHTNING("Raio"),
    LINGERING_POTION("Poção"),
    LLAMA("Lhama"),
    LLAMA_SPIT("Cuspe de Lhama"),
    MAGMA_CUBE("Cubo de Magma"),
    MARKER("Marcador"),
    MINECART("Carrinho"),
    MINECART_CHEST("Carrinho com Baú"),
    MINECART_COMMAND("Carrinho com Bloco de Comando"),
    MINECART_FURNACE("Carrinho com Fornalha"),
    MINECART_HOPPER("Carrinho com Funil"),
    MINECART_MOB_SPAWNER("Carrinho com Gerador de Monstros"),
    MINECART_TNT("Carrinho com Dinamite"),
    MULE("Mula"),
    MUSHROOM_COW("Vaca de Cogumelo"),
    OCELOT("Jaguatirica"),
    PAINTING("Pintura"),
    PANDA("Panda"),
    PARROT("Papagaio"),
    PHANTOM("Phantom"),
    PIG("Porco"),
    PIGLIN("Piglin"),
    PIGLIN_BRUTE("Piglin Bárbaro"),
    PIG_ZOMBIE("Porco Zumbi"),
    PILLAGER("Saqueador"),
    PLAYER("Player"),
    POLAR_BEAR("Urso Polar"),
    PRIMED_TNT("Dinamite"),
    PUFFERFISH("Baiacu"),
    RABBIT("Coelho"),
    RAVAGER("Devastador"),
    SALMON("Salmão"),
    SHEEP("Ovelha"),
    SHULKER("Shulker"),
    SHULKER_BULLET("Dardo de Shulker"),
    SILVERFISH("Silverfish"),
    SKELETON("Esqueleto"),
    SKELETON_HORSE("Cavalo Esqueleto"),
    SLIME("Slime"),
    SMALL_FIREBALL("Bola de Fogo Pequena"),
    SNOWBALL("Bola de Neve"),
    SNOWMAN("Golem de Neve"),
    SPECTRAL_ARROW("Flecha Espectral"),
    SPIDER("Aranha"),
    SPLASH_POTION("Poção Arremessável"),
    SQUID("Lula"),
    STRIDER("Lavagante"),
    STRAY("Esqueleto Vagante"),
    TADPOLE("Girino"),
    THROWN_EXP_BOTTLE("Frasco de Experiência"),
    TIPPED_ARROW("Flecha"),
    TRADER_LLAMA("Lhama"),
    TRIDENT("Tridente"),
    TROPICAL_FISH("Peixe Tropical"),
    TURTLE("Tartaruga"),
    UNKNOWN("Desconhecido"),
    VEX("Fantasma"),
    VILLAGER("Aldeão"),
    VINDICATOR("Vingador"),
    WANDERING_TRADER("Vendedor Ambulante"),
    WARDEN("Defensor"),
    WEATHER("Chuva"),
    WITCH("Bruxa"),
    WITHER("Wither"),
    WITHER_SKELETON("Esqueleto Wither"),
    WITHER_SKULL("Cabeça do Wither"),
    WOLF("Lobo"),
    ZOGLIN("Zoglin"),
    ZOMBIE("Zumbi"),
    ZOMBIE_HORSE("Cavalo Zumbi"),
    ZOMBIE_VILLAGER("Aldeão Zumbi"),
    ZOMBIFIED_PIGLIN("Piglin Zumbi");

    private String name;

    EntityName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static EntityName valueOf(Entity entity) {
        return valueOf(entity.getType());
    }

    public static EntityName valueOf(EntityType entityType) {
        return valueOf(entityType.name());
    }

    @Override
    public String toString() {
        return this.name;
    }

}