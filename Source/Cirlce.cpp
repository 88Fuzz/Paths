#include "Circle.hpp"
#include <iostream>

Circle::Circle(Category::Type category, int mapWidth, int mapHeight, int tileSize, MapNode *start, MapNode *end,
        MapNode *map) :
                Moveable(category, mapWidth, mapHeight, tileSize),
                startNode(start),
                parentNode(start),
                currentNode(start),
                endNode(end),
                map(map),
                circle(),
                maxVelocity(0.0048),
                distanceToTravel(0)
{
    tilePos = startNode->getTilePosition();
    pos = startNode->getCenteredPosition();

    circle.setRadius(10);
    circle.setFillColor(sf::Color::White);
    circle.setOrigin(10, 10);
    circle.setPosition(pos.x, pos.y);

    calculateVelocity();
}

Circle::~Circle()
{

}

void Circle::calculateVelocity()
{
    MapNode *nextNode;
    if(currentNode == endNode)
    {
        tilePos = startNode->getTilePosition();
        pos = startNode->getCenteredPosition();

        circle.setPosition(pos.x, pos.y);
        currentNode = startNode;
    }

    nextNode = currentNode->getChildPathNode();
    sf::Vector2f futurePos = nextNode->getCenteredPosition();
    velocity.x = (futurePos.x - pos.x) * maxVelocity;
    velocity.y = (futurePos.y - pos.y) * maxVelocity;

    calculateDistanceToTravel();

    //TODO the parent should be changed too. and the parent should now point to something new
    currentNode = nextNode;
}

void Circle::calculateDistanceToTravel()
{
    MapNode *nextNode = currentNode->getChildPathNode();
    sf::Vector2f futurePos = nextNode->getCenteredPosition();

    float xDist = futurePos.x - pos.x;
    float yDist = futurePos.y - pos.y;
    float retVal = xDist * xDist + yDist * yDist;

    distanceToTravel = sqrt(retVal);
}

void Circle::drawCurrent(sf::RenderTarget& target, sf::RenderStates states) const
{
    target.draw(circle);
}

void Circle::updateCurrent(sf::Time dt)
{
    sf::Vector2f vel;
    vel.x = velocity.x * dt.asMilliseconds();
    vel.y = velocity.y * dt.asMilliseconds();
    pos.x += vel.x;
    pos.y += vel.y;
    circle.setPosition(pos);
    tilePos.x = (int) pos.x / tileSize;
    tilePos.y = (int) pos.y / tileSize;

    float distance = vel.x * vel.x + vel.y * vel.y;

    distanceToTravel -= sqrt(distance);

    if(distanceToTravel <= 0)
    {
        calculateVelocity();
    }
}
