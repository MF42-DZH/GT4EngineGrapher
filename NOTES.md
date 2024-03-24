# Working Notes

- `torquevol` in `ENGINE` is just a flat multiplier on all torque points.
- Stock parts do absolutely nothing to the engine.
- For upgrades where `torquemodifier1` = `torquemodifier2`, it acts as a flat torque multiplier across all RPMs.
  - Otherwise, apply a lerped modifier from `torquemodifier2` from the lowest RPM torque point to `torquemodifier1` on the highest RPM torque point.
- There is an order to how each upgrade is applied. See the [SpecDB page on the GT Modding Hub](https://nenkai.github.io/gt-modding-hub/concepts/specdb/#torque-modifiers) for more information.
- Nitrous is applied last outside of tuning, while it is actively used. It is another flat multiplier.
