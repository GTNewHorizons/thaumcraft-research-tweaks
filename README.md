# Thaumcraft 4 research table tweaks

This mod brings a reworked research table GUI.

![new UI](doc/example-gui.png)

## Features
Tutorial and research pages where expanded to include explanations on new features.

All added features are showcased in video format:
- [part 1](https://youtu.be/Q0d8swslIv4)
- [part 2](https://youtu.be/nMNMRcZpb9E)

### Aspect pallets
Aspects pallets are now placed on both sides of the research table.
They split between pallets based on their affinities to order and entropy.
Sorting is based of aspect complexity (products are always below components).

### Aspect combination
Aspect combination area was replaced by drag & drop.
You can also combine aspects while dragging by right-clicking, this way you don't stop dragging.
This also can be used when doing research to place same aspect in multiple hexes.

### Aspect batch combination
It is gated behind research expertise and allows combining aspects up to 10 at a time.
To perform it hold ctrl while combining aspects.
You can also do the same with auto combination unlocked in research mastery.

### Research duplication
Copy button is active only when all required components present (aspects, paper, ink).



## Roadmap

### Functional
Nice to have
- Configurable bonus aspect particle (change size/turn off)
- Batch aspect combination in one packet (see PacketAspectCombinationToServer)
- Stable aspect layout (aspect is always in same position, even if this means having gaps)
- "Info" element (question mark icon?) which explains how to use the table
- Old style combine section between scribbling tools and copy button

### Non-functional
Nice to have:
- Migrate gradle to kts
- Automatic Testing

## Development
To run client with specific username set it in `PLAYER_USER_NAME` environment variable.

## Known issues

### Aspect from mod X is not showing up in the aspect pools.

**Probable Cause**: Mod X creates its aspects on `FMLLoadCompleteEvent` or later
which causes aspect caching of this mod to miss it.

**Solutions**:
1. Move aspect creation of mod X earlier (e.g. FMLInitializeEvent).
   This would be a preferred solution since aspects should be created early if possible.
2. Drop `AspectTree` initialization in `elan.tweaks.thaumcraft.research.frontend.integration.proxies.ClientSingletonInitializer`
   (two lines, one creating the tree and second one printing).
   This will cause caching on first UI opening.
   This is not crucial, but somewhat reduces UX, since it will cause a small delay

## Credits
Thanks go to:
- Think for texturing my rough ui sketch
- TimeConqueror and GTNH dev's for helping out on technical part
- Azanor for his amazing mod
