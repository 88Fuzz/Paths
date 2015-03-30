#ifndef __GAMESTATE_HPP__
#define __GAMESTATE_HPP__

#include "State.hpp"
#include "MapNode.hpp"
#include <SFML/Graphics/Sprite.hpp>
#include <SFML/Graphics/Text.hpp>
#include <SFML/Network/Packet.hpp>

class GameState: public State
{
public:
    GameState(StateStack*, Context);
    virtual ~GameState();

    virtual void draw();
    virtual bool update(sf::Time);
    virtual bool handleEvent(const sf::Event*);

private:
    void findPath();
    void calulateGHValues();
    void colorPath();
    float calculateDistance(sf::Vector2f, sf::Vector2f);
    float calculateDistanceToEnd(sf::Vector2f);
    void resetOpenClosed();

    sf::RenderWindow* window;
    MapNode map;
    MapNode *startNode;
    MapNode *endNode;

    int squareSize;
    sf::Vector2u windowSize;
    sf::Vector2u windowTileSize;
    MapNode::Type squareType;
    bool buttonToggle;
};

#endif
