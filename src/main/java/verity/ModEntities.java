package verity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import verity.entity.VerityEntity;

public class ModEntities {
    public static final ResourceKey<EntityType<?>> VERITY_KEY =
            ResourceKey.create(Registries.ENTITY_TYPE, Verity.id("verity"));

    public static final EntityType<VerityEntity> VERITY = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            VERITY_KEY,
            EntityType.Builder.of(VerityEntity::new, MobCategory.CREATURE)
                    .sized(0.52f, 0.52f)
                    .build(VERITY_KEY));

    public static void init() {
        FabricDefaultAttributeRegistry.register(VERITY, VerityEntity.createAttributes());
    }
}