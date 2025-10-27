package org.example

/**
 * Class to store values of the specific maze type to be used in this QLearning Project
 *
 * Stores a maze in the form of given [mazeLayout], a 2d array of 1s and 0s. Start and end positions of the maze are given
 * and stored within [startPos] and [endPos]. The shape of the maze is found and kept within [mazeHeight] and
 * [mazeWidth]. The function showMaze is used to display the maze in a visual.
 *
 * @param mazeLayout a 2d array of ints of 1s and 0s representing empty walls and spaces respectively
 * @param startPos a pair of ints representing the start position in the maze
 * @param endPos a pair of ints representing the end position in the maze
 * @property mazeHeight
 * @property mazeWidth
 */
class QLearningMaze(override val mazeLayout: Array<IntArray>, override val startPos: Pair<Int, Int>, override val endPos: Pair<Int, Int>): Maze {
    override val mazeHeight = mazeLayout.size
    override val mazeWidth = mazeLayout[0].size

    override fun showMaze() {
        TODO("Not yet implemented")
    }
}