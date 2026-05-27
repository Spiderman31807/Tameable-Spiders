package tamable.spiders;

import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.attachment.IAttachmentCopyHandler;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

import com.mojang.serialization.Codec;

public class CustomData {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, TamableSpidersMod.MODID);
	public static final Supplier<AttachmentType<Boolean>> Climbing = ATTACHMENT_TYPES.register("climbing", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).build());
}