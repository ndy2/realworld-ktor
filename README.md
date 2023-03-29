# realworld-ktor

![image](https://user-images.githubusercontent.com/67302707/227979583-09b2cddc-23a0-4c4c-aa14-0240f6553dff.png)

[Ktor](https://ktor.io/) codebase containing real world examples (CRUD, auth, advanced patterns, etc) that adheres to the RealWorld spec and API.

This codebase was created to demonstrate a fully fledged fullstack application built with *Ktor* including CRUD operations, authentication, routing, pagination, and more.
We've gone to great lengths to adhere to the *Ktor* community styleguides & best practices.
For more information on how to this works with other frontends/backends, head over to the [RealWorld repo](https://github.com/gothinkster/realworld)!!

## It works with...
As above logo shows, it works mainly with four kotlin based frameworks/libraries. That is ..

- [`Ktor`](https://ktor.io/) - web server(netty)
- [`Exposed`](https://github.com/JetBrains/Exposed) - kotlin native orm
- [`Koin`](https://insert-koin.io/) - for dependency injection
- [`Kotest`](https://kotest.io/) - kotlin native test (supports property test)

This is my first project with above frameworks. I think there exists small amount of reference to find out the "Best Practice" to create web server in ktor. 
I will try my best to find out my own "Best Practice" in this project.

## Good Points
- cleanup `/build.gradle.kts` with buildSrc
  - [see here](https://github.com/ndy2/realworld-ktor/tree/main/buildSrc)
  - [ref](https://docs.gradle.org/current/userguide/organizing_gradle_projects.html#sec:build_sources)
- custimized `kotest with ktor`
  - kotest base spec with custom configuration - [code](https://github.com/ndy2/realworld-ktor/blob/main/src/test/kotlin/ndy/test/spec/BaseSpec.kt)
  - utility function that esiliy create Arb in property testing
    - [see here](https://github.com/ndy2/realworld-ktor/tree/main/src/test/kotlin/ndy/test/generator)
    - [inspired by](https://naver.github.io/fixture-monkey/)
- Password with PasswordDelegate
  - it is my first time to apply what is called [delegate property](https://kotlinlang.org/docs/delegated-properties.html). 
  It could probably get better... but I'm really satisfied with that

## Plan!
- I would play on [kotlin's context receiver](https://youtu.be/GISPalIVdQY) feature with this project. It must be fun!

## Questions
- I think that ktor/exposed support "light weight" server. So, I wonder is it worth enough to consist on DI or clean architecture pattern 
that I used to studied in the `Spring Framework`. Well that must be case by case. In this case I ommited lots of those values and focus on framwork/library usages

## Current Test with postman - with script provided [@here](https://github.com/gothinkster/realworld/tree/main/api)
![image](https://user-images.githubusercontent.com/67302707/227984143-cf1d120e-de0f-4017-8231-a56571750066.png)

## Getting started

```
./gradlew build
./gradlew run
```
