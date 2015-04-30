package com.paths.utils;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

import com.badlogic.gdx.math.Vector2;
import com.paths.drawable.MapNode;
import com.paths.drawable.SceneNode;

public class PathGenerator
{
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

    public static boolean findPath(MapNode map, MapNode startNode, MapNode endNode, Vector2 windowTileSize)
    {
//        MapNodeLink *openList = NULL;
//        MapNodeLink *tmpNodeLink;
        MapNode currentNode;
        MapNode adjacentNode;
        Vector2 pos;
        Vector2 tilePos;
        int i, j;
        boolean finished = false;
        MapNode.Category nodeType;

        //TODO do this step on level load????
        calulateGHValues(map, endNode.getPosition(), startNode, endNode);


        openList.add(startNode);
        while(!finished)
        {
            //Get node in open list with lowest fValue
            currentNode = openList.poll();

            //We have found the end, We should end earlier than this though, if an adjacent square is the end
            if(currentNode == endNode)
                break;

            //ADD it to the closed list, don't add start node so that the color doesn't change
            if(currentNode != startNode)
                currentNode.setType(MapNode.Category.CLOSED);

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

                    //TODO this is not correct. If node is closed but the f value is less here, update it and put it in open list
                    if(nodeType == MapNode.Category.CLOSED || (nodeType.getValue() & MapNode.Category.BLOCKING.getValue()) != 0)
                    {
                        continue;
                    }

                    float distance = calculateDistance(pos, adjacentNode.getPosition());
                    if(nodeType != MapNode.Category.OPEN || adjacentNode.getDistanceFromStart() > distance + currentNode.getDistanceFromStart())
                    {
                        adjacentNode.setDistanceFromStart(distance + currentNode.getDistanceFromStart());
                        adjacentNode.setParentPathNode(currentNode);
                        if(nodeType != MapNode.Category.OPEN)
                        {
                            adjacentNode.setType(MapNode.Category.OPEN);
                            openList.add(adjacentNode);
                        }
                    }
                }
            }
        }
        openList.clear();
        if(finished)
        {
            colorPath(startNode, endNode);
            resetOpenClosed(map);
            return true;
        }
        return false;
    }
    
    public static void calulateGHValues(MapNode map, Vector2 endPosition, MapNode startNode, MapNode endNode)
    {
        MapNode node;
        Iterator<SceneNode> it = map.getChildenIterator();
        while(it.hasNext())
        {
            node = (MapNode) it.next();
            node.setDistanceFromStart(0);
            node.setDistanceToEnd(calculateDistance(node.getPosition(), endPosition));

            //Reset path since new one is being calculated
            if((node.getType().getValue() & MapNode.Category.BLOCKING.getValue()) == 0)
                node.setType(MapNode.Category.REGULAR);
            
        }
        startNode.setType(MapNode.Category.START);
        endNode.setType(MapNode.Category.END);
    }
    
    private static float calculateDistance(Vector2 pos1, Vector2 pos2)
    {
        float yDist = pos2.y - pos1.y;
        float xDist = pos2.x - pos1.x;
        float retVal = xDist * xDist + yDist * yDist;
        return (float) Math.sqrt(retVal);
    }
    
    private static void colorPath(MapNode startNode, MapNode endNode)
    {
        MapNode path = endNode.getParentPathNode();
        MapNode prev = endNode;
        while(path != startNode)
        {
            path.setType(MapNode.Category.PATH);
            path.setChildPathNode(prev);
            prev = path;
            path = path.getParentPathNode();
        }
        startNode.setType(MapNode.Category.START);
        startNode.setChildPathNode(prev);
    }
    
    private static void resetOpenClosed(MapNode map)
    {
        MapNode node;
        Iterator<SceneNode> it = map.getChildenIterator();
        while(it.hasNext())
        {
            node = (MapNode) it.next();
            MapNode.Category type = node.getType();
            if(type == MapNode.Category.OPEN || type == MapNode.Category.CLOSED)
            {
                node.setType(MapNode.Category.REGULAR);
            }
        }
    }
}