package com.paths.utils;

public class GameStats
{
    private float crumbs;
    private float points;
    private float crumbMultiplier;
    private float pointMultiplier;
    
    public GameStats(float crumbs, float points, float crumbMultiplier, float pointMultiplier) {
        init(crumbs, points, crumbMultiplier, pointMultiplier);
    }
    
    public void init(float crumbs, float points, float crumbMultiplier, float pointMultiplier) {
        this.crumbs = crumbs;
        this.points = points;
        this.setCrumbMultiplier(crumbMultiplier);
        this.setPointMultiplier(pointMultiplier);
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
        System.out.println("Setting points to " + points);
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
