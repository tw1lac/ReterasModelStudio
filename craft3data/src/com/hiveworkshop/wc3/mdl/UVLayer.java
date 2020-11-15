package com.hiveworkshop.wc3.mdl;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.PrintWriter;
/**
 * A layer of TVertices (UV Mapping)
 * 
 * Eric Theller
 * 3/10/2012
 */
public class UVLayer
{
    ArrayList<TVertex> tverts;
    public UVLayer()
    {
        tverts = new ArrayList<TVertex>();
    }
    public void addTVertex(TVertex v)
    {
        tverts.add(v);
    }
    public TVertex getTVertex(int vertId)
    {
        return tverts.get(vertId);
    }
    public int numTVerteces()
    {
        return tverts.size();
    }
    public static UVLayer read(BufferedReader mdl)
    {
        UVLayer temp = new UVLayer();
        String line = "";
        while( !((line = MDLReader.nextLine(mdl)).contains("\t}") ) )
        {
            temp.addTVertex(TVertex.parseText(line));
        }
        return temp;
    }
    public void printTo(PrintWriter writer, int tabHeight, boolean addHeader)
    {
        //Here we may assume the header "TVertices" to already have been written,
        // based on addHeader
        StringBuilder tabs = new StringBuilder();
        for( int i = 0; i < tabHeight; i++ )
        {
            tabs.append("\t");
        }
        String inTabs = tabs.toString();
        if( addHeader )
        {
            inTabs = inTabs + "\t";
            writer.println(tabs + "TVertices "+tverts.size() +" {");
        }
        for (TVertex tvert : tverts) {
            writer.println(inTabs + tvert.toString() + ",");
        }
       
        if( addHeader )
        {
            writer.println(tabs+"}");
        }
    }
}
