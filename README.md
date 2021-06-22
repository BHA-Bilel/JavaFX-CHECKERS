# JavaFX-CHECKERS 🕹️ - version 1.0
<p align="left"> <img src="https://komarev.com/ghpvc/?username=bha-bilel&label=Project%20views&color=0e75b6&style=plastic" alt="bha-bilel" /> </p>

> Play the famous classic Checkers game with your friends!
> 
> Jump to the [Setup](#install-instructions) section to to download the game, enjoy!
> 
> Discover [How to play](#how-to-play) and give me your [Feedback](#feedback) on this project.
> 
> You can find 5 more board games in the [Collection](#collection) section, give them a try!

## Table of contents
* [Second try](#second-try)
* [Game Charachteristics](game-charachteristics)
* [Install Instructions](#install-instructions)
* [How to play](#how-to-play)
* [Collection](#collection)
* [Additional info](#additional-info)
* [Feedback](#feedback)
* [Copyright notice](#copyright-notice)

# Second try
Checkers is the **second** learning project of my collection, still an easy target but comes with more challenge!

This time around I had to deal with **moving** Pieces and **attack** moves, it was rather a gui/logic challenge than a networking one, I was in fact surprised as I discovered how easy the **networking** part was.
###### The project in its current state is way more featured and structured than a basic checkers game

# Game Charachteristics
- Tabletop view (2D)
- Multiplayer (no AI)
- Light and dark themes
- Local and online mode
- Distributed (client/server)
- Socket programming (TCP + MultiCast)
- Three languages (English, French and Arabic)
- Message oriented communication (except game app)
- Supports **King** pieces, rewarded when reaching the opponent's utmost side
- **Kings** are special pieces that can reach the farthest ends of the board in one move
- however, it doesn't allow consecutive attack moves

# Install instructions
- First, you need to download the adequate version based on your OS:
  - windows: [64 bit](./setup/checkers-win64.exe?raw=true), or [32 bit](./setup/checkers-win32.exe?raw=true)
  - macOS: [10.10 or later](./setup/checkers-mac.zip?raw=true)
  - linux: [s390x](./setup/checkers-linux-s390x.zip?raw=true)

###### app executables created using jlink, and only tested on windows 7/10, I would appreciate if someone test it on mac/linux

After downloading, extract the archive to whatever location you want.

- Where to find game executable?
  - If you have mac or linux:
    - Navigate to the 'bin' folder inside the extracted folder
    - You will find 'checkers.bat' which is the game executable
  - If you have windows, it's called 'launcher.bat' inside the extracted folder

**WARNING**: **MAKE SURE** you allow program through firewall and run it as administrator

# How to play

The first time you launch the game you'll be prompted with welcome alerts, that will let you pick your preferred language and theme, and teach you some shortcuts, after that you need to choose between local and online mode to get started.

![Main app gui](./screenshots/mainApp.png)

In the main menu, you'll need to enter a username, then either 
- Host a room.
- Join public/local rooms.
- or Join a specific room through its ID (only available in online mode).

![Join app gui](./screenshots/joinApp.png)

In the join menu, you'll either see all (non full) rooms in local mode, or a list of public rooms in online mode.

![Room app gui](./screenshots/roomApp.png)

After joining a **room** the player is presented with this gui, where he can:
- Start a **chat** with people already in the room
- Change his **name**
- Take an empty **place**
- Set his **ready** status

The host has in addition the ability to:
- Change the room's **privacy** to either public/private (only available in online mode)
- **Kick** someone out of the room
- **Start** the game

Host privileges are **passed** automatically to the next player if the host leaves the room

![Game app gui](./screenshots/gameApp.png)

The game works as a regular Checkers game but in a distributed environment, 
you can end the game without leaving the room using the **Game** menu on top.

# Collection
- [XO](https://github.com/BHA-Bilel/JavaFX-XO)
- Checkers (current)
- [Chess](https://github.com/BHA-Bilel/JavaFX-CHESS)
- [Connect4](https://github.com/BHA-Bilel/JavaFX-CONNECT4)
- [Dominoes](https://github.com/BHA-Bilel/JavaFX-DOMINOS)
- [Coinche](https://github.com/BHA-Bilel/JavaFX-COINCHE)

# Additional info
- The game will create config files in "your_home_directory/.BG/.CHECKERS" to persist necessary info and your preferences updated, if you suddenly lose access to the game it may be that they became corrupted,  delete them and they'll be replaced with default ones automatically.
- For the time being, you can only play the game locally, as the [online server](https://github.com/BHA-Bilel/BG-SERVER) isn't deployed yet, if the project get a noticeable audience (which is very unlikely) I will host the server on some cloud service, but I will need some financial support.
- JavaFX apps can be deployed to android/ios devices so you might see some cross-platfom support in the future!
- All the collection was initially developed in **Eclipse IDE** in late 2019, before I migrated to **Intellij IDEA** in 2021 to code the remaining parts while redesigning some parts to fit the new workflow model.

- This project wasn't my first nor my last experience coding in JavaFX, I'll do my best to publish other projects on my GitHub.

- **All** of the projects/repositories in my profile are produced by an **individual** effort, that I coded from **scratch**. However, I had to watch some Youtube tutorials , specifically [JavaFX Game: Checkers](https://www.youtube.com/watch?v=6S6km5duBrM) by **Almas Baimagambetov**.

# Feedback
What do you think of this project? leave your thoughts/recommendations!

<p align="center">
  <a href="https://gist.github.com/BHA-Bilel/611ba000cb650850990b3b383fe4f591">
    <img src="https://gist.githubusercontent.com/BHA-Bilel/6eb01c298f0ccceff7511427afb52534/raw/ebb3b59e6e8af742699627d15672f28a1f144d26/feedback.gif" alt="Click here to give feedback!">
  </a>
</p>

# Copyright notice
This public repository contain purposely **unlicensed** source code (**NOT** open-source), 
that I only consider as a personal side project and a way to showcase my skills.
You can surely and gladly download and play my game, or view how it's made.

However, **I DO NOT** grant any kind of usage (Commercial, Patent, Private), Distribution or Modification of the source code contained in this repository.
For a **private** license agreement please contact me at: bilel.bha.pro@gmail.com
