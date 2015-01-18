__author__ = 'Andrew'

def calculateCollision(trail_one, trail_two, player_select):
    """(list of tuples, list of tuples, int) -> boolean
    :param trail_one: breadcrumb trail of player one
    :param trail_two: breadcrumb trail of player two
    :param playerSelect: Which player is to be checked currently. (1 - player one/2 - player two)
    :return: whether or not the player has collided.
    """
    if player_select == 1 and len(trail_one) >= 2:
        start = trail_one[len(trail_one) - 1]
        end = trail_one[len(trail_one) - 2]
        for i in range(len(trail_one) - 2):
            if i >= 1:
                tStart = trail_one[i - 1]
                tEnd = trail_one[i]
                if iHateGeometry(start[0], start[1], end[0], end[1], tStart[0], tStart[1], tEnd[0], tEnd[1]):
                    return True
        for i in range(len(trail_two)):
            if i >= 1:
                tStart = trail_two[i - 1]
                tEnd = trail_two[i]
                if iHateGeometry(start[0], start[1], end[0], end[1], tStart[0], tStart[1], tEnd[0], tEnd[1]):
                    return True
    if player_select == 2 and len(trail_two) >= 2:
        start = trail_two[len(trail_two) - 1]
        end = trail_two[len(trail_two) - 2]
        for i in range(len(trail_two) - 2):
            if i >= 1:
                tStart = trail_two[i - 1]
                tEnd = trail_two[i]
                if iHateGeometry(start[0], start[1], end[0], end[1], tStart[0], tStart[1], tEnd[0], tEnd[1]):
                    return True
        for i in range(len(trail_one)):
            if i >= 1:
                tStart = trail_one[i - 1]
                tEnd = trail_one[i]
                if iHateGeometry(start[0], start[1], end[0], end[1], tStart[0], tStart[1], tEnd[0], tEnd[1]):
                    return True
    return False

def iHateGeometry(p0_x, p0_y, p1_x, p1_y, p2_x, p2_y, p3_x, p3_y):
    s1_x = p1_x - p0_x
    s1_y = p1_y - p0_y
    s2_x = p3_x - p2_x
    s2_y = p3_y - p2_y

    s = (-s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y)) / (-s2_x * s1_y + s1_x * s2_y)
    t = ( s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / (-s2_x * s1_y + s1_x * s2_y)

    if (s >= 0 and s <= 1 and t >= 0 and t <= 1):
        return True
    return False

if __name__ == '__main__':
    trail_one = [(1, 1), (2, 2), (3, 3)]
    trail_two = [(3, 1), (2, 1)]
    print(calculateCollision(trail_one, trail_two, 1))