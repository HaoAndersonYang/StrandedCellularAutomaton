# Stranded Cellular Automaton

In order to analyze weaving products mathematically and find out valid weaving products, it is
natural to relate them to Cellular Automaton. In order to analyze the Stranded Cellular Automaton, I construct a
Java program and analyze various aspects of the automaton they created.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Installing

Clone the project to your machine and open the project with an IDE that supports Java 8. You will be able to develop and execute the program and investigate Stranded Cellular Automaton.

## Running the program

Explain how to use the project to investigate Stranded Cellular Automata.

### Generating a single weaving product

Select class "TextBasedInterface" and run it. You will be able to generate a single weaving product by following instructions.
Sample input following instructions (user input is **_emphasize_**):

Input the Turning Rule (0~511)

**_68_**

Input the Crossing Rule (0~511)

**_0_**

Input the height of the grid

**_9_**

Input the width of the grid (not less than 2)

**_9_**

Input the initial configuration in the following form:

<Turning Left><Turning Right><Crossing> <Turning Left><Turning Right><Crossing>...
  
For the turning rules, please use U for Upright. S for Slanted. N for No Strand

For the crossing rules, please use N for NoCross. L for LeftTop. R for RightTop.

**_UUN UUN UUN UUN SSR UUN UUN UUN UUN_**

If you want to decide whether the pattern holds together based on a full cycle enter
1. Otherwise enter 0.

**_1_**

### Brute force search

Select class "BruteForceSearch" and run. You can perform the brute force searching on the maximum cycle length and average number of distinct weaving patterns based on a given width. 

Sample input following instructions (user input is **_emphasize_**):

Please enter the width:

**_2_**

If you want to only check the case where all cells will have two strands, enter 1. Otherwise enter 0

**_1_**

## Authors

* **Hao Yang** - *Initial work*

## Acknowledgments

* I would like to thank Dr. Joshua Holden for the inspirations and helpful advices.
