package StoreByDirectedGraph;

enum TurningStatus {
    No, Upright, Slanted
}

/**
 * For future reference:
 *
 * Right Top: The thread from left on the top is considered "left top"
 * \  /
 *  \\
 * /  \
 * Left Top: The thread from to the right on the top is considered "right top"
 * \  /
 *  //
 * /  \
 */

enum CrossingStatus {
    NoCross, LeftTop, RightTop
}
