/*
 * Copyright © Wynntils 2022.
 * This file is released under AGPLv3. See LICENSE for full license details.
 */
package com.wynntils.mc.extension;

import com.wynntils.handlers.item.ItemAnnotation;

public interface ItemStackExtension {
    ItemAnnotation getAnnotation();

    void setAnnotation(ItemAnnotation annotation);

    String getOriginalName();

    void setOriginalName(String name);
}