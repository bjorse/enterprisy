# Development log
This is a development log for each day anything was done on the project. Each post briefly covers completed tasks of the day and sums up time spent (expressed as hours).

## 2015-06-06
Alright. The "Client data service" is now fetching and adding from/to a PostgreSQL database that is hosted on my Xubuntu machine. Finally!

Next up is getting RabbitMQ up and running!

**Time spent:** 3

## 2015-06-05
Finally got the work order GUI to work(ish) in the client page (with filtering). You learn something new every day!

Another big thing is that the first backend service is LIVE! The first one to hit the hot spot is the "Client data service", which of course will provide this excellent application with some client data. A wonderful feeling this is, although it still only handles static data. But I've installed PostgreSQL on my Xubuntu machine which I will try to reach tomorrow through code. So, perhaps I'm finally leaving some of the static data behind me soon!

I'll have to upload this project on GitHub for backup purposes too. I keep telling myself that I don't got the time, but deep down I know it just a few minutes of work and I'm just being lazy (or just cherry picking all the fun stuff).

**Time spent:** 2

## 2015-06-04
Trouble in paradise! Today I've been having some problems getting my code to work, and I'm a bit depressed about it. I'm still not done with work orders and it's really bugging me. Oh well, perhaps tomorrow I'll get it right...

**Time spent:** 3

## 2015-06-03
Started working with work orders which comes along pretty well. The web server returns data so next up is getting the GUI to present it!

**Time spent:** 0.5

## 2015-06-02
Good news! My cold is finally starting to loosen up its grip on me, allowing me to think more clearly now.

I have worked on the client page and will begin on work orders tomorrow!

**Time spent:** 1.5

## 2015-06-01
Just got information from my boss that my deadline is (a lot) earlier than I expected - 2016-06-17 (I expected at least another week on top of that). It will be really tough to accomplish what I want so I need to focus a lot more from now on. I'm feeling decent in the development process now, so the remaining GUI stuff will probably progress a lot faster. I expect to start on backend in just a few days, otherwise it will never get finished in time.

Now I'm creating some static data for work orders and figuring out how that domain should look and what it should do. The most important thing with work orders is its status process which will create the todo items. I'll try to keep it really simple.

I've also created a menu to the left. It is not fully functional of course since not all pages are there yet, but it's a good start!

**Time spent:** 2

## 2015-05-31
Alright! I'm like almost totally done with the add client form (with server side validation and client side representation of validation errors). It was a delight putting it all together. Next up is the client detail page.

**Time spent:** 3

## 2015-05-30
Yay! Finally got the add client form to actually work! And yet again, I'm amazed how Clojure and Reagent/React works. Now it's time to post some data to the server!

**Time spent:** 2.5

## 2015-05-29
Started working on the add client page. It was trickier than I initially thought. You think you finally understand something, but then comes the "you-suck-and-knows-nothing"-hammer and slams you down to the ground again.

Because of this, I of course got less done today than I actually expected, though getting a modal up and running (as a component) was surpisingly easy!

**Time spent:** 2.5

## 2015-05-28
With the clients page ready to show some "real" data, I fixed a bug in the client detail page where the page endlessly fetched client data from the server. I appearantly still got a lot to learn about the state handling in React/Reagent.

**Time spent:** 1

## 2015-05-27
Finally some REAL breakthrough! Now I can query from the client side and filter on the server side (only static data so far though). I'm going to stick to static data for a while and aim to get the client detail page up and running with some "real" data before moving on. Adding/editing clients would be nice as well.

Good times (although an eye infection and a computer screen is not the best of buddies)!

**Time spent:** 2

## 2015-05-26
Minor breakthrough in getting the communication going. I now can communicate with the backend, but so far there's no data transferred from my queries. Also I'm suffering from a cold which severely shortens the time I want to stay awake in the evening. Damn you germs!

**Time spent:** 1.5

## 2015-05-25
Started working on getting some backend up and running. By backend I mean the web server which will return static data until the real backend services are implemented :).

I'm also trying to understand how Compojure (and Ring) works to figure out how to return JSON responses where appropriate. The web server will, in addition to serving static html pages, also host a JSON API under /data/ as well, to simplify(?) things.

**Time spent:** 0.5

## 2015-05-24
I'm giving myself some time to understand the Flux architecture to know the appropiate data flow through the views.

I'm thinking that the web server itself should aggregate data and transfer requests instead of the client going directly at the web services. The problem is that then the web server does a lot. What is the best solution to this?

On a side note: when the day comes to implement backend services, I'll use this framework to test consumer driven contracts: https://github.com/realestate-com-au/pact

**Time spent:** 3

## 2015-05-23
I've learned a lot more about React (and Clojure) today and how to put it together in preferred ways. I've found a great project (https://github.com/yogthos/yuggoth) to use as a code reference.

The client pages (both the one where you list/search clients, and the individual client detail page) is coming along nicely and I'm getting ready to use some real backend data. Yay!

**Time spent:** 4

## 2015-05-22
Alright, today I started developing a GUI for handling clients, first by creating some static data to use (client side). I then implemented a list of clients (by using a table, yay for UX) and a filter function which worked pretty well. It's still a lot to learn before being really effective, but I feel like I'm on the right track.

**Time spent:** 4

## 2015-05-21
Today I realised that I had to decide whether to use authentication/authorization in this demo project. I decided not to, for several reasons. First of all it is not important in the context of learning Clojure and React. Also I read an article concerning implementing Friend (which seems to be THE library for dealing with this stuff in Clojure) and it seems pretty tricky to get it up and running fast. So I'll skip it for now and maybe implement it later if I find it necessary, because at this point it just feels like wasted time.

I also read a lot about React (and Reagent) and thought about how the structure should be in my web-main project (which is the first GUI I'll develop). More importantly I thought about what the hell this project should do and what data/domain it should focus on. We'll see what happens there, but I'll start with designing some kind of "client" interface :)

**Time spent:** 0.5

## 2015-05-20
I watched an amazing video (https://www.youtube.com/watch?t=10&v=h3KksH8gfcQ) and read another amazing presentation (http://murilopereira.com/the-case-for-reactjs-and-clojurescript/) which convinced me even further that I'm on the right track towards awesomeness by using React together with ClojureScript.

I decided to go with Reagent (https://github.com/reagent-project/reagent) to use as the ClojureScript implementation of React. I liked it better than Om because Reagent looked a bit more slim judging from the example code that was provided by each repo on GitHub. The Google searches said a lot of nice things about it as well and the learning curve seems short, allowing me to spend more time learning Clojure. Yay!

Reagent forced me to upgrade from version 1.6 to version 1.7 of Java and I expected a lot of trouble, but it actually went smooth. I guess that's how application installation goes down nowadays (on Mac, at least)!

The remaining time of the day I spent on getting some basic Bootstrap layout going. Too bad I never got Figwheel to take notice of my changes and update the web server for me, which forced me to manually reload the server like some kind of cave man. Oh well, at least I got SOME things done today...

**Time spent:** 4

## 2015-05-19
Today the decision was made to start the project. Languages and technologies to use (initially) are Clojure, ClojureScript, React, Bootstrap. I chose Bootstrap because I've done stuff in it before and I wont be doing any fancy design stuff, I just want to learn Clojure and React above all. Bootstrap allows me to get some basic GUI ready reasonable fast for that purpose.

The idea behind this project is to look beyond the traditional data-binding stuff that I'm currently doing in WPF/MVVM and instead try to solve it with functional programming. Also, I really miss developing for the web and this is a free time project to keep me sane as well!

I will start by focusing on getting some GUI running tomorrow before implementing any backend features.

**Time spent:** 1
