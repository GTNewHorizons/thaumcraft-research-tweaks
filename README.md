## Roadmap
### Functional
  - [ ] Larger aspect pallet for research table
  - [ ] Mixing aspects 10x when ALT pressed? (shift + alt + click on aspect in pallet)
  - [ ] Better aspect sorting (depth in tree + alphabetic order) - aspect product is always after it's components

### Non-Functional
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
