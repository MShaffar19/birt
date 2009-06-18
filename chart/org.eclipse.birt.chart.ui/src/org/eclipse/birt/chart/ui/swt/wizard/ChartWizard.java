/***********************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Actuate Corporation - initial API and implementation
 ***********************************************************************/

package org.eclipse.birt.chart.ui.swt.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.ui.i18n.Messages;
import org.eclipse.birt.chart.ui.swt.interfaces.ITaskChangeListener;
import org.eclipse.birt.chart.ui.util.ChartCacheManager;
import org.eclipse.birt.chart.ui.util.UIHelper;
import org.eclipse.birt.core.ui.frameworks.taskwizard.TasksManager;
import org.eclipse.birt.core.ui.frameworks.taskwizard.WizardBase;
import org.eclipse.birt.core.ui.frameworks.taskwizard.interfaces.IButtonHandler;
import org.eclipse.birt.core.ui.frameworks.taskwizard.interfaces.ITask;
import org.eclipse.birt.core.ui.frameworks.taskwizard.interfaces.IWizardContext;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

/**
 * Chart builder for BIRT designer.
 * 
 */
public class ChartWizard extends WizardBase
{

	public static final String PreviewPainter_ID = "ChartPreviewPainter"; //$NON-NLS-1$
	public static final String RepDSProvider_Cube_ID = "ReportDataServiceProvider.setDataCube"; //$NON-NLS-1$
	public static final String RepDSProvider_Set_ID = "ReportDataServiceProvider.setDataSet"; //$NON-NLS-1$
	public static final String RepDSProvider_Ref_ID = "ReportDataServiceProvider.setReportItemReference"; //$NON-NLS-1$
	public static final String RepDSProvider_Style_ID = "ReportDataServiceProvider.setStyle"; //$NON-NLS-1$
	public static final String StaChartDSh_switch_ID = "StandardChartDataSheet.swtichDataSet"; //$NON-NLS-1$
	public static final String StaChartDSh_dPreview_ID = "StandChartDataSheet.dataPreview"; //$NON-NLS-1$
	public static final String StaChartDSh_gHeaders_ID = "StandardChartDataSheet.getPreviewHeaders"; //$NON-NLS-1$
	public static final String StaChartDSh_checCube_ID = "StandardChartDataSheet.checkCubeColumnBinding"; //$NON-NLS-1$
	public static final String ChartColBinDia_ID = "ChartColumnBindingDialog"; //$NON-NLS-1$
	public static final String FormatSpeciCom_ID = "FormatSpecifierComposite"; //$NON-NLS-1$
	public static final String MarkerEdiCom_ID = "MarkerEditorComposite"; //$NON-NLS-1$
	public static final String PluginSet_getAggF_ID = "PluginSettings.getAggregateFunc"; //$NON-NLS-1$
	public static final String PluginSet_getDPDef_ID = "PluginSettings.getDataPointDefinition"; //$NON-NLS-1$
	public static final String CheckSeriesBindingType_ID = "CheckSeriesBindingType_"; //$NON-NLS-1$
	public static final String TaskSelType_chOvST_ID = "TaskSelectType.changeOverlaySeriesType"; //$NON-NLS-1$
	public static final String TaskSelType_refreCh_ID = "TaskSelectType.refreshChart"; //$NON-NLS-1$
	public static final String LineSMarkerSh_ID = "LineSeriesMarkerSheet"; //$NON-NLS-1$
	public static final String SeriesShImpl_ID = "SeriesSheetImpl.getNewSeries"; //$NON-NLS-1$
	public static final String ChartUIUtil_pLiPreview_ID = "ChartUIUtil.prepareLivePreview"; //$NON-NLS-1$
	public static final String Gatt_aggCheck_ID = "Gantt.aggCheck_"; //$NON-NLS-1$
	public static final String ChartUIUtil_cGType_ID = "ChartUIUtil.checkGroupType"; //$NON-NLS-1$

