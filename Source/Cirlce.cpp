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
                maxVelocity(0.18),
                distanceToTravel(0)
{
    pos = startNode->getPosition();
    circle.setRadius(10);
    circle.setFillColor(sf::Color::White);
    circle.setOrigin(10, 10);
    circle.setPosition(startNode->getPosition().x + startNode->getTileWidth()/2, startNode->getPosition().y + startNode->getTileWidth()/2);

    calculateVelocity();

//    if(futurePos != NULL) {
//
//    }
//    start here with circle movements

}

Circle::~Circle()
{

}

void Circle::calculateVelocity()
{
    MapNode *nextNode = currentNode->getChildPathNode();
    sf::Vector2f futurePos = nextNode->getPosition();
    sf::Vector2f currentPos = currentNode->getPosition();
    velocity.x = abs(futurePos.x - currentPos.x) * maxVelocity;
    velocity.y = abs(futurePos.y - currentPos.y) * maxVelocity;

    calculateDistanceToTravel();

    //TODO the parent should be changed too. and the parent should now point to something new
    currentNode = nextNode;
}

void Circle::calculateDistanceToTravel()
{
    MapNode *nextNode = currentNode->getChildPathNode();
    sf::Vector2f futurePos = nextNode->getPosition();
    sf::Vector2f currentPos = currentNode->getPosition();

    //TODO change these hard coded 15s to be included with the getPosition call yo! (this still needs to be changed so that getPosition is the center of the square
    float yDist = futurePos.y - currentPos.y;
    float xDist = futurePos.x - currentPos.x;
    float retVal = xDist * xDist + yDist * yDist;

    distanceToTravel = sqrt(retVal);
}

void Circle::drawCurrent(sf::RenderTarget& target, sf::RenderStates states) const
{
    target.draw(circle);
}

void Circle::updateCurrent(sf::Time dt)
{
    sf::Vector2f vel = velocity;
    vel.x = velocity.x * dt.asMilliseconds();
    vel.y = velocity.y * dt.asMilliseconds();
    sf::Vector2f posBefore = circle.getPosition();
    circle.move(vel);
    sf::Vector2f posAfter = circle.getPosition();

    float yDist = posBefore.y/30 - posAfter.y/30;
    float xDist = posBefore.x/30 - posAfter.x/30;
    float retVal = xDist * xDist + yDist * yDist;

    distanceToTravel -= sqrt(retVal);
    std::cout << "distance: " << distanceToTravel << "\n";
    if(distanceToTravel <= 0)
    {
        std::cout << "Changing velocity!\n";
        calculateVelocity();
    }
}
