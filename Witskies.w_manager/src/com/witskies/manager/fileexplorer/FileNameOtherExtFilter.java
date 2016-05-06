/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * This file is part of FileExplorer.
 *
 * FileExplorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FileExplorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.witskies.manager.fileexplorer;

import java.util.Arrays;
import java.util.HashSet;

public class FileNameOtherExtFilter extends FileNameExtFilter {

    private HashSet<FileNameExtFilter> mExts;

    // using lower case
    public FileNameOtherExtFilter(FileNameExtFilter[] exts) {
        super(null);
        mExts = new HashSet<FileNameExtFilter>();
        mExts.addAll(Arrays.asList(exts));
    }

    @Override
    public boolean contains(String ext) {
        for (FileNameExtFilter f : mExts) {
            if (f.contains(ext))
                return false;
        }
        return true;
    }
}
