package org.example

import kotlin.math.pow

/**
 * QLearning agent class for initializing and teaching a Q learning agent.
 *
 * QLearningAgent takes [maze] a [QLearningMaze] object to create a [qTable]. The agent also takes [discountFactor],
 * [explorationStart], [explorationEnd], and [numEpisodes] to get the exploration rate. This exploration rate influences
 * the next action the agent will take, exploration vs exploitation. Finally, given a state of the maze, action,
 * next state to take, and reward the q table will update its values.
 *
 * @param maze a [QLearningMaze] object for creating the [qTable]
 * @param learningRate a double that controls how much the agent updates its Q values after each action
 * @param discountFactor a double that determines the importance of future rewards in the agent's decisions
 * @param explorationStart a double that determines the likelihood of the agent taking a random action
 * @param explorationEnd a double that along with the exploration start gives the exploration rate
 * @param numEpisodes the number of times the agent will attempt to move through the maze
 * @property qTable a 3d array of Q values corresponding to all the states in the maze.
 */
class QLearningAgent(maze: QLearningMaze, override val learningRate: Double,
                     override val discountFactor: Double, override val explorationStart: Double,
                     override val explorationEnd: Double, override val numEpisodes: Int): Agent{
    // initialize a Q table containing  all zeros, rows represent the states, columns represent actions, and the third
    // dimension is for each action (up, down, left, right)
    override val qTable = Array(maze.mazeHeight) { Array(maze.mazeWidth) { DoubleArray(4) {0.0} } }

    override fun getExplorationRate(currentEpisode: Int): Double {
        val exponent = currentEpisode.toDouble() / numEpisodes.toDouble()
        return explorationStart * (explorationEnd / explorationStart).pow(exponent)
    }

    override fun getAction(state: Pair<Int, Int>, currentEpisode: Int): Int {
        val (row, col) = state
        val explorationRate = getExplorationRate(currentEpisode)

        // determine exploration vs exploitation
        // if a random value is lower than our explorationRate then we randomly explore
        return if (Math.random() < explorationRate) {
            (0..3).random()
        // otherwise we rely on what we have learned through the qtable and choose the best action
        } else {
            qTable[row][col].bestActionIndex()
        }
    }

    override fun updateQTable(state: Pair<Int, Int>, action: Int, nextState: Pair<Int, Int>, reward: Double) {
        val (row, col) = state
        val (nextRow, nextCol) = nextState

        val bestNextAction = qTable[nextRow][nextCol].bestActionIndex()

        val currentQValue = qTable[row][col][action]

        // we update the q value based on the learning rate and reward given as well as a discountFactor
        val newQValue = currentQValue + learningRate *
                (reward + discountFactor * qTable[nextRow][nextCol][bestNextAction] - currentQValue)

        qTable[row][col][action] = newQValue
    }

    /**
     * Gets the highest value in a double array
     *
     * The double array corresponds to a state in the q table. We look through this array and find the best value.
     *
     * @param DoubleArray an array of doubles corresponding to a state in the q table
     */
    fun DoubleArray.bestActionIndex(): Int {
        var bestIndex = 0
        var bestValue = this[0]

        // loop through the array and keep updating if the found value is bigger than stored
        for (i in 1 until size) {
            if (this[i] > bestValue) {
                bestIndex = i
                bestValue = this[i]
            }
        }
        return bestIndex
    }
}