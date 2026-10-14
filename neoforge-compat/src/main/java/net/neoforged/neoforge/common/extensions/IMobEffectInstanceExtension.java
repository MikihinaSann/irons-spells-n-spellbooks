/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.neoforge.common.extensions;

import java.util.Set;
import net.neoforged.neoforge.common.EffectCure;

/**
 * NeoForge's patched {@code MobEffectInstance#getCures()} — injected onto
 * {@code MobEffectInstance}; the cures set is filled by the owning
 * {@code MobEffect#fillEffectCures}.
 */
public interface IMobEffectInstanceExtension {
    Set<EffectCure> getCures();
}
