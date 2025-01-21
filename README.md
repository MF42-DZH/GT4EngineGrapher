# Gran Turismo Engine Grapher

[![Master Build with JPackage](https://github.com/MF42-DZH/GT4EngineGrapher/actions/workflows/main.yml/badge.svg?branch=master)](https://github.com/MF42-DZH/GT4EngineGrapher/actions/workflows/main.yml)

This is a program trying to replicate the power/torque graphs you see in GT4's tuning menu.

All data is sourced from the SpecDB databases of GT4 (whose SQLite files were sourced from [Nenkai/GT4SaveEditor](https://github.com/Nenkai/GT4SaveEditor)), or extracted from GT4 Spec II using [Razer2015/GT4FS](https://github.com/Razer2015/GT4FS), or extracted from GT3 using [pez2k/GT3VOLExtractor](https://github.com/pez2k/gt2tools/tree/master/GT3VOLExtractor) and [Nenkai/GTDataSQLiteConverter](https://github.com/Nenkai/GTDataSQLiteConverter) by ddm and Nenkai.

Currently, torque and power units are selectable, but any units that aren't `kgf.m` or `PS` may not convert accurately to what is shown in-game, as we do not have the unit conversion constants as of right now.

### High Priority Issues

- https://github.com/MF42-DZH/GT4EngineGrapher/issues/8 - Power inaccuracies when upgrades get involved.

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

### Display Options

![Display Options Picker](https://raw.githubusercontent.com/MF42-DZH/GT4EngineGrapher/dev/img/DisplayOptions.png)

### Wear Editor

![Wear Editor for Oil and Engine Wear](https://raw.githubusercontent.com/MF42-DZH/GT4EngineGrapher/dev/img/WearEditor.png)

### Shopping List

Shift-click on `Map Engine` to obtain a rudimentary shopping list for the required upgrades and maintenance.

![Shopping List of Upgrades](https://raw.githubusercontent.com/MF42-DZH/GT4EngineGrapher/dev/img/ShoppingList.png)

## Special Thanks

- **Nenkai**, **pez2k**, **ddm**: Data extractor tools for GT3 and GT4.
- **TeaKanji**: For inspiring me to go down this rabbit-hole.
- **Ablationer**: From the GT Modding Discord, for helping me with torque modifiers for upgrades (and inadvertently helping me rubber-duck debug this program).
