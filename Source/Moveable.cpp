#include "Moveable.hpp"
#include <iostream>

Moveable::Moveable(Category::Type category, int mapWidth, int mapHeight, int tileSize) :
        SceneNode(category), tileSize(tileSize), mapWidth(mapWidth), mapHeight(mapHeight)
{
}

Moveable::~Moveable()
{
    destroy();
}

void Moveable::destroy()
{
    SceneNode::destroy();
}

void Moveable::setMapWidth(int width)
{
    mapWidth = width;
}

int Moveable::getMapWidth()
{
    return mapWidth;
}

void Moveable::setMapHeight(int height)
{
    mapHeight = height;
}

int Moveable::getMapHeight()
{
    return mapHeight;
}

void Moveable::setTileSize(int size)
{
    tileSize = size;
}

int Moveable::getTileSize()
{
    return tileSize;
}
