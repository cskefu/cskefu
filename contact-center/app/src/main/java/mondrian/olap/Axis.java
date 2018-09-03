/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// You must accept the terms of that agreement to use this software.
//
// Copyright (C) 2001-2005 Julian Hyde
// Copyright (C) 2005-2007 Pentaho and others
// All Rights Reserved.
*/

package mondrian.olap;

import java.util.List;

/**
 * A <code>Axis</code> is a component of a {@link Result}.
 * It contains a list of {@link Position}s.
 *
 * @author jhyde
 * @since 6 August, 2001
 */
public interface Axis {
	int getDataSize();
    List<Position> getPositions();
}
// End Axis.java
