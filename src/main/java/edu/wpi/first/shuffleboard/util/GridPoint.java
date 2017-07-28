package edu.wpi.first.shuffleboard.util;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Represents a point in a grid.
 */
public class GridPoint implements Serializable {

  public final int col;
  public final int row;

  /**
   * Creates a point at the given column and row indices.
   */
  public GridPoint(int col, int row) {
    checkArgument(col >= 0, "Column index must be non-negative, was " + col);
    checkArgument(row >= 0, "Row index must be non-negative, was " + row);
    this.col = col;
    this.row = row;
  }

  /**
   * Subtracts another grid point from this one. If either the resulting {@code row} or {@code col} would be negative,
   * it is set to zero.
   *
   * @param other the point to subtract from this one
   */
  public GridPoint subtract(GridPoint other) {
    return new GridPoint(Math.max(0, this.col - other.col), Math.max(0, this.row - other.row));
  }

  public int getCol() {
    return col;
  }

  public int getRow() {
    return row;
  }

  public String toString() {
    return String.format("GridPoint(%d, %d)", col, row);
  }

}