	private static final int CHART_WIZARD_WIDTH_MINMUM = 690;

	private static final int CHART_WIZARD_HEIGHT_MINMUM = 670;

	/**
	 * Store all the unfixed error messages when operating.
	 */
	private static Map<String, String> errors = new HashMap<String, String>( 3 );

	/**
	 * Indicates whether the popup is being closed by users
	 */
	public static boolean POPUP_CLOSING_BY_USER = true;

	/**
	 * Caches last opened task of each wizard
	 */
	private static Map<String, String> lastTask = new HashMap<String, String>( 3 );

	private ChartAdapter adapter = null;

	public ChartWizard( )
	{
		this( null );
	}

	/**
	 * Creates the chart wizard using a specified shell, such as a workbench
	 * shell
	 * 
	 * @param parentShell
	 *            parent shell. Null indicates using a new shell
	 */
	public ChartWizard( Shell parentShell )
	{
		this( parentShell,
				ChartWizard.class.getName( ),
				CHART_WIZARD_WIDTH_MINMUM,
				CHART_WIZARD_HEIGHT_MINMUM,
				Messages.getString( "ChartWizard.Title.ChartBuilder" ), //$NON-NLS-1$
				UIHelper.getImage( "icons/obj16/chartselector.gif" ), //$NON-NLS-1$
				Messages.getString( "ChartWizard.Label.SelectChartTypeDataFormat" ), //$NON-NLS-1$
				UIHelper.getImage( "icons/wizban/chartwizardtaskbar.gif" ) ); //$NON-NLS-1$
	}

	protected ChartWizard( Shell parentShell, String wizardId,
			int iInitialWidth, int iInitialHeight, String strTitle,
			Image imgTitle, String strHeader, Image imgHeader )
	{
		super( parentShell,
				wizardId,
				iInitialWidth,
				iInitialHeight,
				strTitle,
				imgTitle,
				strHeader,
				imgHeader );
		setWizardClosedWhenEnterPressed( false );
		adapter = new ChartAdapter( this );
	}

	public void addTask( String sTaskID )
	{
		super.addTask( sTaskID );
		ITask task = TasksManager.instance( ).getTask( sTaskID );
		if ( task instanceof ITaskChangeListener )
		{
			adapter.addListener( (ITaskChangeListener) task );
		}
	}

	private void removeAllAdapters( EObject chart )
	{
		chart.eAdapters( ).remove( adapter );
		TreeIterator<EObject> iterator = chart.eAllContents( );
		while ( iterator.hasNext( ) )
		{
			EObject oModel = iterator.next( );
			oModel.eAdapters( ).remove( adapter );
		}
	}

	/**
	 * Returns the object which can add adapters
	 * 
	 * @param context
	 *            wizard context
	 * @return object to add adapters
	 */
	protected EObject getAdaptableObject( IWizardContext context )
	{
		return ( (ChartWizardContext) context ).getModel( );
	}

	public void dispose( )
	{
		if ( getContext( ) != null )
		{
			// Dispose data sheet
			getContext( ).getDataSheet( ).dispose( );

			EObject chart = getAdaptableObject( getContext( ) );
			if ( chart != null )
			{
				// Remove all adapters
				removeAllAdapters( chart );

				// Remove cache data
				ChartCacheManager.getInstance( ).dispose( );
			}
		}
		super.dispose( );
	}

	public EContentAdapter getAdapter( )
	{
		return adapter;
	}

	protected ChartWizardContext getContext( )
	{
		return (ChartWizardContext) context;
	}

	public String[] validate( )
	{
		return getContext( ).getUIServiceProvider( )
				.validate( getContext( ).getModel( ),
						getContext( ).getExtendedItem( ) );
	}
	
	protected Chart getChartModel( IWizardContext context )
	{
		return ( (ChartWizardContext) context ).getModel( );
	}

