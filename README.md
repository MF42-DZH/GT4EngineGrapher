# Gran Turismo Engine Grapher

[![Master Build](https://github.com/MF42-DZH/GT4EngineGrapher/actions/workflows/main.yml/badge.svg)](https://github.com/MF42-DZH/GT4EngineGrapher/actions/workflows/main.yml)

This is a program trying to replicate the power/torque graphs you see in GT4 in the tuning menu.

All data is sourced from the SpecDB databases of the game (whose SQLite files were sourced from [Nenkai/GT4SaveEditor](https://github.com/Nenkai/GT4SaveEditor)).

Currently, torque and power units are fixed to `kgf.m` and `PS` respectively.

### High Priority Issues

- https://github.com/MF42-DZH/GT4EngineGrapher/issues/8

### Usage

The grapher can be run as a standalone program with no arguments, but launching from the terminal will yield some customizability. Pass in `--help` or `-h` to the program for more information.

- If building and running (or running a prebuilt) JAR, use Java 21.
- If running from an installed instance, run the program executable installed onto your system. It has a JRE packaged along with all the necessary dependencies.

## Used Libraries

Reading the SpecDB SQLite databases is done using [Slick](https://scala-slick.org/) and [xerial/sqlite-jdbc](https://github.com/xerial/sqlite-jdbc).

The graphs are powered by [JFreeChart](https://www.jfree.org/jfreechart/), and as such, you can right-click on the chart and save an image of the chart.

## Screenshots

### Parts Picker

This UI style will be system-dependent. Windows and Mac OS uses Java's system-compliant look-and-feel, and Linux uses Java's default crossplatform look-and-feel.

![Parts Picker](https://raw.githubusercontent.com/MF42-DZH/GT4EngineGrapher/dev/img/PartsPicker.png)

### Graph

![Torque / power graph for the HPA Stage II R32](https://raw.githubusercontent.com/MF42-DZH/GT4EngineGrapher/dev/img/EngineGraph.png)

### Wear Editor

![Wear Editor for Oil and Engine Wear](https://raw.githubusercontent.com/MF42-DZH/GT4EngineGrapher/dev/img/WearEditor.png)

### Graph

Shift-click on `Map Engine` to obtain a rudimentary shopping list for the required upgrades and maintenance.

![Shopping List of Upgrades](https://raw.githubusercontent.com/MF42-DZH/GT4EngineGrapher/dev/img/ShoppingList.png)

## Special Thanks

- **TeaKanji**: For inspiring me to go down this rabbit-hole.
- **Ablationer**: From the GT Modding Discord, for helping me with torque modifiers for upgrades (and inadvertently helping me rubber-duck debug this program).
