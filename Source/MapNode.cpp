#include "MapNode.hpp"
#include <iostream>

MapNode::MapNode() :
        MapNode(0, 0, 0, 0, MapNode::Type::NONE)
{

}

MapNode::MapNode(int x, int y, int width, int height, MapNode::Type type) :
                SceneNode(),
                gValue(0),
                hValue(0),
                parentPathNode(NULL),
                childPathNode(NULL),
                tileWidth(width),
                tileHeight(height)
{
    pos = sf::Vector2f(x * width, y * height);

    rect.setPosition(pos.x, pos.y);
    rect.setSize(sf::Vector2f(width - 1, height - 1));
    rect.setOutlineColor(sf::Color::Black);
    setType(type);
}

MapNode::~MapNode()
{

}

sf::Vector2f MapNode::getPosition()
{
    return pos;
}

sf::Vector2f MapNode::getTilePosition()
{
    std::cout << "pos x: " << pos.x << " y: " << pos.y << "\n";
    sf::Vector2f tmpPos = sf::Vector2f(pos.x / tileWidth, pos.y / tileHeight);
    std::cout << " x " << tmpPos.x << " y " << tmpPos.y << "\n";
    return tmpPos;
}

//TODO decide if this should be returning an int or a float
int MapNode::getTileWidth()
{
    return tileWidth;
}

//TODO decide if this should be returning an int or a float
int MapNode::getTileHeight()
{
    return tileHeight;
}

void MapNode::drawCurrent(sf::RenderTarget& target, sf::RenderStates states) const
{
    target.draw(rect);
}

MapNode::Type MapNode::getType()
{
    return nodeType;
}

void MapNode::setType(MapNode::Type type)
{
    nodeType = type;
    rect.setFillColor(getTypeColor(type));
}

sf::Color MapNode::getTypeColor(MapNode::Type type)
{
    switch(type)
    {
    case MapNode::Type::REGULAR:
        //GREEN
        return sf::Color(0x00, 0x99, 0x00);
    case MapNode::Type::START:
        //RED
        return sf::Color(0xFF, 0x00, 0x00);
    case MapNode::Type::END:
        //YELLOw
        return sf::Color(0xFF, 0xFF, 0x00);
    case MapNode::Type::PATH:
        //ORANGE
        return sf::Color(0xFF, 0x80, 0x00);
    case MapNode::Type::OPEN:
        //CYAN
        return sf::Color(0x00, 0xFF, 0xFF);
    case MapNode::Type::CLOSED:
        //PURPLE
        return sf::Color(0x7F, 0x00, 0xFF);
    case MapNode::Type::BLOCK:
        //BLACK
        return sf::Color(0x00, 0x00, 0x00);
    case MapNode::Type::NONE:
    default:
        //CLEAR
        return sf::Color(0x00, 0x00, 0x00, 0x00);
    }
}

float MapNode::getFValue()
{
    return gValue + hValue;
}

float MapNode::getDistanceFromStart()
{
    return gValue;
}
float MapNode::getDistanceToEnd()
{
    return hValue;
}

void MapNode::setDistanceFromStart(float value)
{
    gValue = value;
}

void MapNode::setDistanceToEnd(float value)
{
    hValue = value;
}

void MapNode::setParentPathNode(MapNode *node)
{
    parentPathNode = node;
}

MapNode * MapNode::getParentPathNode()
{
    return parentPathNode;
}

MapNode *MapNode::getChildPathNode()
{
    return childPathNode;
}

void MapNode::setChildPathNode(MapNode *node)
{
    childPathNode = node;
}
