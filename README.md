## Roadmap
### Functional
1. Must:
  - [x] Larger aspect pallet for research table
  - [x] Combining aspects via drag & drop
  - [x] Combining 10x aspects when CTRL pressed? (shift + alt + click on aspect in pallet)
  - [ ] Combining multiple times when hovering
  - [ ] Better aspect sorting (depth in tree + alphabetic order + side based on alignment to order,terra,water/perdition,ignis,air) - aspect product is always after it's components
  - [ ] Research notes
  - [ ] Research duplication
  - [ ] UI textures

2. Nice to have:
  - [ ] Batch aspect combination as one in one packet (see PacketAspectCombinationToServer)
  - [ ] Make aspect layout super stable (aspect is always in same position, even if this means having gaps)

### Non-Functional
  - [ ] Add CI/CD
  - [ ] Migrate and clean-up this and Forgelin 1.9 gradle
  - [ ] Fix asm trying to parse Forgelin zips and failing
  - [ ] Add proper logging
  - [ ] Check licensing

## Research table refactor

UI elements:
  - [x] (I) Scribbling tools slot
  - [x] (N) Research notes slot
  - [ ] (C) Research duplication button? (check how it worked)
     - not present when not unlocked
     - not active till finished & have unlocked duplication
     - shows cost tooltip
  - [ ] (1,2) Aspect pallet 4 columns by 13 rows (104 aspect slots)
     - Allows drag & drop aspect combination:
       - Shift + Left click & having research = create this aspect
       - Drop = combine x1
       - Drag + Right click = combine x1
       - (Drop || Drag + Right click) + Ctrl = combine 10
  - [ ] (6) Drawing paper
     - hex grid
       - individual hexes
        - can connect as lines
     - random runes?
  - [x] Player inventory

Mock:

![UI mock](src/main/resources/assets/thaumcraft/textures/research/table/research-table.png)

## Development
To run client with specific user name set it in `PLAYER_USER_NAME` environment variable.

## Notes
When drawing background he uses:
```
    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    GL11.glEnable(3042);
    ...
    draw backgrounds
    draw aspects
    ...
    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    RenderHelper.disableStandardItemLighting();
    draw research
```
find out what this changes

notifications are not drawing on top of ui, this should fix it:
```java
    protected void drawGuiContainerForegroundLayer(final int mx, final int my) {
        final Minecraft mc = Minecraft.getMinecraft();
        final long time = System.nanoTime() / 1000000L;
        if (PlayerNotifications.getListAndUpdate(time).size() > 0) {
            GL11.glPushMatrix();
            Thaumcraft.instance.renderEventHandler.notifyHandler.renderNotifyHUD(this.width, this.height, time);
            GL11.glPopMatrix();
        }
    }
```
