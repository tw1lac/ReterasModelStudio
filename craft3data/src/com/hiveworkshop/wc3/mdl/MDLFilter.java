package com.hiveworkshop.wc3.mdl;

import javax.swing.filechooser.FileFilter;
import java.io.File;
/**
 * Write a description of class MDLFilter here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MDLFilter extends FileFilter
{
    @Override
	public boolean accept(File f)
    {
        //Special thanks to the Oracle Java tutorials for a lot of the major code concepts here
        if( f.isDirectory() )
        {
            return true;
        }
        String name = f.getName();
        int perIndex = name.lastIndexOf('.');
        return name.substring(perIndex + 1).toLowerCase().equals("mdl") && perIndex > 0 && perIndex < name.length() - 1;
    }
    
    @Override
	public String getDescription()
    {
        return "Warcraft III Model Files \"-.mdl\"";
    }
}
