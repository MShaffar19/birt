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

package org.eclipse.birt.report.item.crosstab.internal.ui;

import java.util.Iterator;
import java.util.List;

import org.eclipse.birt.report.designer.internal.ui.editors.schematic.providers.ISchematicMenuListener;
import org.eclipse.birt.report.designer.internal.ui.extension.ExtendedElementUIPoint;
import org.eclipse.birt.report.designer.internal.ui.extension.ExtensionPointManager;
import org.eclipse.birt.report.designer.internal.ui.extension.experimental.EditpartExtensionManager;
import org.eclipse.birt.report.designer.internal.ui.extension.experimental.PaletteEntryExtension;
import org.eclipse.birt.report.designer.nls.Messages;
import org.eclipse.birt.report.designer.ui.actions.GeneralInsertMenuAction;
import org.eclipse.birt.report.designer.ui.actions.InsertAggregationAction;
import org.eclipse.birt.report.designer.util.DEUtil;
import org.eclipse.birt.report.item.crosstab.internal.ui.editors.action.AddLevelHandleAction;
import org.eclipse.birt.report.item.crosstab.internal.ui.editors.action.AddMesureViewHandleAction;
import org.eclipse.birt.report.item.crosstab.internal.ui.editors.action.AddSubTotalAction;
import org.eclipse.birt.report.item.crosstab.internal.ui.editors.action.DeleteDimensionViewHandleAction;
import org.eclipse.birt.report.item.crosstab.internal.ui.editors.action.DeleteMeasureHandleAction;
import org.eclipse.birt.report.item.crosstab.internal.ui.editors.model.CrosstabCellAdapter;
import org.eclipse.birt.report.item.crosstab.internal.ui.editors.model.ICrosstabCellAdapterFactory;
import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.metadata.IElementDefn;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.UpdateAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;

/**
 * 
 */

public class CrosstabCellMenuAdapterFactory implements IAdapterFactory
{

	private ActionRegistry actionRegistry;

	private void createMeasureMenu( IMenuManager menu, Object firstSelectedObj,
			IContributionItem beforeThis )
	{
		DesignElementHandle element = null;
		String firstId = beforeThis.getId( );
		if ( firstSelectedObj instanceof DesignElementHandle )
		{
			element = (DesignElementHandle) firstSelectedObj;
		}
		else if ( firstSelectedObj instanceof CrosstabCellAdapter )
		{
			element = ( (CrosstabCellAdapter) firstSelectedObj ).getDesignElementHandle( );
		}
		if ( element != null )
		{
			IAction action = new AddMesureViewHandleAction( (DesignElementHandle) element );
			menu.insertBefore( firstId, action );

			action = new DeleteMeasureHandleAction( (DesignElementHandle) element );
			menu.insertBefore( firstId, action );
		}
	}

	private void createLevelMenu( IMenuManager menu, Object firstSelectedObj,
			IContributionItem beforeThis )
	{
		DesignElementHandle element = null;
		if ( firstSelectedObj instanceof DesignElementHandle )
		{
			element = (DesignElementHandle) firstSelectedObj;
		}
		else if ( firstSelectedObj instanceof CrosstabCellAdapter )
		{
			element = ( (CrosstabCellAdapter) firstSelectedObj ).getDesignElementHandle( );
		}

		String firstId = beforeThis.getId( );
		if ( element != null )
		{
			IAction action = new AddLevelHandleAction( (DesignElementHandle) element );
			menu.insertBefore( firstId, action );

			action = new AddSubTotalAction( (DesignElementHandle) element );
			menu.insertBefore( firstId, action );

			action = new DeleteDimensionViewHandleAction( (DesignElementHandle) element );
			menu.insertBefore( firstId, action );
		}

	}

