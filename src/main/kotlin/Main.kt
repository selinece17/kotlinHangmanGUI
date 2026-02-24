import java.awt.*
import java.io.File
import javax.swing.*
import kotlin.system.exitProcess

/**
 * HangmanSwing
 *
 * A GUI-based Hangman game built using Java Swing.
 *
 * Features:
 * - Category selection (Science, Countries, Tech)
 * - Difficulty selection (Easy / Hard)
 * - Score tracking
 * - Persistent high score storage using a text file
 * - ASCII hangman drawing
 */
class HangmanSwing : JFrame("Hangman Game") {
    // ==============================
    // ===== GAME STATE VARIABLES ===
    // ==============================
    /** The randomly selected word the player must guess */
    private var secretWord = ""
    /** Number of remaining incorrect guesses allowed */
    private var attemptsLeft = 6
    /** Maximum allowed attempts (depends on difficulty) */
    private var maxAttempts = 6
    /** Player's current score */
    private var score = 0
    /** True if a round is currently active */
    private var isGameActive = false
    /** True if difficulty is set to Hard */
    private var isHardMode = false
    /** Set of letters the player has already guessed */
    private val guessedLetters = mutableSetOf<Char>()
    // ==============================
    // ===== GUI COMPONENTS =========
    // ==============================
    /** Displays the word with underscores for hidden letters */
    private val wordLabel = JLabel("", SwingConstants.CENTER)
    /** Displays remaining attempts */
    private val attemptsLabel = JLabel("Attempts: 0", SwingConstants.CENTER)
    /** Displays win/lose messages */
    private val messageLabel = JLabel("", SwingConstants.CENTER)
    /** Displays current score */
    private val scoreLabel = JLabel("Score: 0", SwingConstants.CENTER)
    /** ASCII hangman drawing area */
    private val hangmanArea = JTextArea()
    /** List containing all letter buttons (A-Z) */
    private val letterButtons = mutableListOf<JButton>()
    // ==============================
    // ===== PLAYER / FILE DATA =====
    // ==============================
    /** Name entered by player */
    private var playerName = ""
    /** Highest score ever recorded */
    private var highScore = 0
    /** Player who holds the high score */
    private var highScoreHolder = "None"
    /** Displays high score information */
    private val highScoreLabel = JLabel("High Score: 0 (None)", SwingConstants.CENTER)
    /** File used to store high scores persistently */
    private val highScoreFile = File("highscores.txt")
    // ==============================
    // ===== CONSTRUCTOR (INIT) =====
    // ==============================
    init {

        // Frame setup
        layout = BorderLayout()
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(700, 600)
        setLocationRelativeTo(null)

        // ---------- TOP PANEL ----------
        // Contains category selection, difficulty selection, and start button
        val topPanel = JPanel()
        val categoryBox = JComboBox(arrayOf("Science", "Countries", "Tech"))
        val difficultyBox = JComboBox(arrayOf("Easy", "Hard"))
        val startButton = JButton("Start Game")
        val startHintLabel = JLabel("Just hit Start for a new game")
        startHintLabel.font = Font("Arial", Font.ITALIC, 12)
        startHintLabel.foreground = Color.BLUE

        topPanel.add(startHintLabel)
        topPanel.add(categoryBox)
        topPanel.add(difficultyBox)
        topPanel.add(startButton)
        add(topPanel, BorderLayout.NORTH)

        // ---------- CENTER PANEL ----------
        val centerPanel = JPanel()
        centerPanel.layout = BoxLayout(centerPanel, BoxLayout.Y_AXIS)

        // Styling labels
        wordLabel.font = Font("Monospaced", Font.BOLD, 28)
        attemptsLabel.font = Font("Arial", Font.PLAIN, 18)
        messageLabel.font = Font("Arial", Font.BOLD, 18)
        scoreLabel.font = Font("Arial", Font.BOLD, 18)

        // Configure hangman drawing area
        hangmanArea.font = Font("Monospaced", Font.PLAIN, 16)
        hangmanArea.isEditable = false

        // Add components to center panel
        centerPanel.add(wordLabel)
        centerPanel.add(attemptsLabel)
        centerPanel.add(scoreLabel)
        centerPanel.add(hangmanArea)
        centerPanel.add(messageLabel)
        highScoreLabel.font = Font("Arial", Font.BOLD, 16)
        centerPanel.add(highScoreLabel)

        add(centerPanel, BorderLayout.CENTER)

        // ---------- LETTER PANEL ----------
        val letterPanel = JPanel(GridLayout(4, 7, 5, 5))

        // Create buttons for A-Z
        for (c in 'A'..'Z') {
            val button = JButton(c.toString())
            // When clicked:
            button.addActionListener {
                // Convert to lowercase to match secret word format
                handleGuess(c.lowercaseChar())
                // Disable button after being pressed
                button.isEnabled = false
            }
            letterButtons.add(button)
            letterPanel.add(button)
        }

        add(letterPanel, BorderLayout.SOUTH)

        // ---------- START BUTTON ACTION ----------
        startButton.addActionListener {
            val category = categoryBox.selectedItem.toString()
            val difficulty = difficultyBox.selectedItem.toString()
            startGame(category, difficulty)
        }

        // Ask for name and load previous high scores
        askPlayerName()
        loadHighScore()
        isVisible = true
    }

    // =========================================================
    // ================== PLAYER NAME INPUT ====================
    // =========================================================

