#include "State.hpp"
#include "MapNodeLink.hpp"
#include "GameState.hpp"
#include "Circle.hpp"
#include <SFML/Network/TcpSocket.hpp>
#include <SFML/Network/Packet.hpp>
#include <SFML/Graphics/RenderWindow.hpp>
#include <iostream>
#include <math.h>

GameState::GameState(StateStack* stack, Context context) :
                State(stack, context),
                window(context.window),
                startNode(NULL),
                endNode(NULL),
                squareSize(30),
                squareType(MapNode::Type::START),
                buttonToggle(false)
{
    windowSize = window->getSize();
    windowTileSize = sf::Vector2u(windowSize.x / squareSize, windowSize.y / squareSize);
    for(unsigned int j = 0; j < windowSize.y / squareSize; j++)
    {
        for(unsigned int i = 0; i < windowSize.x / squareSize; i++)
        {
            map.attachChild(new MapNode(i, j, squareSize, squareSize, MapNode::Type::REGULAR));
        }
    }
}

GameState::~GameState()
{
    map.destroy();
}

void GameState::draw()
{
    window->draw(map);
//    world.draw();
}

/*
 * Updates the world, if return false updates to the gameStack will stop.
 * Useful for a pause menu, will not update the background (will not make
 * a difference if pause menu is not transparent)
 */
bool GameState::update(sf::Time dt)
{
//    world.update(dt);
    sf::Vector2i mousePos = sf::Mouse::getPosition(*window);
    MapNode *tile;

    if(buttonToggle)
    {
        tile = (MapNode *) map.getChildNode(
                SceneNode::get1d(mousePos.x / squareSize, mousePos.y / squareSize, windowTileSize.x));
        if(tile == NULL)
            std::cout << "Woops\n";
        else
        {
            if(!(tile->getType() & MapNode::START_OR_END))
                tile->setType(squareType);
        }
    }

    map.update(dt);
    return true;
}

/*
 * Handle SFML events, ex: mouse clicks, keyboard key presses
 */
bool GameState::handleEvent(const sf::Event* event)
{
    sf::Vector2i mousePos = sf::Mouse::getPosition(*window);
    MapNode *tile;
    if(event->type == sf::Event::MouseButtonPressed)
    {
        tile = (MapNode *) map.getChildNode(
                SceneNode::get1d(mousePos.x / squareSize, mousePos.y / squareSize, windowTileSize.x));
        if(tile == NULL)
            std::cout << "Woops\n";
        else
        {
            if(!(tile->getType() & MapNode::Type::BLOCKING))
            {
                if(squareType == MapNode::Type::START)
                {
                    tile->setType(squareType);
                    squareType = MapNode::Type::END;
                    startNode = tile;
                }
                else if(squareType == MapNode::Type::END)
                {
                    tile->setType(squareType);
                    squareType = MapNode::Type::BLOCK;
                    endNode = tile;
                }
                else
                {
                    buttonToggle = true;
                }
            }
        }
    }
    else if(event->type == sf::Event::MouseButtonReleased)
    {
        buttonToggle = false;
    }
    else if(event->type == sf::Event::KeyPressed && event->key.code == sf::Keyboard::Return)
    {
        std::cout << "RETURN PRESSED\n";
        if(startNode == NULL || endNode == NULL)
        {
            std::cout << "Start or end node not set. Cannot find path\n";
        }
        else
        {
            sf::Clock clock;
            findPath();
            sf::Time time = clock.getElapsedTime();
            std::cout << "It took " << time.asMilliseconds() << " milliseconds to calculate the shortest path\n";
//            map.layerChildNode(
//                    new Circle(Category::Type::NONE, windowTileSize.x, windowTileSize.y, squareSize, startNode, endNode,
//                            &map),
//                    MapNode::get1d(startNode->getPosition().x, startNode->getPosition().y, windowTileSize.x));
        }

    }
    return true;
}

void GameState::calulateGHValues()
{
    MapNode *node;
    for(std::vector<SceneNode *>::iterator it = map.getChildIteratorBegin(); it != map.getChildIteratorEnd(); it++)
    {
        node = (MapNode *) (*it);
        node->setDistanceFromStart(0);
        node->setDistanceToEnd(calculateDistanceToEnd(node->getPosition()));
        if(!(node->getType() & MapNode::Type::BLOCKING))
            node->setType(MapNode::Type::REGULAR);
    }
    startNode->setType(MapNode::Type::START);
    endNode->setType(MapNode::Type::END);
}

float GameState::calculateDistance(sf::Vector2f pos1, sf::Vector2f pos2)
{
    float yDist = pos2.y - pos1.y;
    float xDist = pos2.x - pos1.x;
    float retVal = xDist * xDist + yDist * yDist;
    return sqrt(retVal);
}