	public IWizardContext open( String[] sTasks, String topTaskId,
			IWizardContext initialContext )
	{
		// clear the errors when open a new wizard.
		errors.clear( );
		Chart chart = getChartModel( initialContext );

		if ( chart == null )
		{
			setTitle( getTitleNewChart( ) );
		}
		else
		{
			setTitle( getTitleEditChart( ) );
			// Add adapters to chart model
			getAdaptableObject( initialContext ).eAdapters( ).add( adapter );
		}

		if ( chart == null )
		{
			// If no chart model, always open the first task
			topTaskId = null;
		}
		else if ( topTaskId == null )
		{
			// Try to get last opened task if no task specified
			topTaskId = lastTask.get( initialContext.getWizardID( ) );
		}

		return super.open( sTasks, topTaskId, initialContext );
	}

	/**
	 * Updates wizard title as Edit chart.
	 * 
	 */
	public void updateTitleAsEdit( )
	{
		if ( getTitle( ).equals( getTitleNewChart( ) ) )
		{
			setTitle( getTitleEditChart( ) );
			getDialog( ).getShell( ).setText( getTitleEditChart( ) );
		}
	}

	/**
	 * Updates Apply button with enabled status.
	 * 
	 */
	public void updateApplyButton( )
	{
		List<IButtonHandler> buttonList = getCustomButtons( );
		for ( int i = 0; i < buttonList.size( ); i++ )
		{
			if ( buttonList.get( i ) instanceof ApplyButtonHandler )
			{
				Button applyButton = ( (ApplyButtonHandler) buttonList.get( i ) ).getButton( );
				if ( !applyButton.isEnabled( ) )
				{
					applyButton.setEnabled( true );
				}
			}
		}
	}

	public void detachPopup( )
	{
		POPUP_CLOSING_BY_USER = false;
		super.detachPopup( );
		POPUP_CLOSING_BY_USER = true;
	}

	public void switchTo( String sTaskID )
	{
		lastTask.put( getContext( ).getWizardID( ), sTaskID );
		super.switchTo( sTaskID );
	}

	protected String getTitleNewChart( )
	{
		return Messages.getString( "ChartWizard.Title.NewChart" ); //$NON-NLS-1$
	}

	protected String getTitleEditChart( )
	{
		return Messages.getString( "ChartWizard.Title.EditChart" ); //$NON-NLS-1$
	}

	public static void showException( String key, String errorMessage )
	{
		if ( errorMessage != null )
		{
			WizardBase.showException( errorMessage );
			errors.put( key, errorMessage );
		}
	}

	public static void removeException( String key )
	{
		boolean removed = false;
		String error = errors.get( key );
		if ( error != null && error.equals( WizardBase.getErrors( ) ) )
		{
			WizardBase.removeException( );
			removed = true;
		}

		errors.remove( key );
		// show other unfixed exceptions
		if ( ( removed || WizardBase.getErrors( ) == null )
				&& errors.size( ) > 0 )
		{
			String es = errors.values( ).toArray( new String[errors.size( )] )[0];
			WizardBase.showException( es );
		}
	}

	/**
	 * Remove all the exceptions which the keys contain the argument.
	 * 
	 * @param subKey
	 */
	public static void removeAllExceptions( String subKey )
	{
		boolean removed = false;

		Iterator<String> iter = errors.keySet( ).iterator( );
		List<String> needToRemove = new ArrayList<String>( 2 );
		while ( iter.hasNext( ) )
		{
			String key = iter.next( );
			if ( key.indexOf( subKey ) > -1 )
			{
				needToRemove.add( key );
			}
		}
		for ( String s : needToRemove )
		{
			String e = errors.get( s );
			if ( e != null && e.equals( WizardBase.getErrors( ) ) )
			{
				WizardBase.removeException( );
				removed = true;
			}
			errors.remove( s );
		}

		// show other unfixed exceptions
		if ( ( removed || WizardBase.getErrors( ) == null )
				&& errors.size( ) > 0 )
		{
			String es = errors.values( ).toArray( new String[errors.size( )] )[0];
			WizardBase.showException( es );
		}
	}
}
