# cribbage-strategy

Picking which cards to drop in a cribbage hand for the optimal amount of points is often tricky;
directly calculating the optimal discard card(s) while playing is simply not feasible due to the
sheer amount of possibilities of drop combinations and starter cards

There are several online calculators that can find points in a given hand and maybe give tips, but
more can be done. The goal of this project is to:

* Take a starting cribbage hand as input
* Calculate the average number of points obtained from each combination of card(s) dropped
* Advise the user on other strategies they should take into account beyond just average points

## cribbage ##

If you are unfamiliar with cribbage, an in-depth guide can be
found [here](https://bicyclecards.com/how-to-play/cribbage/)

A handy scoring sheet can be
found [here](https://i.pinimg.com/originals/f8/c8/82/f8c8821f3094d75847767e61bc54319d.png)

## Try it out ##

If you would like to test this calculator, run UserInterface.java in src/main/java/main.
Instructions will be provided to the console

Currently, this program just completes the brunt work of calculating the average number of points
obtained for each drop combination. An option to indicate which player has the crib as well as other
strategies will be added in the future
