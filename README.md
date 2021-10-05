## Roadmap
### Functional
1. Must:
  - [x] Larger aspect pallet for research table
  - [x] Combining aspects via drag & drop
  - [x] Combining 10x aspects when CTRL pressed? (shift + alt + click on aspect in pallet)
  - [x] Combining multiple times when hovering (RMB)
  - [x] Research notes re-implementation
  - [x] Put aspect multiple times on notes when hovering (RMB)
  - [x] Make sure expertise and mastery rules about aspect conservation apply correctly
  - [x] Draw random runes on parchment when researching
  - [x] Missing ink message
  - [x] Research duplication
  - [x] Fix aspect combination notification rendering
  - [x] UI textures
  - [x] Clean-up dependency code
  - [ ] Clean up TODOs
  - [ ] Better aspect sorting (depth in tree + alphabetic order + side based on alignment to order,terra,water/perdition,ignis,air) - aspect product is always after it's components
  - [x] Ensure all sounds are played accordingly
  - [ ] Include checking inventory when deciding on duplicate button activity
  - [ ] Update lang file
  - [ ] Optimize memory consumption (caches)

2. Nice to have:
  - [ ] Testing
  - [ ] Batch aspect combination in one packet (see PacketAspectCombinationToServer)
  - [ ] Make aspect layout super stable (aspect is always in same position, even if this means having gaps)

### Non-Functional
  - [ ] Add CI/CD
  - [ ] Migrate and clean-up this and Forgelin 1.9 gradle
  - [ ] Fix asm trying to parse Forgelin zips and failing
  - [ ] Add proper logging
  - [ ] Check licensing

## Research table refactor

UI elements:
  - [x] Scribbling tools slot
  - [x] Research notes slot
  - [x] Research duplication button? (check how it worked)
     - not present when not unlocked
     - not active till finished & have unlocked duplication
     - shows cost tooltip
  - [x] Aspect pallet 4 columns by 13 rows (104 aspect slots)
     - Allows drag & drop aspect combination:
       - Shift + Left click & having research = create this aspect
       - Drop = combine x1
       - Drag + Right click = combine x1
       - (Drop || Drag + Right click) + Ctrl = combine 10
  - [x] Drawing paper
     - hex grid
       - individual hexes
        - can connect as lines
     - random runes?
  - [x] Player inventory

New UI:

![UI](src/main/resources/assets/thaumcraft/textures/research/table/research-table.png)

## Development
To run client with specific username set it in `PLAYER_USER_NAME` environment variable.

## Gratitude
Thanks go to: 
- Think for texturing my rough ui sketch
- TimeConqueror and GTNH dev's for helping out on technical part
- Azanor for his amazing mod
