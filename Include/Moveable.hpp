#ifndef __MOVEABLE_HPP__
#define __MOVEABLE_HPP__
#include "SceneNode.hpp"
#include "Category.hpp"

class Moveable: public SceneNode
{
public:

    Moveable(Category::Type, int, int, int);
    virtual ~Moveable();
    virtual void destroy();

    void setMapWidth(int);
    int getMapWidth();
    void setMapHeight(int);
    int getMapHeight();
    void setTileSize(int);
    int getTileSize();

protected:
    //Map information
    //the width of the map tiles, used to set the appropriate position for the sprite
    int mapWidth;
    int mapHeight;
    int tileSize;
    sf::Vector2f velocity;

    sf::Vector2i tilePos;
};

#endif
