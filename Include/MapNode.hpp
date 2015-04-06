#ifndef __MAPNODE_HPP__
#define __MAPNODE_HPP__

#include "SceneNode.hpp"
#include <SFML/Graphics/RenderTarget.hpp>
#include <SFML/Graphics/RectangleShape.hpp>
#include <SFML/Graphics/Color.hpp>

class MapNode: public SceneNode
{
public:
    enum Type
    {
        START = 1 << 0,
        END = 1 << 1,
        BLOCK = 1 << 2,
        REGULAR = 1 << 3,
        PATH = 1 << 4,
        OPEN = 1 << 5,
        CLOSED = 1 << 6,
        NONE = 1 << 7,
        BLOCKING = BLOCK | NONE,
        START_OR_END = START | END
    };

    MapNode();
    MapNode(int, int, int, int, MapNode::Type);
    virtual ~MapNode();
    virtual void drawCurrent(sf::RenderTarget& target, sf::RenderStates states) const;

    void setType(MapNode::Type);
    MapNode::Type getType();
    sf::Vector2f getPosition();
    sf::Vector2f getTilePosition();
    sf::Vector2f getCenteredPosition();
    int getTileWidth();
    int getTileHeight();

    //gValue + hValue
    float getFValue();
    float getDistanceFromStart();
    float getDistanceToEnd();

    void setDistanceFromStart(float);
    void setDistanceToEnd(float);

    MapNode *getParentPathNode();
    void setParentPathNode(MapNode *);
    MapNode *getChildPathNode();
    void setChildPathNode(MapNode *);

private:
    //Distance from startingNode
    float gValue;
    //Distance from endingNode
    float hValue;

    sf::Vector2f pos;
    MapNode::Type nodeType;
    sf::Color getTypeColor(MapNode::Type);
    sf::RectangleShape rect;

    MapNode *parentPathNode;
    MapNode *childPathNode;
    int tileWidth;
    int tileHeight;
};

#endif
