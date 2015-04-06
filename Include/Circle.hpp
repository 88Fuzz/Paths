#ifndef __CIRCLE_HPP__
#define __CIRCLE_HPP__

#include "Moveable.hpp"
#include "MapNode.hpp"
#include "MySprite.hpp"
#include <SFML/Graphics/CircleShape.hpp>

class Circle: public Moveable
{
public:
    Circle(Category::Type, int, int, int, MapNode *, MapNode *, MapNode*);
    virtual ~Circle();

    virtual void drawCurrent(sf::RenderTarget&, sf::RenderStates) const;
    virtual void updateCurrent(sf::Time);

private:
    void calculateDistanceToTravel();
    void calculateVelocity();

    float maxVelocity;
    float distanceToTravel;
    MapNode *startNode;
    MapNode *currentNode;
    MapNode *parentNode;
    MapNode *endNode;
    MapNode *map;
    //TODO change circle and SceneNode's rect
    sf::CircleShape circle;
//    MySprite sprite;
    sf::Vector2f pos;
    sf::Vector2f tilePos;
};

#endif
