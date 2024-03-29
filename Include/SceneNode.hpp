#ifndef __SCENENODE_HPP__
#define __SCENENODE_HPP__

#include "Category.hpp"
#include <SFML/System/NonCopyable.hpp>
#include <SFML/System/Time.hpp>
#include <SFML/Graphics/Transformable.hpp>
#include <SFML/Graphics/Drawable.hpp>
#include <vector>
#include <queue>

struct Command;
//class CommandQueue;
class Player;

class SceneNode: public sf::Transformable, public sf::Drawable, private sf::NonCopyable
{
public:
    static int get1d(int, int, int);

    explicit SceneNode(Category::Type category = Category::NONE);
    virtual ~SceneNode();
    void attachChild(SceneNode*);
    SceneNode* detachChild(SceneNode*);
//    void update(sf::Time, CommandQueue& commands);
    void update(sf::Time);

    sf::Vector2f getWorldPosition() const;
    sf::Transform getWorldTransform() const;

    void onCommand(const Command*, sf::Time);
    virtual unsigned int getCategory() const;

    virtual sf::FloatRect getBoundingRect() const;
    virtual bool isAlive();
    virtual bool isDestroyed() const;
    virtual void destroy();
    SceneNode* getChildNode(int);
    SceneNode* getChildNode(int, Category::Type);
    SceneNode* getChildNode(int, Category::Type, int);
    Player* removePlayerChildNode(int);
    int layerChildNode(SceneNode *, int);

    std::vector<SceneNode *>::iterator getChildIteratorBegin();
    std::vector<SceneNode *>::iterator getChildIteratorEnd();

protected:
    std::vector<SceneNode *> children;
    SceneNode *parent;

private:
//    virtual void updateCurrent(sf::Time dt, CommandQueue& commands);
    virtual void updateCurrent(sf::Time);
//    void updateChildren(sf::Time dt, CommandQueue& commands);
    void updateChildren(sf::Time);

    virtual void draw(sf::RenderTarget&, sf::RenderStates) const;
    virtual void drawCurrent(sf::RenderTarget&, sf::RenderStates) const;
    void drawChildren(sf::RenderTarget&, sf::RenderStates) const;
    void drawBoundingRect(sf::RenderTarget&, sf::RenderStates) const;
    Player* findAndRemovePlayer();
    SceneNode* findPlayer();

    Category::Type type;
};

#endif
