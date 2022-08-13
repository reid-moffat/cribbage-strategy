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

I started this project since I played a lot of cribbage during the beginning of COVID-19 times and wanted to improve my skill at the game; after a while I felt that I already knew all the main strategies, but lacked knoweldge on the optimal usage of said strategies and their effectiveness in certain scenarios. But since there are so many possibilities for what could happen, it gets harder and harder to improve as time goes on and I felt like I had basically plateaued.

So I made this project, it didn't really take that long (few days of coding for it to work fine) and I got good use out of it but after some time it sort of evolved into more of a sandbox for learning coding. The project was correct, but had lots of room for technical improvement so over the next while I would apply new techniques, tools and idea to this project. After some time, it's become somewhat overboard on QA- more than enough test cases, plenty of documentation, heavy refactoring, integration with maven and the usage of Junit, Intellij and github copilot; I found this even more interesting than the project itself.

In the future, I hope to expand on this project and maybe add it to a full website where it's easy for anyone to use. But overall, while this is quite a simple project that could probably be whipped up by anyone fairly easily, I've learned a lot of important things from this and hope to apply any new ones here too. It's really got me interested in proper code quality and testing, and hope that from all the skills I've learned from building on this somewhat trivial project I'll be able to make larger and more useful software more effectively in the future.
