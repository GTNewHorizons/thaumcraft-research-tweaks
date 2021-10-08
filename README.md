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
  - [x] Ensure all sounds are played accordingly
  - [x] Clean-up dependency code
  - [x] Stable aspect sorting and affinity-based splitting
  - [x] Clean up TODOs
  - [x] Include checking inventory when deciding on duplicate button activity
  - [x] Update lang file

2. Nice to have:
  - [ ] Batch aspect combination in one packet (see PacketAspectCombinationToServer)
  - [ ] Make aspect layout super stable (aspect is always in same position, even if this means having gaps)

### Non-Functional
1. Must:
  - [x] Optimize memory consumption (cache hex map structure)
  - [ ] Add CI/CD
  - [x] Migrate and clean-up this and Forgelin 1.9 gradle
  - [ ] Fix asm trying to parse Forgelin zips and failing
  - [ ] Check licensing
  - [ ] Clean-up readme

2. Nice to have:
  - [ ] Testing

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

## Showcase
All added features are showcased in video format:
- [part 1](https://youtu.be/Q0d8swslIv4)
- [part 2](https://youtu.be/nMNMRcZpb9E)

## Credits
Thanks go to: 
- Think for texturing my rough ui sketch
- TimeConqueror and GTNH dev's for helping out on technical part
- Azanor for his amazing mod
