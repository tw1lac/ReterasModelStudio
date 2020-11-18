package com.hiveworkshop.wc3.mdl;

/**
 * Something to keep track of which stuff has visibility, 
 * 
 * Eric Theller
 * 6/28/2012
 */
public interface VisibilitySource extends Named
{
    AnimFlag getVisibilityFlag();
    void setVisibilityFlag(AnimFlag what);
//     public String getVisTagname();
String visFlagName();
}
