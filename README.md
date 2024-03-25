# GT4 Engine Grapher

This is a program trying to replicate the power/torque graphs you see in GT4 in the tuning menu.

All data is sourced from the SpecDB databases of the game (whose SQLite files were sourced from [Nenkai/GT4SaveEditor](https://github.com/Nenkai/GT4SaveEditor)).

Currently, torque units are stuck on `kgf.m` and power units are stuck on `PS`. Power is also inaccurate by about `±0.1 PS`, so if the graph outputs something like `600.55 PS @ 8000 RPM`, the power in GT4 is actually anywhere between `600.45` and `600.65`.

## Used Libraries

Reading the SpecDB SQLite databases is done using [Slick](https://scala-slick.org/) and an SQLite JDBC driver.

The graphs are powered by [JFreeChart](https://www.jfree.org/jfreechart/), and as such, you can right-click on the chart and save an image of the chart.

## Screenshots

### Parts Picker

![Parts Picker](https://raw.githubusercontent.com/MF42-DZH/GT4EngineGrapher/dev/img/PartsPicker.png)

### Graph

![Torque / power graph for the HPA Stage II R32](https://raw.githubusercontent.com/MF42-DZH/GT4EngineGrapher/dev/img/EngineGraph.png)

## Special Thanks

- TeaKanji: For inspiring me to go down this rabbit-hole.
- Ablationer: From the GT Modding Discord, for helping me with torque modifiers for upgrades (and inadvertently helping me rubber-duck debug this program).
