package com.paths.utils;

public class GameStats
{
    private float health;
    private float crumbs;
    private float points;
    private float crumbMultiplier;
    private float pointMultiplier;
    
    public GameStats(float crumbs, float points, float crumbMultiplier, float pointMultiplier, float health) {
        init(crumbs, points, crumbMultiplier, pointMultiplier, health);
    }
    
    public void init(float crumbs, float points, float crumbMultiplier, float pointMultiplier, float health) {
        this.health = health;
        this.crumbs = crumbs;
        this.points = points;
        this.setCrumbMultiplier(crumbMultiplier);
        this.setPointMultiplier(pointMultiplier);
    }
    
    public float getHealth()
    {
        return health;
    }
    
    public void addHealth(float health)
    {
        this.health += health;
    }

    public float getCrumbs()
    {
        return crumbs;
    }

    public void addCrumbs(float crumbs)
    {
        this.crumbs += crumbs;
    }

    public float getPoints()
    {
        return points;
    }

    public void addPoints(float points)
    {
        this.points += points;
    }

    public float getCrumbMultiplier()
    {
        return crumbMultiplier;
    }

    public void setCrumbMultiplier(float crumbMultiplier)
    {
        this.crumbMultiplier = crumbMultiplier;
    }

    public float getPointsMultiplier()
    {
        return pointMultiplier;
    }

    public void setPointMultiplier(float pointMultiplier)
    {
        this.pointMultiplier = pointMultiplier;
    }
}