    /**
     * Prompts the user to enter their name.
     * Prevents empty input.
     * Exits program if user cancels.
     */
    private fun askPlayerName() {
        while (true) {
            val input = JOptionPane.showInputDialog(
                this,
                "Enter your name:",
                "Player Name",
                JOptionPane.PLAIN_MESSAGE
            )
            // If user presses Cancel → exit program
            if (input == null) {
                exitProcess(0)
            }
            val trimmed = input.trim()
            if (trimmed.isNotEmpty()) {
                playerName = trimmed
                break
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Name cannot be blank!",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }

    // =========================================================
    // ================== HIGH SCORE METHODS ===================
    // =========================================================

    /**
     * Reads highscores.txt
     * Finds the highest score and its holder
     * Updates label
     */
    private fun loadHighScore() {
        if (!highScoreFile.exists()) return
        val lines = highScoreFile.readLines()
        var highest = 0
        var holder = "None"
        for (line in lines) {
            val parts = line.split(",")
            // Ensure correct format: name,score
            if (parts.size == 2) {
                val name = parts[0]
                val savedScore = parts[1].toIntOrNull()
                if (savedScore != null && savedScore > highest) {
                    highest = savedScore
                    holder = name
                }
            }
        }

        highScore = highest
        highScoreHolder = holder
        highScoreLabel.text = "High Score: $highScore ($highScoreHolder)"
    }

    /**
     * Appends current player's score to highscores.txt.
     * Then reloads file to determine if new high score exists.
     */
    private fun saveHighScore() {
        if (!highScoreFile.exists()) {
            highScoreFile.createNewFile()
        }
        // Append player and score on new line
        highScoreFile.appendText( "$playerName,$score${System.lineSeparator()}" )
        loadHighScore()
        // Notify if new record
        if (score == highScore) {
            JOptionPane.showMessageDialog(
                this,
                "NEW HIGH SCORE!",
                "Congratulations",
                JOptionPane.INFORMATION_MESSAGE
            )
        }
    }

    // =========================================================
    // ================== GAME LOGIC METHODS ===================
    // =========================================================

    /**
     * Starts a new game round.
     * Loads words from file based on category.
     * Sets difficulty.
     * Resets UI and state.
     */
    private fun startGame(category: String, difficulty: String) {
        // Determine filename based on category
        val filename = when (category) {
            "Science" -> "science.txt"
            "Countries" -> "countries.txt"
            else -> "tech.txt"
        }
        val words = try {
            File(filename)
                .readLines()
                .map { it.trim() }
                .filter { it.isNotBlank() }
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(this, "Word file not found!")
            return
        }

        if (words.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Word file is empty!")
            return
        }

        // Choose random word
        secretWord = words.random().lowercase()

        // Set difficulty
        isHardMode = difficulty == "Hard"

        if (isHardMode) {
            attemptsLeft = 3
            maxAttempts = 3
        } else {
            attemptsLeft = 6
            maxAttempts = 6
        }

        // Reset game state
        guessedLetters.clear()
        letterButtons.forEach { it.isEnabled = true }
        messageLabel.text = ""
        score = 0
        scoreLabel.text = "Score: $score"
        isGameActive = true

        updateDisplay()
    }

    /**
     * Handles a player's letter guess.
     * Updates attempts and checks win/lose conditions.
     */
    private fun handleGuess(letter: Char) {
        if (!isGameActive) return
        if (letter in guessedLetters) return
        guessedLetters.add(letter)
        // If incorrect guess → reduce attempts
        if (letter !in secretWord) {
            attemptsLeft--
        }
        updateDisplay()

        // WIN CONDITION
        if (secretWord.all { it in guessedLetters }) {
            isGameActive = false
            // Score calculation depends on difficulty
            score += if (isHardMode)
                attemptsLeft * 20
            else
                attemptsLeft * 10

            scoreLabel.text = "Score: $score"
            saveHighScore()
            messageLabel.text = "You Won!"
            disableLetters()
            return
        }

        // LOSE CONDITION
        if (attemptsLeft <= 0) {
            isGameActive = false
            messageLabel.text = "Game Over! Word was: $secretWord"
            saveHighScore()
            disableLetters()
        }
    }

    /**
     * Updates:
     * - Displayed word
     * - Attempts label
     * - ASCII hangman drawing
     */
    private fun updateDisplay() {
        val displayedWord = secretWord.map { letter ->
            if (guessedLetters.contains(letter)) letter else '_'
        }.joinToString(" ")

        wordLabel.text = displayedWord
        attemptsLabel.text = "Attempts Left: $attemptsLeft"
        hangmanArea.text = drawHangman()
    }

    /**
     * Disables all letter buttons after game ends.
     */
    private fun disableLetters() {
        letterButtons.forEach { it.isEnabled = false }
    }

    /**
     * Returns ASCII art of hangman depending on
     * number of wrong guesses.
     */
    private fun drawHangman(): String {
        val stages = listOf(
                   """
        ------ 
        |    |
        |    
        |    
        |    
        |    
        """,
                    """
        ------
        |    |
        |    O
        |    
        |    
        |    
        """,
                    """
        ------
        |    |
        |    O
        |    |
        |    
        |    
        """,
                    """
        ------
        |    |
        |    O
        |   /|
        |    
        |    
        """,
                    """
        ------
        |    |
        |    O
        |   /|\
        |    
        |    
        """,
                    """
        ------
        |    |
        |    O
        |   /|\
        |   /
        |    
        """,
                    """
        ------
        |    |
        |    O
        |   /|\
        |   / \
        |    
        """
                )
        val wrongGuesses = (maxAttempts - attemptsLeft)
            .coerceIn(0, stages.size - 1)
        return stages[wrongGuesses]
    }
}

/**
 * Main entry point.
 * Ensures GUI runs on Event Dispatch Thread.
 */
fun main() {
    SwingUtilities.invokeLater {
        HangmanSwing()
    }
}