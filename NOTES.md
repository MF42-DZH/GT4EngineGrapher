# Working Notes

- `torquevol` in `ENGINE` is just a flat multiplier on all torque points.
- Stock parts do absolutely nothing to the engine.
- For simple upgrades where `torquemodifier1` = `torquemodifier2`, it acts as a flat torque multiplier across all RPMs.
- The four complex upgrades are:
  1. Engine Balancing (does it change the rev limit or not?)
  2. NA Tuning (does it also affect the rev limit?)
  2. Supercharger (how does it affect torque?)
  3. Turbocharger (how does it affect torque, but probably more difficult to tell?)
- There is an order to how each upgrade is applied. See the [SpecDB page on the GT Modding Hub](https://nenkai.github.io/gt-modding-hub/concepts/specdb/#torque-modifiers) for more information.
- Nitrous is applied last outside of tuning, while it is actively used. It is another flat multiplier.
