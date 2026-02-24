# 🎮 Hangman Swing (Kotlin GUI)

A desktop Hangman game built with **Kotlin** and **Java Swing**,
featuring:

-   Multiple categories (Science, Countries, Tech)
-   Easy and Hard difficulty modes
-   Score system
-   Persistent high score tracking
-   ASCII hangman drawing
-   File-based word loading

------------------------------------------------------------------------

## 📂 Project Structure

    kotlinGUI/
    │
    ├── build.gradle.kts
    ├── settings.gradle.kts
    ├── gradlew
    ├── gradlew.bat
    │
    ├── countries.txt
    ├── science.txt
    ├── tech.txt
    ├── highscores.txt
    │
    └── src/
        └── main/
            └── kotlin/
                └── Main.kt

------------------------------------------------------------------------

## 🛠 Technologies Used

-   Kotlin (JVM)
-   Java Swing (GUI)
-   Gradle (Kotlin DSL)
-   File I/O for persistent storage

------------------------------------------------------------------------

## 📦 Dependencies

This project does **not use any external GUI libraries**.

It relies on:

-   Kotlin Standard Library
-   Java Swing (included in JDK)
-   Gradle Kotlin DSL

All dependencies are managed inside:

    build.gradle.kts

No additional installation is required beyond Java and Gradle (wrapper
included).

------------------------------------------------------------------------

## 💻 Requirements

Make sure you have:

-   JDK 8 or higher
-   IntelliJ IDEA (recommended)
-   OR ability to run Gradle projects from terminal

Verify Java installation:

``` bash
java -version
```

------------------------------------------------------------------------

# 🚀 How To Run

## Option 1: Run in IntelliJ (Recommended)

1.  Open IntelliJ IDEA.
2.  Click **Open**.
3.  Select the `kotlinGUI` project folder.
4.  Wait for Gradle to sync.
5.  Open:

```{=html}
<!-- -->
```
    src/main/kotlin/Main.kt

6.  Click the green ▶ next to `fun main()`.

The GUI window will launch.

------------------------------------------------------------------------

## Option 2: Run Using Gradle Wrapper (Terminal)

From the project root directory:

### macOS / Linux:

``` bash
./gradlew run
```

### Windows:

``` bash
gradlew.bat run
```

If `run` is not configured:

``` bash
./gradlew build
```

Then run:

``` bash
java -jar build/libs/kotlinGUI.jar
```

------------------------------------------------------------------------

# 🎯 How to Play

1.  Enter your name when prompted.
2.  Select category and difficulty.
3.  Press **Start Game**.
4.  Click letters to guess the word.
5.  Win by guessing all letters before running out of attempts.

------------------------------------------------------------------------

# 🏆 Scoring System

-   Easy Mode: `10 × remaining attempts`
-   Hard Mode: `20 × remaining attempts`
-   Scores are saved to `highscores.txt`

------------------------------------------------------------------------

# 📁 Word Files

The game loads words from:

-   `science.txt`
-   `countries.txt`
-   `tech.txt`

Each word should be on its own line.

Example:

    atom
    gravity
    molecule

------------------------------------------------------------------------

# 📌 High Score Format

    PlayerName,Score

Example:

    Selin,40
    Alex,20

------------------------------------------------------------------------
License
This project is open source. Feel free to modify and distribute it.
