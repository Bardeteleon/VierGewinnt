# VierGewinnt

This application lets you play Connect4 (German: VierGewinnt) with your friends. You can either play a classic game with 6 by 7 field or customize the field to other sizes. Since this is a digital version of a game it allows for special modifications like adding *bomb chips* which you can enable optionally and they enable new tactics.

Game Field:

<img src="/img/game_field.png" width="450">

Game Config:

<img src="/img/game_config.png" width="250">

Lobby:

<img src="/img/lobby.png" width="450">

# Setup

The game is implemented as a Server-Client application. Thus there need to be one server where the clients can connect to.

## Server

1. Run VierGewinntServer
2. Press the button to start it (default port 1000 is used)
3. Configure port forwarding (if you have players outside your local network):
    * Open the config page of your router
    * Setup portforwarding to the computer where the server runs (find out its local IP) with port 1000 (default)
    * **Important:**
        * Players outside the servers local network must use the routers public IP
        * Players inside the servers local network just use its local IP (usually something like 192.168...)
    
## Client

1. Start VierGewinntClient
2. Click "Lobby" > "Verbinden"(Connect)
3. Enter a nick, the IP of the server and the port (1000)
4. Click "Verbinden"

# How to play

1. In the Lobby click "Spielen" and configure your game
2. Click "Anfrage Senden" and wait for a reply of your teammate
3. You are read to go!
4. Controls:
    * Left/Right arrow -> Move chip left/right
    * Down arrow -> Insert
    * Up arrow -> Change between normal chip and bomb (if enabled)
    * Explosion: Double click on your bomb in the field when its your turn
  
Hint: The game chat is equal to the whipser chat

## How to build and deploy

0. Go to VierGewinnt directory
1. Run: javac -d bin --module-path src $(find src -name "*.java")
2. Copy: src/images (and src/Help.txt) to bin/images (and bin/Help.txt)
3. Run: jlink --module-path "bin;$JAVA_HOME/jmods" --add-modules vierGewinnt --launcher VierGewinnt=vierGewinnt/vierGewinnt.StartClient --output dist --strip-debug --compress 2 --no-header-files --no-man-pages
4. Run: jpackage -n VierGewinnt --type app-image -m vierGewinnt/vierGewinnt.StartClient --runtime-image dist


