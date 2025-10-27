package org.example

/**
 * Maze interface for storing maze data.
 *
 * Maze stores a given maze layout of the form of a 2d array of integer arrays of 1s and 0s, where 1 represents a wall and 0 represents an
 * empty space. Maze also stores a start point and end point as pairs of ints that representing the starting and ending
 * positions within the maze. Using the maze layout we can also store the maze height and width as integers.
 *
 * @property mazeLayout array of integer arrays filled with 1s and 0s representing the maze's layout
 * @property startPos a pair of ints representing the position of the start point
 * @property endPos a pair of ints representing the position of the end point
 * @property mazeHeight an int representing the height of the maze
 * @property mazeWidth an int representing the width of the maze
 */
interface Maze {
    val mazeLayout: Array<IntArray>

    val startPos: Pair<Int, Int>
    val endPos: Pair<Int, Int>

    val mazeHeight: Int
    val mazeWidth: Int

    fun showMaze()
}