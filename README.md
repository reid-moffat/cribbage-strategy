# cribbage-strategy

Picking which cards to drop in a cribbage hand is tricky because of the sheer amount of possible
combinations; you always end up with a guesstimate at best. Luckily, optimizing a cribbage hand
is simple for a program to calculate

There are several online calculators that can find points in a given hand and maybe give tips, but
this calculator can do more:

* Take a starting cribbage hand as input
* Calculate the best drop combinations with the highest average points
* Advise the user on other strategies they should take into account beyond just average points
  (e.g. don't drop a five when you don't have the crib)

If you would like to run this program, the entry point is UserInterface.java in src/main/java/main.
Instructions will be provided to the console

<p align="center">
    <img style="margin-left: auto; margin-right: auto;"
         src="https://user-images.githubusercontent.com/61813081/145729405-d7368ff3-130b-4d71-9fbd-ef92269470ce.png"
         alt="Program output example">
<p>

## References

If you are unfamiliar with cribbage, an in-depth guide can be found
[here](https://bicyclecards.com/how-to-play/cribbage/)

A handy scoring sheet can be found
[here](https://i.pinimg.com/originals/f8/c8/82/f8c8821f3094d75847767e61bc54319d.png)

## Notes

I made this project when I started to plateau after playing cribbage for a while, and it ended up being simpler that I
expected - just a few days of coding and it was working. But after some time, it transitioned into more of a playground
for software QA techniques; I realized that there was a lot that I could do to improve the project, and that Java has
plenty of great tools to add to it to improve the code. I've added plenty of features to the project - probably **too**
much that might not be required - but this was a great way for me to learn more about things like Intellij, Maven, Junit
and other tools & concepts.

Maybe sometime in the future I'll make this into a website so it's easy for everyone to use 🙂
