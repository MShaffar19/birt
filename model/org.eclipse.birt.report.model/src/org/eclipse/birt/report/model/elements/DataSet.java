/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.model.elements;

import java.util.List;

import org.eclipse.birt.report.model.activity.NotificationEvent;
import org.eclipse.birt.report.model.core.ReferenceableElement;
import org.eclipse.birt.report.model.metadata.PropertyValueException;
import org.eclipse.birt.report.model.util.StringUtil;

/**
 * This class represents a data set: a query, stored procedure, or other source
 * of data. A data set is a named object that provides a result set defined by a
 * sequence of data rows. Report elements use data sets to retrieve data for
 * display. A data set has three key parts:
 * <ul>
 * <li>Data access: Instructions for retrieving data from an external data
 * source. For example, and SQL query, a stored procedure definition, and so on.
 * <li>Report-specific properties: Properties for how the data is to be used in
 * the report such as rules for searching, data export and so on.
 * <li>Data transforms: Rules for processing the data for use by the report.
 * Data transforms are most frequently defined by report items that use the data
 * set, and are applied to the result set by BIRT.
 * </ul>
 * 
 *  
 */

public abstract class DataSet extends ReferenceableElement
{

	/**
	 * Name of the data source property.
	 */

	public static final String DATA_SOURCE_PROP = "dataSource"; //$NON-NLS-1$

	/**
	 * The property name of the script called before opening this data set.
	 */

	public static final String BEFORE_OPEN_METHOD = "beforeOpen"; //$NON-NLS-1$

	/**
	 * The property name of the script called before closing this data set.
	 */

	public static final String BEFORE_CLOSE_METHOD = "beforeClose"; //$NON-NLS-1$

	/**
	 * The property name of the script called after opening this data set.
	 */

	public static final String AFTER_OPEN_METHOD = "afterOpen"; //$NON-NLS-1$

	/**
	 * The property name of the script called after closing this data set.
	 */

	public static final String AFTER_CLOSE_METHOD = "afterClose"; //$NON-NLS-1$

	/**
	 * The property name of the script called when fetching this data set.
	 */

	public static final String ON_FETCH_METHOD = "onFetch"; //$NON-NLS-1$

	/**
	 * The property name of the input data set parameters definitions.
	 */

	public static final String INPUT_PARAMETERS_PROP = "inputParameters"; //$NON-NLS-1$

	/**
	 * The property name of the output data set parameters definitions.
	 */

	public static final String OUTPUT_PARAMETERS_PROP = "outputParameters"; //$NON-NLS-1$

	/**
	 * The property name of the data set parameter binding elements that bind
	 * data set input parameters to expressions.
	 */

	public static final String PARAM_BINDINGS_PROP = "paramBindings"; //$NON-NLS-1$

	/**
	 * The property name of the structures of the expected result set.
	 */
	public static final String RESULT_SET_PROP = "resultSet"; //$NON-NLS-1$

	/**
	 * The property name of the columns computed with expression.
	 */

	public static final String COMPUTED_COLUMNS_PROP = "computedColumns"; //$NON-NLS-1$

	/**
	 * The property name of the column hint elements.
	 */

	public static final String COLUMN_HINTS_PROP = "columnHints"; //$NON-NLS-1$

	/**
	 * The property name of the filters to apply to the data set.
	 */

	public static final String FILTER_PROP = "filter"; //$NON-NLS-1$
    
	/**
     * The property name of the cached data set information.
     */
    
    public static final String CACHED_METADATA_PROP = "cachedMetaData"; //$NON-NLS-1$
    

	/**
	 * Default constructor.
	 */

	public DataSet( )
	{
	}

	/**
	 * Constructs the data set with a required name.
	 * 
	 * @param theName
	 *            the required name
	 */

	public DataSet( String theName )
	{
		super( theName );
	}

	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.birt.report.model.core.DesignElement#validate(org.eclipse.birt.report.model.elements.ReportDesign)
	 */
	
	public List validate( ReportDesign design )
	{
		List list = super.validate( design );
		
		String value = getStringProperty( design, DATA_SOURCE_PROP );
		if ( StringUtil.isBlank( value ) )
		{
			list.add( new PropertyValueException( this, DATA_SOURCE_PROP,
					value, PropertyValueException.DESIGN_EXCEPTION_VALUE_REQUIRED ) );
		}

		// Check the element reference of dataSource property

		if ( !checkElementReference( design, DATA_SOURCE_PROP ) )
		{
			list.add( new SemanticError( this, new String[]{DATA_SOURCE_PROP},
					SemanticError.DESIGN_EXCEPTION_INVALID_ELEMENT_REF ) );
		}

		// Check input parameter structure list

		list.addAll( validateStructureList( design, INPUT_PARAMETERS_PROP ) );
		list.addAll( validateStructureList( design, OUTPUT_PARAMETERS_PROP ) );
		list.addAll( validateStructureList( design, PARAM_BINDINGS_PROP ) );
		list.addAll( validateStructureList( design, COMPUTED_COLUMNS_PROP ) );
		list.addAll( validateStructureList( design, COLUMN_HINTS_PROP ) );
		list.addAll( validateStructureList( design, FILTER_PROP ) );
		
		List columns = (List) getProperty( design, RESULT_SET_PROP );
		if ( columns != null && columns.size( ) == 0 )
		{
			list.add( new SemanticError( this, SemanticError.DESIGN_EXCEPTION_AT_LEAST_ONE_COLUMN ) );
		}

		return list; 
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.model.core.ReferenceableElement#setDeliveryPath(org.eclipse.birt.report.model.activity.NotificationEvent)
	 */

	protected void adjustDeliveryPath( NotificationEvent ev )
	{
		ev.setDeliveryPath( NotificationEvent.ELEMENT_CLIENT );
	}
}