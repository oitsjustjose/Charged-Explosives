package com.oitsjustjose.charged_explosives.common;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

public class Util {
    public static Component translateOrFallback(String transKey) {
        try {
            return new TranslatableContents(transKey).resolve(null, null, 0);
        } catch (CommandSyntaxException e) {
            MutableComponent m = MutableComponent.create(ComponentContents.EMPTY);
            m.append("Failed to resolve key ");
            m.append(transKey);
            return m;
        }
    }
}
