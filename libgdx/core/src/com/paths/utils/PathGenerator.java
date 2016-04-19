package com.paths.utils;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

import com.badlogic.gdx.math.Vector2;
import com.paths.drawable.MapNode;
import com.paths.drawable.SceneNode;

public class PathGenerator
{
    public enum AStarNodeType {
        OPEN,
        CLOSED,
        NONE
    };

    private static class MapNodeComparator implements Comparator<MapNode>
    {
        @Override
        public int compare(MapNode x, MapNode y)
        {
            return (int) (x.getFValue() - y.getFValue());
        }
    }

    private static final Comparator<MapNode> comparator = new MapNodeComparator();
    private static final PriorityQueue<MapNode> openList = new PriorityQueue<MapNode>(30, comparator);

    public static boolean findPathToPath(MapNode map, MapNode startNode, MapNode endNode, Vector2 windowTileSize)
    {
        return true;
    }
    
    public static boolean findPath(MapNode map, MapNode startNode, MapNode endNode, Vector2 windowTileSize, 
            boolean invalidatePaths, boolean toPath)
    {
        MapNode currentNode;
        MapNode adjacentNode;
        MapNode prevNode = null;
        MapNode tmpEndNode = endNode;
        Vector2 pos;
        Vector2 tilePos;
        int i, j;
        boolean finished = false;
        boolean pathFound = true;
        MapNode.Category nodeType;
        AStarNodeType pathingType;

        openList.add(startNode);
        while(!finished)
        {
            //Get node in open list with lowest fValue
            currentNode = openList.poll();

            //it will be null if it is impossible to get to the finish
            if(currentNode == null)
            {
                finished = true;
                pathFound = false;
                break;
            }

            //We have found the end, We should end earlier than this though, if an adjacent square is the end
            if(currentNode == endNode)
                break;

            if(toPath && currentNode.isValidPath())
            {
                tmpEndNode = prevNode;
                prevNode.setChildPathNode(currentNode);
                break;
            }

            //ADD it to the closed list, don't add start node so that the color doesn't change
            if(currentNode != startNode)
                currentNode.setPathFindingType(AStarNodeType.CLOSED);

            pos = currentNode.getPosition();
            tilePos = currentNode.getTilePosition();

            for(i = (int) (tilePos.x - 1); i <= tilePos.x + 1; i++)
            {
                //Check so that x is not out of bounds
                if(i < 0 || i > windowTileSize.x - 1)
                    continue;

                for(j = (int) (tilePos.y - 1); j <= tilePos.y + 1; j++)
                {
                    //Check so that j is not out of bounds or not the current tile
                    if(j < 0 || j > windowTileSize.y - 1 || (i == tilePos.x && j == tilePos.y))
                        continue;

                    //If checking a diagonal, make sure it is possible to get there
                    if(i != tilePos.x && j != tilePos.y)
                    {
                        adjacentNode = (MapNode) map.getChildNode(SceneNode.get1d(i, (int)tilePos.y, (int)windowTileSize.x));
                        if((adjacentNode.getType().getValue() & MapNode.Category.BLOCKING.getValue()) != 0)
                            continue;

                        adjacentNode = (MapNode ) map.getChildNode(SceneNode.get1d((int)tilePos.x, j, (int)windowTileSize.x));
                        if((adjacentNode.getType().getValue() & MapNode.Category.BLOCKING.getValue()) != 0)
                            continue;
                    }

                    adjacentNode = (MapNode) map.getChildNode(SceneNode.get1d(i, j, (int)windowTileSize.x));
                    if(adjacentNode == null)
                    {
                        //TODO logging
                        System.out.println("FUCKED UP");
                        continue;
                    }

                    //Path has been found
                    if(adjacentNode == endNode)
                    {
                        endNode.setParentPathNode(currentNode);
                        finished = true;
                        break;
                    }
                    nodeType = adjacentNode.getType();
                    pathingType = adjacentNode.getPathFindingType();

                    //TODO this is not correct. If node is closed but the f value is less here, update it and put it in open list
                    if(pathingType == AStarNodeType.CLOSED || (nodeType.getValue() & MapNode.Category.BLOCKING.getValue()) != 0)
                    {
                        continue;
                    }

                    float distance = CollisionDetection.getDistance(pos, adjacentNode.getPosition());
                    if(pathingType != AStarNodeType.OPEN || adjacentNode.getDistanceFromStart() > distance + currentNode.getDistanceFromStart())
                    {
                        adjacentNode.setDistanceFromStart(distance + currentNode.getDistanceFromStart());
                        adjacentNode.setParentPathNode(currentNode);
                        prevNode = adjacentNode;
                        if(pathingType != AStarNodeType.OPEN)
                        {
//                            this should be changing som eother property and not the node type.
                            adjacentNode.setPathFindingType(AStarNodeType.OPEN);
                            openList.add(adjacentNode);
                        }
                    }
                }
            }
        }

        openList.clear();
        resetOpenClosed(map, invalidatePaths);
        colorPath(startNode, tmpEndNode, toPath);
        return pathFound;
    }
    
    public static void calulateGHValues(MapNode map, Vector2 endPosition, MapNode startNode, MapNode endNode)
    {
        MapNode node;
        Iterator<SceneNode> it = map.getChildenIterator();
        while(it.hasNext())
        {
            node = (MapNode) it.next();
            node.setDistanceFromStart(0);
            node.setDistanceToEnd(CollisionDetection.getDistance(node.getPosition(), endPosition));

            //Reset path since new one is being calculated
            if((node.getType().getValue() & MapNode.Category.BLOCKING.getValue()) == 0)
                node.setType(MapNode.Category.REGULAR);
            
        }
        startNode.setType(MapNode.Category.START);
        endNode.setType(MapNode.Category.END);
    }
    
    private static void colorPath(MapNode startNode, MapNode endNode, boolean toPath)
    {
        MapNode path = endNode.getParentPathNode();
        MapNode prev = endNode;
        while(path != startNode)
        {
            path.validPath();
            path.setType(MapNode.Category.PATH);
            path.setChildPathNode(prev);
            prev = path;
            path = path.getParentPathNode();
        }
        if(toPath)
            startNode.setType(MapNode.Category.PATH);
        else
            startNode.setType(MapNode.Category.START);

        startNode.setChildPathNode(prev);
        startNode.validPath();
    }
    
    private static void resetOpenClosed(MapNode map, boolean invalidatePaths)
    {
        MapNode node;
        Iterator<SceneNode> it = map.getChildenIterator();
        while(it.hasNext())
        {
            node = (MapNode) it.next();
            MapNode.Category type = node.getType();
//            if(node.isValidPath())
//                node.setType(MapNode.Category.PATH);
            System.out.println("Iterating over " + node.getTilePosition());
            if((type.getValue() & MapNode.Category.BLOCKING.getValue()) == 0)
                node.setPathFindingType(AStarNodeType.NONE);
            //TODO this next line is needed, I'm pretty sure
//                node.setType(MapNode.Category.REGULAR);
            
            //This may need to go in the if above
            if(invalidatePaths)
                //TODO add check to not invalidate start and exit?
                node.invalidatePath();
        }
    }
}
