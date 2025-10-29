package org.example

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.delay

// const values for rewards, wall penalty, and step penalty
// set to different values to test
const val goalReward = 100
const val wallPenalty = -10
const val stepPenalty = -1


// learning values to help teach the agent
const val learningRate = 0.1
const val discountFactor = 0.9
const val explorationStart = 1.0
const val explorationEnd = 0.01
const val numEpisodes = 100

// actions the agent cna take
val actions = arrayOf(
    Pair(-1, 0), // down
    Pair(1, 0), // up
    Pair(0, 1), // right
    Pair(0, -1) // left
)

// class to store the episode results
data class EpisodeResult(
    val totalReward: Double,
    val steps: Int,
    val path: List<Pair<Int, Int>>
)

/**
 * Main script to initialize a maze, test the agent, train it, and then test it again.
 *
 */
fun main() = application {

    Window(onCloseRequest = ::exitApplication, title = "Q-Learning Maze") {
        // initialize the maze layout that we want to use for our tests
        // 1s represent walls and 0s represent empty spaces
        val mazeLayout = arrayOf(
            intArrayOf(0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0),
            intArrayOf(0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0),
            intArrayOf(0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0),
            intArrayOf(0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0),
            intArrayOf(1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0),
            intArrayOf(0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0),
            intArrayOf(0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0),
            intArrayOf(0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0),
            intArrayOf(0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1),
            intArrayOf(0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0),
            intArrayOf(0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0),
            intArrayOf(0, 1, 0, 1, 1, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0),
            intArrayOf(0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 1, 1, 0, 1, 0),
            intArrayOf(0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 0),
            intArrayOf(0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        )

        // set the starting and ending points of the maze
        val startingPoint = Pair(7, 0)
        val endingPoint = Pair(7, 14)

        // create a Q Learning Maze object
        val maze = remember { QLearningMaze(mazeLayout, startingPoint, endingPoint) }

        // initialize the agent
        val agent = remember { QLearningAgent(maze, learningRate, discountFactor, explorationStart, explorationEnd, numEpisodes) }

        var path by remember { mutableStateOf(listOf<Pair<Int, Int>>()) }
        var stepIndex by remember { mutableStateOf(0) }
        var isRunning by remember { mutableStateOf(false) }

        Column(modifier = Modifier.padding(16.dp)) {

            Button(onClick = {
                // Train the agent before showing visualization:
                trainAgent(agent, maze, numEpisodes = 10000)
                val result = finishEpisode(agent, maze, 10000, train = false)
                path = result.path
                stepIndex = 0
                isRunning = true
            }) {
                Text("Train + Visualize")
            }

            Spacer(Modifier.height(16.dp))

            MazeView(mazeLayout, path.getOrNull(stepIndex), endingPoint)

            // Animation
            LaunchedEffect(isRunning) {
                if (isRunning) {
                    while (stepIndex < path.size - 1) {
                        delay(300)
                        stepIndex++
                    }
                    isRunning = false
                }
            }
        }
    }
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

/**
 * Test the agent over an episode and print the results
 *
 * Given an agent and a maze run the agent through the maze. Print the resulting path along with the number of steps and
 * the total reward for the path.
 *
 * @param agent an agent of the class [QLearningAgent]
 * @param maze a maze of the class [QLearningMaze]
 * @param numEpisodes number of episodes to run
 */
fun testAgent(agent: QLearningAgent, maze: QLearningMaze, numEpisodes: Int = 1) {
    val (episodeReward, episodeStep, path) = finishEpisode(agent, maze, numEpisodes, train = false)

    // print the learned path
    println("Learned Path:")
    for(pos in path) {
        print("($pos)-> ")
    }
    // reached the goal!
    println("Goal!")

    // number of steps the agent took
    println("Number of steps: $episodeStep")
    // total reward over the episode
    println("Total reward: $episodeReward")
}

/**
 * Train the agent over a number of episodes.
 *
 * Given an agent, a maze, and a number of episodes the agent will run through the episodes learning and updating q
 * values and will learn from its decisions
 *
 * @param agent a [QLearningAgent] object
 * @param maze a [QLearningMaze] object
 * @param numEpisodes the number of episodes the agent will learn over
 */
fun trainAgent(agent: QLearningAgent, maze: QLearningMaze, numEpisodes: Int) {
    val episodeRewards = mutableListOf<Double>()
    val episodeSteps = mutableListOf<Int>()

    // run the agent over the number of episodes and store how steps it took to finish each episode and the total reward
    for (episode in 0 until numEpisodes) {
        val (episodeReward, episodeStep) = finishEpisode(agent, maze, episode, train = true)

        episodeRewards.add(episodeReward)
        episodeSteps.add(episodeStep)
    }

    // calculate and print average reward
    val averageReward = episodeRewards.sum() / episodeRewards.size
    println("The average reward is: $averageReward")

    // calculate and print the average steps over each episode
    val averageSteps = episodeSteps.sum() / episodeSteps.size
    println("The average steps is: $averageSteps")
}

@Composable
fun MazeView(maze: Array<IntArray>, agentPos: Pair<Int, Int>?, goalPos: Pair<Int, Int>) {
    val rows: Int = maze.size
    val cols: Int = maze[0].size
    val cellSize: Dp = 50.dp

    Canvas(modifier = Modifier.size(cellSize * cols, cellSize * rows)) {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val color = when {
                    agentPos == Pair(r, c) -> Color.Red
                    goalPos == Pair(r, c) -> Color.Green
                    maze[r][c] == 1 -> Color.DarkGray
                    else -> Color.LightGray
                }
                drawRect(
                    color,
                    topLeft = androidx.compose.ui.geometry.Offset(
                        c * cellSize.toPx(),
                        r * cellSize.toPx()
                    ),
                    size = androidx.compose.ui.geometry.Size(cellSize.toPx(), cellSize.toPx())
                )
            }
        }
    }
}

