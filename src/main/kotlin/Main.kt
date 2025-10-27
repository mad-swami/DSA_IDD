package org.example

const val goalReward = 100
const val wallPenalty = -10
const val stepPenalty = -1

const val learningRate = 0.1
const val discountFactor = 0.9
const val explorationStart = 1.0
const val explorationEnd = 0.01
const val numEpisodes = 100

val actions = arrayOf(
    Pair(-1, 0), // down
    Pair(1, 0), // up
    Pair(0, 1), // right
    Pair(0, -1) // left
)

data class EpisodeResult(
    val totalReward: Double,
    val steps: Int,
    val path: List<Pair<Int, Int>>
)
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {

    // initialize the maze layout that we want to use for our tests
    // 1s represent walls and 0s represent empty spaces
    val mazeLayout = arrayOf(
        intArrayOf(0, 1, 0, 0, 0),
        intArrayOf(0, 1, 1, 1, 0),
        intArrayOf(0, 0, 0, 1, 0),
        intArrayOf(1, 1, 0, 1, 1),
        intArrayOf(0, 0, 0, 0, 0)
    )

    // set the starting and ending points of the maze
    val startingPoint = Pair(0, 0)
    val endingPoint = Pair(4, 4)

    // create a Q Learning Maze object
    val maze = QLearningMaze(mazeLayout, startingPoint, endingPoint)

    val agent = QLearningAgent(maze, learningRate, discountFactor, explorationStart, explorationEnd, numEpisodes)
    testAgent(agent, maze)
}

/**
 * Go through an episode of the maze as the agent
 *
 * Given an agent it will take the agent through a given maze. The agent will continue through the maze until it
 * finishes and if train is set to true then the agent's q values will be updated and it will learn.
 *
 * @param agent an agent of class [QLearningAgent]
 * @param maze an agent of class [QLearningMaze]
 * @param currentEpisode the episode that the agent is at
 * @param train whether the agent will learn or not based on the episode
 * @return an array of the episodeReward, episodeStep, and path
 */
fun finishEpisode(agent: QLearningAgent, maze: QLearningMaze, currentEpisode: Int, train: Boolean): EpisodeResult {
    // set current state to the maze starting position
    var currentState = maze.startPos
    var isDone = false
    var episodeReward = 0.0
    var episodeStep = 0
    val path = mutableListOf(currentState)

    // continue while the agent isn't done with the maze
    while (!isDone) {
        // get the action for the agent to commit
        val action = agent.getAction(currentState, currentEpisode)

        // get the next state that the agent will take
        var nextState = Pair(currentState.first + actions[action].first, currentState.second + actions[action].second)
        var reward: Double

        // if the agent would take a step outside the maze or into a wall then give it a wall penalty and keep it in
        // the same place
        if ((nextState.first < 0) || (nextState.first >= maze.mazeHeight)
            || (nextState.second < 0) || (nextState.second >= maze.mazeWidth)
            || (maze.mazeLayout[nextState.first][nextState.second] == 1)
        ) {

            reward = wallPenalty.toDouble()
            nextState = currentState

        // if the agent reaches the end of the maze then it gets the goal reward and the loop ends
        } else if (nextState == maze.endPos) {
            path.add(currentState)
            reward = goalReward.toDouble()
            isDone = true

        // if the agent takes a regular step then it will receive a step penalty
        } else {
            path.add(currentState)
            reward = stepPenalty.toDouble()
        }

        // add the reward of this move to the overall episode reward and move one step forward
        episodeReward += reward
        episodeStep += 1

        // if we are training the agent then update the q table for the agent
        if (train) {
            agent.updateQTable(currentState, action, nextState, reward)
        }

        // move the current state forward
        currentState = nextState
    }

    // return the overall episode reward, the steps this episode took, and the path it took
    return EpisodeResult(episodeReward, episodeStep, path)
}

fun testAgent(agent: QLearningAgent, maze: QLearningMaze, numEpisodes: Int = 1) {
    val (episodeReward, episodeStep, path) = finishEpisode(agent, maze, numEpisodes, train = false)

    println("Learned Path:")
    for(pos in path) {
        print("($pos)-> ")
    }
    println("Goal!")

    println("Number of steps: $episodeStep")
    println("Total reward: $episodeReward")
}


