#include "MapNodeLink.hpp"
#include "MapNode.hpp"

MapNodeLink::MapNodeLink() :
        next(NULL), node(NULL)
{
}

MapNodeLink *MapNodeLink::getNewNodeLink(MapNode *newNode)
{
    MapNodeLink *tmp = new MapNodeLink();
    tmp->setNode(newNode);
    return tmp;
}

void MapNodeLink::attachNode(MapNodeLink **parent, MapNode *newNode)
{
    MapNodeLink *nextLink;
    if(*parent == NULL)
    {
        *parent = new MapNodeLink();
        (*parent)->setNode(newNode);
        return;
    }

    MapNode *parentMapNode = (*parent)->getNode();
    if(newNode->getFValue() < parentMapNode->getFValue())
    {
        //TODO clean up this section/repeated NULL check
        if((*parent)->getNextLink() == NULL)
        {
            (*parent)->setNextLink(getNewNodeLink(parentMapNode));
            (*parent)->setNode(newNode);
            return;
        }

        nextLink = (*parent)->getNextLink();
        attachNode(&nextLink, parentMapNode);
        (*parent)->setNode(newNode);
        return;
    }
    else if(newNode->getFValue() == parentMapNode->getFValue())
    {
        if(newNode->getDistanceFromStart() < parentMapNode->getDistanceFromStart())
        {
            //TODO clean up this section/repeated NULL check
            if((*parent)->getNextLink() == NULL)
            {
                (*parent)->setNextLink(getNewNodeLink(parentMapNode));
                (*parent)->setNode(newNode);
                return;
            }

            nextLink = (*parent)->getNextLink();
            attachNode(&nextLink, parentMapNode);
            (*parent)->setNode(newNode);
            return;
        }
    }

    //TODO clean up this section/repeated NULL check
    if((*parent)->getNextLink() == NULL)
    {
        (*parent)->setNextLink(getNewNodeLink(newNode));
        return;
    }
    nextLink = (*parent)->getNextLink();
    attachNode(&nextLink, newNode);
}

void MapNodeLink::destroy(MapNodeLink *parent)
{
    if(parent == NULL)
        return;

    MapNodeLink *next = parent->getNextLink();
    delete (parent);
    destroy(next);
}
void MapNodeLink::setNextLink(MapNodeLink *link)
{
    next = link;
}

void MapNodeLink::setNode(MapNode *newNode)
{
    node = newNode;
}

MapNode * MapNodeLink::getNode()
{
    return node;
}

MapNodeLink * MapNodeLink::getNextLink()
{
    return next;
}