	public Object getAdapter( Object adaptableObject, Class adapterType )
	{
		if ( adaptableObject instanceof CrosstabCellAdapter
				&& ( (CrosstabCellAdapter) adaptableObject ).getCrosstabCellHandle( ) != null
				&& adapterType == IMenuListener.class )
		{
			final String position = ( (CrosstabCellAdapter) adaptableObject ).getPositionType( );
			final CrosstabCellAdapter firstSelectedElement = (CrosstabCellAdapter) adaptableObject;
			return new ISchematicMenuListener( ) {

				private ActionRegistry actionRegistry;

				public void menuAboutToShow( IMenuManager manager )
				{
					// items.length must be larger than 0
					IContributionItem items[] = manager.getItems( );
					IContributionItem firstMemuItem = items[0];

					if ( ICrosstabCellAdapterFactory.CELL_FIRST_LEVEL_HANDLE.equals( position ) )
					{
						createLevelMenu( manager,
								firstSelectedElement,
								firstMemuItem );
						manager.insertBefore( firstMemuItem.getId( ),
								new Separator( ) );
					}
					else if ( ICrosstabCellAdapterFactory.CELL_MEASURE.equals( position ) )
					{
						createMeasureMenu( manager,
								firstSelectedElement,
								firstMemuItem );
						manager.insertBefore( firstMemuItem.getId( ),
								new Separator( ) );
					}

					MenuManager subMenu = new MenuManager( Messages.getString( "SchematicContextMenuProvider.Menu.insertElement" ) );

					IAction action = getAction( GeneralInsertMenuAction.INSERT_LABEL_ID );
					action.setText( GeneralInsertMenuAction.INSERT_LABEL_DISPLAY_TEXT );
					subMenu.add( action );

					action = getAction( GeneralInsertMenuAction.INSERT_TEXT_ID );
					action.setText( GeneralInsertMenuAction.INSERT_TEXT_DISPLAY_TEXT );
					subMenu.add( action );

					action = getAction( GeneralInsertMenuAction.INSERT_DYNAMIC_TEXT_ID );
					action.setText( GeneralInsertMenuAction.INSERT_DYNAMIC_TEXT_DISPLAY_TEXT );
					subMenu.add( action );

					action = getAction( GeneralInsertMenuAction.INSERT_DATA_ID );
					action.setText( GeneralInsertMenuAction.INSERT_DATA_DISPLAY_TEXT );
					subMenu.add( action );

					action = getAction( GeneralInsertMenuAction.INSERT_IMAGE_ID );
					action.setText( GeneralInsertMenuAction.INSERT_IMAGE_DISPLAY_TEXT );
					subMenu.add( action );

					action = getAction( GeneralInsertMenuAction.INSERT_GRID_ID );
					action.setText( GeneralInsertMenuAction.INSERT_GRID_DISPLAY_TEXT );
					subMenu.add( action );

					action = getAction( GeneralInsertMenuAction.INSERT_LIST_ID );
					action.setText( GeneralInsertMenuAction.INSERT_LIST_DISPLAY_TEXT );
					subMenu.add( action );

					action = getAction( GeneralInsertMenuAction.INSERT_TABLE_ID );
					action.setText( GeneralInsertMenuAction.INSERT_TABLE_DISPLAY_TEXT );
					subMenu.add( action );

					/*
					 * Extended Items insert actions
					 */

					List points = ExtensionPointManager.getInstance( )
							.getExtendedElementPoints( );
					for ( Iterator iter = points.iterator( ); iter.hasNext( ); )
					{
						ExtendedElementUIPoint point = (ExtendedElementUIPoint) iter.next( );

						IElementDefn extension = DEUtil.getMetaDataDictionary( )
								.getExtension( point.getExtensionName( ) );
						String displayName = new String( );
						displayName = extension.getDisplayName( );

						action = getAction( point.getExtensionName( ) );
						if ( action != null )
						{
							if ( displayName.equalsIgnoreCase( "Chart" ) ) //$NON-NLS-1$
							{
								action.setText( "&" + displayName ); //$NON-NLS-1$
							}
							else
							{
								action.setText( displayName );
							}
							subMenu.add( action );
						}
					}

					PaletteEntryExtension[] entries = EditpartExtensionManager.getPaletteEntries( );
					for ( int i = 0; i < entries.length; i++ )
					{
						action = getAction( entries[i].getItemName( ) );
						action.setText( entries[i].getMenuLabel( ) );
						subMenu.add( action );
					}

					subMenu.add( new Separator( ) );
					action = getAction( InsertAggregationAction.ID );
					action.setText( InsertAggregationAction.TEXT );
					subMenu.add( action );

					manager.add( subMenu );
				}

				public void setActionRegistry( ActionRegistry actionRegistry )
				{
					this.actionRegistry = actionRegistry;
				}

				protected IAction getAction( String actionID )
				{
					IAction action = getActionRegistry( ).getAction( actionID );
					if ( action instanceof UpdateAction )
					{
						( (UpdateAction) action ).update( );
					}
					return action;
				}

				private ActionRegistry getActionRegistry( )
				{
					if ( actionRegistry == null )
						actionRegistry = new ActionRegistry( );
					return actionRegistry;
				}
			};
		}
		return null;
	}

	public Class[] getAdapterList( )
	{
		// TODO Auto-generated method stub
		return null;
	}

}
