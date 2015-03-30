#ifndef __MAPNODELINK_HPP__
#define __MAPNODELINK_HPP__

#include "MapNode.hpp"

class MapNodeLink
{
public:
    MapNodeLink();
    static void attachNode(MapNodeLink **, MapNode *);
    static void destroy(MapNodeLink *);
    static MapNodeLink *getNewNodeLink(MapNode *newNode);
    MapNode *getNode();
    void setNextLink(MapNodeLink *);
    void setNode(MapNode *);
    MapNodeLink *getNextLink();

private:
    MapNodeLink *next;
    MapNode *node;
};

#endif