float GameState::calculateDistanceToEnd(sf::Vector2f pos1)
{
    return calculateDistance(pos1, endNode->getPosition());
}

/*
 * Find a path from startNode to endNode with A*
 */
void GameState::findPath()
{
    //TODO implement this has a heap?
    MapNodeLink *openList = NULL;
    MapNodeLink *tmpNodeLink;
    MapNode *currentNode;
    MapNode *adjacentNode;
    sf::Vector2f pos;
    int i, j;
    bool finished = false;
    MapNode::Type nodeType;

    calulateGHValues();

    MapNodeLink::attachNode(&openList, startNode);
    while(!finished)
    {
        //Get node in open list with lowest fValue
        currentNode = openList->getNode();
        tmpNodeLink = openList;
        openList = openList->getNextLink();
        delete tmpNodeLink;

        //We have found the end, We should end earlier than this though, if an adjacent square is the end
        if(currentNode == endNode)
            break;

//        //ADD it to the closed list, don't add start node so that the color doesn't change
//        if(currentNode != startNode)
        currentNode->setType(MapNode::Type::CLOSED);

        pos = currentNode->getTilePosition();
        for(i = pos.x - 1; i <= pos.x + 1; i++)
        {
            //Check so that x is not out of bounds
            if(i < 0 || i > windowTileSize.x - 1)
                continue;

            for(j = pos.y - 1; j <= pos.y + 1; j++)
            {
                //Check so that j is not out of bounds or not the current tile
                if(j < 0 || j > windowTileSize.y - 1 || (i == pos.x && j == pos.y))
                    continue;

                //If checking a diagonal, make sure it is possible to get there
                if(i != pos.x && j != pos.y)
                {
                    adjacentNode = (MapNode *) map.getChildNode(MapNode::get1d(i, pos.y, windowTileSize.x));
                    if(adjacentNode->getType() & MapNode::Type::BLOCKING)
                        continue;

                    adjacentNode = (MapNode *) map.getChildNode(MapNode::get1d(pos.x, j, windowTileSize.x));
                    if(adjacentNode->getType() & MapNode::Type::BLOCKING)
                        continue;
                }

                adjacentNode = (MapNode *) map.getChildNode(MapNode::get1d(i, j, windowTileSize.x));
                if(adjacentNode == NULL)
                {
                    std::cout << "FUCKED UP\n";
                    continue;
                }

                //Path has been found
                if(adjacentNode == endNode)
                {
                    std::cout << "ENDING\n";
                    endNode->setParentPathNode(currentNode);
                    finished = true;
                    break;
                }
                nodeType = adjacentNode->getType();
                //TODO this is not correct. If node is closed but the f value is less here, update it and put it in open list
                if(nodeType == MapNode::Type::CLOSED || (nodeType & MapNode::Type::BLOCKING))
                {
                    continue;
                }

                float distance = calculateDistance(pos, adjacentNode->getPosition());
                if(nodeType != MapNode::Type::OPEN
                        || adjacentNode->getDistanceFromStart() > distance + currentNode->getDistanceFromStart())
                {
                    adjacentNode->setDistanceFromStart(distance + currentNode->getDistanceFromStart());
                    adjacentNode->setParentPathNode(currentNode);
                    if(nodeType != MapNode::Type::OPEN)
                    {
                        adjacentNode->setType(MapNode::Type::OPEN);
                        MapNodeLink::attachNode(&openList, adjacentNode);
                    }
                }
            }
        }
    }
    MapNodeLink::destroy(openList);
    std::cout << "FINISED BITCH\n";
    if(finished)
    {
        colorPath();
        resetOpenClosed();
    }
    else
        std::cout << "Could not find path\n";
}

void GameState::colorPath()
{
    MapNode *path = endNode->getParentPathNode();
    MapNode *prev = endNode;
    while(path != startNode)
    {
        path->setType(MapNode::Type::PATH);
        path->setChildPathNode(prev);
        prev = path;
        path = path->getParentPathNode();
    }
    startNode->setType(MapNode::Type::START);
    startNode->setChildPathNode(prev);
}

void GameState::resetOpenClosed()
{
    MapNode *node;
    for(std::vector<SceneNode *>::iterator it = map.getChildIteratorBegin(); it != map.getChildIteratorEnd(); it++)
    {
        node = (MapNode *) (*it);
        MapNode::Type type = node->getType();
        if(type == MapNode::Type::OPEN || type == MapNode::Type::CLOSED)
        {
            node->setType(MapNode::Type::REGULAR);
        }
    }
}
