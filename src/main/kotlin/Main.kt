package org.example

val actions = arrayOf(
    Pair(-1, 0), // down
    Pair(1, 0), // up
    Pair(0, 1), // right
    Pair(0, -1) // left
)

const val goalReward = 100
const val wallPenalty = -10
const val stepPenalty = -1

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
}

fun finishEpisode(agent: QLearningAgent, maze: QLearningMaze, currentEpisode: Int, train: Boolean = true): Array<Any> {
    var currentState = maze.startPos
    var isDone = false
    var episodeReward = 0.0
    var episodeStep = 0
    val path = mutableListOf(currentState)

    while (!isDone) {
        val action = agent.getAction(currentState, currentEpisode)

        var nextState = Pair(currentState.first + actions[action].first, currentState.second + actions[action].second)
        var reward: Double

        if ((nextState.first < 0) || (nextState.first >= maze.mazeHeight)
            || (nextState.second < 0) || (nextState.second >= maze.mazeWidth)
            || (maze.mazeLayout[nextState.first][nextState.second] == 1)
        ) {

            reward = wallPenalty.toDouble()
            nextState = currentState

        } else if (nextState == maze.endPos) {
            path.add(currentState)
            reward = goalReward.toDouble()
            isDone = true
        } else {
            path.add(currentState)
            reward = stepPenalty.toDouble()
        }

        episodeReward += reward
        episodeStep += 1

        if (train) {
            agent.updateQTable(currentState, action, nextState, reward)
        }

        currentState = nextState
    }
    return arrayOf(episodeReward, episodeStep, path)
}
