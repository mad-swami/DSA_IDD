package org.example

/**
 * Agent interface for storing and manipulation agent data.
 *
 * Agent stores a given [learningRate], [discountFactor], [explorationStart], and [explorationEnd] as doubles. These all
 * influence the way the agent learns and explores at a given rate. The agent also stores [numEpisodes] as an integer
 * for learning. Given the state the agent is in the maze and the current episode they are at we can get a random action
 * or find the highest Q value for the state, exploration vs exploitation. Given a state the agent is in, an action, the
 * next state, and a reward the agent will update its Q table to learn.
 *
 * @property qTable a 3d array of Q values corresponding to all the states in the maze.
 * @property learningRate a double that controls how much the agent updates its Q values after each action
 * @property discountFactor a double that determines the importance of future rewards in the agent's decisions
 * @property explorationStart a double that determines the likelihood of the agent taking a random action
 * @property explorationEnd a double that along with the exploration start gives the exploration rate
 * @property numEpisodes the number of times the agent will attempt to move through the maze
 */
interface Agent {
    val qTable: Array<Array<DoubleArray>>

    val learningRate: Double
    val discountFactor: Double
    val explorationStart: Double
    val explorationEnd: Double
    val numEpisodes: Int

    /**
     * Calculate the exploration rate
     *
     * Given the current episode use the formula to find the exploration rate. This increases as we reach the number of
     * episodes.
     *
     * @param currentEpisode int representing the current point the agent is at in terms of learning
     */
    fun getExplorationRate(currentEpisode: Int): Double

    /**
     * Get the next action to do based on exploration vs exploitation in a given state
     *
     * Given the current episode we get the exploration date and based upon this rate we choose exploration or
     * exploitation.
     *
     * @param state a pair of ints representing the state that the agent is in within the maze
     * @param currentEpisode an int that represents how far along within the learning the agent is
     */
    fun getAction(state: Pair<Int, Int>, currentEpisode: Int): Int

    /**
     * Update the q value of a state within the q table based on an action to take, the next state it will take, and a
     * reward value.
     *
     * We first find the best next action that maximizes the q value for the next state. Then we get the current q value.
     * Using these values along with the reward we have set and discount factors we create a new q value. Finally, we
     * update the q table with the new q value for the current state and action.
     *
     * @param state the current state the agent is in within the maze
     * @param action the current action that the agent has taken
     * @param nextState a pair of the next state that the agent will take
     * @param reward a double representing the way the agent will act and if it is good or not
     */
    fun updateQTable(state: Pair<Int, Int>, action: Int, nextState: Pair<Int, Int>, reward: Double)
}