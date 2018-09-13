package com.ardublock.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.ardublock.core.Context;
import com.ardublock.ui.listener.ArdublockWorkspaceListener;
import com.ardublock.ui.listener.GenerateCodeButtonListener;
import com.ardublock.ui.listener.NewButtonListener;
import com.ardublock.ui.listener.OpenButtonListener;
import com.ardublock.ui.listener.OpenblocksFrameListener;
import com.ardublock.ui.listener.SaveAsButtonListener;
import com.ardublock.ui.listener.SaveButtonListener;

import edu.mit.blocks.controller.WorkspaceController;
import edu.mit.blocks.workspace.Page;
import edu.mit.blocks.workspace.PageChangeEventManager;
import edu.mit.blocks.workspace.Workspace;


public class OpenblocksFrame extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2841155965906223806L;

	private Context context;
	private JFileChooser fileChooser;
	private FileFilter ffilter;
	
	private boolean workspaceExpertState = false;
	
	private ResourceBundle uiMessageBundle;
	
	public void addListener(OpenblocksFrameListener ofl)
	{
		context.registerOpenblocksFrameListener(ofl);
	}
	
	public String makeFrameTitle()
	{
		String title = Context.APP_NAME + " " + context.getSaveFileName();
		if (context.isWorkspaceChanged())
		{
			title = title + " *";
		}
		return title;
	}
	
	public OpenblocksFrame()
	{
		context = Context.getContext();
		this.setTitle(makeFrameTitle());
		this.setSize(new Dimension(1024, 760));
		this.setLayout(new BorderLayout());
		//put the frame to the center of screen
		this.setLocationRelativeTo(null);
		//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		uiMessageBundle = ResourceBundle.getBundle("com/ardublock/block/ardublock");
		
		fileChooser = new JFileChooser();
		ffilter = new FileNameExtensionFilter(uiMessageBundle.getString("ardublock.file.suffix"), "abp");
		fileChooser.setFileFilter(ffilter);
		fileChooser.addChoosableFileFilter(ffilter);
		
		initOpenBlocks();
	}
	
	private void initOpenBlocks()
	{
		final Context context = Context.getContext();
		
		/*
		WorkspaceController workspaceController = context.getWorkspaceController();
		JComponent workspaceComponent = workspaceController.getWorkspacePanel();
		*/
		
		final Workspace workspace = context.getWorkspace();
		
		// WTF I can't add workspacelistener by workspace controller
		workspace.addWorkspaceListener(new ArdublockWorkspaceListener(this));
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout());
		JButton newButton = new JButton(uiMessageBundle.getString("ardublock.ui.new"));
		newButton.addActionListener(new NewButtonListener(this));
		JButton saveButton = new JButton(uiMessageBundle.getString("ardublock.ui.save"));
		saveButton.addActionListener(new SaveButtonListener(this));
		JButton saveAsButton = new JButton(uiMessageBundle.getString("ardublock.ui.saveAs"));
		saveAsButton.addActionListener(new SaveAsButtonListener(this));
		JButton openButton = new JButton(uiMessageBundle.getString("ardublock.ui.load"));
		openButton.addActionListener(new OpenButtonListener(this));
		JButton generateButton = new JButton(uiMessageBundle.getString("ardublock.ui.upload"));
		generateButton.addActionListener(new GenerateCodeButtonListener(this, context));
		JButton serialMonitorButton = new JButton(uiMessageBundle.getString("ardublock.ui.serialMonitor"));
		serialMonitorButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				context.getEditor().handleSerial();
			}
		});

		
		topPanel.add(newButton);
		topPanel.add(saveButton);
		topPanel.add(saveAsButton);
		topPanel.add(openButton);
		topPanel.add(generateButton);
		topPanel.add(serialMonitorButton);
		
		//SAVE IMAGE BUTTON
		//*****************************************
		JPanel bottomPanel = new JPanel();
		JButton saveImageButton = new JButton(uiMessageBundle.getString("ardublock.ui.saveImage"));
		saveImageButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				Dimension size = workspace.getCanvasSize();
				Color transparent = new Color(0, 0, 0, 0);
				Page page = workspace.getPageNamed("Main");
				page.setPageName(""); //set page name to NULL to remove text in background 
				
				//System.out.println("size: " + size);
				BufferedImage bi = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB); //letsgoING

				Graphics2D g = (Graphics2D)bi.createGraphics();
				g.setBackground(transparent);
				//double theScaleFactor = (300d/72d); 
				//g.scale(theScaleFactor,theScaleFactor);

				workspace.getBlockCanvas().getPageAt(0).getJComponent().paint(g);
				
				//CHANGE BACKGROUND COLOR FOR PRINT
				//*****************************************
				int maxPixelX = 0;
				int maxPixelY = 0;
				
			    for( int x = 0; x < bi.getWidth(); x++ ) {          // loop through the pixels
			        for( int y = 0; y < bi.getHeight(); y++ ) {
			            Color pixelColor = new Color(bi.getRGB(x, y));
			            if( pixelColor.getRed() == 128 && pixelColor.getGreen() == 128 && pixelColor.getBlue() == 128 ) {    
			            	bi.setRGB(x, y,transparent.getRGB());
			            }
			            else{ //GET SIZE OF BLOCKS ON IMAGE
			            	maxPixelX = Math.max(x,maxPixelX); 
							maxPixelY =  Math.max(y,maxPixelY);
			            }
			        }
			    }
			    
			    //System.out.println("Max X: " + maxPixelX +"  Max Y: " + maxPixelY +"Image W: " + bi.getWidth() +  "Image H: " +bi.getHeight());
			    
			    BufferedImage ci = bi.getSubimage(0, 0, Math.min(maxPixelX+20, bi.getWidth()), Math.min(maxPixelY+20, bi.getHeight())); 
			    page.setPageName("Main"); //restore page name
				//*****************************************
			    
				try{
					final JFileChooser fc = new JFileChooser();
					fc.setSelectedFile(new File("ardublock.png"));
					int returnVal = fc.showSaveDialog(workspace.getBlockCanvas().getJComponent());
			        if (returnVal == JFileChooser.APPROVE_OPTION) {
			            File file = fc.getSelectedFile();
						ImageIO.write(ci,"png",file); //////
			        }
				} catch (Exception e1) {
					
				} finally {
					g.dispose();
				}
			}
		});
		//*****************************************
		
		//LMS SITE BUTTON
		//*****************************************
		JButton lmssiteButton = new JButton(uiMessageBundle.getString("ardublock.ui.lmssite"));
		lmssiteButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
			    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			    URL url;
			    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			        try {
			        	url = new URL(uiMessageBundle.getString("ardublock.ui.lmssite.domain"));
			            desktop.browse(url.toURI());
			        } catch (Exception e1) {
			            e1.printStackTrace();
			        }
			    }
			}
		});
		//*****************************************
		
		//BLOCK REFERENCE BUTTON
		//*****************************************
		JButton blockreferenceButton = new JButton(uiMessageBundle.getString("ardublock.ui.blockReference"));
		blockreferenceButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
			    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			    URL url;
			    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			        try {
			        	url = new URL(uiMessageBundle.getString("ardublock.ui.blockReference.domain"));
			            desktop.browse(url.toURI());
			        } catch (Exception e1) {
			            e1.printStackTrace();
			        }
			    }
			}
		});
		//*****************************************
		
		// VERSION LABEL
		//*****************************************
		JLabel versionLabel = new JLabel(uiMessageBundle.getString("ardublock.ui.version"));
		//*****************************************
		
		//ADD ZOOM BUTTONS
		//*****************************************
		JButton zoomOutButton = new JButton("-");
		zoomOutButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				
				double zoomLevel = workspace.getCurrentWorkspaceZoom();
				
				if(zoomLevel > 0.6){
					zoomLevel -= 0.1;
					workspace.setWorkspaceZoom(zoomLevel);
					PageChangeEventManager.notifyListeners();
				}
			}
		});
		
		JButton zoomInButton = new JButton("+");
		zoomInButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				
				double zoomLevel = workspace.getCurrentWorkspaceZoom();
				
				if(zoomLevel < 1.5){
					zoomLevel += 0.1;
					workspace.setWorkspaceZoom(zoomLevel);
					PageChangeEventManager.notifyListeners();
				}
			}
		});
		
		JButton zoomResetButton = new JButton("0");
		zoomResetButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				workspace.setWorkspaceZoomToDefault();
				PageChangeEventManager.notifyListeners();
			}
		});
		//*****************************************	
	
		//SWITCH BLOCK MENU EXPERT/STANDARD 
		//*****************************************
		JButton expertButton = new JButton(uiMessageBundle.getString("ardublock.ui.modeButton.modeExpert"));
		expertButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
			    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			        try {
			        	WorkspaceController workspaceController = context.getWorkspaceController();
			        if(!workspaceExpertState){
			        	workspaceController.loadProject(getArduBlockString(), null , "custom");	
			        	expertButton.setText(uiMessageBundle.getString("ardublock.ui.modeButton.modeStandard"));
			        }
			        else{
			        	workspaceController.loadProject(getArduBlockString(), null , "default");
			        	expertButton.setText(uiMessageBundle.getString("ardublock.ui.modeButton.modeExpert"));
			        }
			        workspaceExpertState=!workspaceExpertState;
			        
			        } catch (Exception e1) {
			            e1.printStackTrace();
			        }
			    }
			}
		});
		//*****************************************
		
		//WEBSITE BUTTON
		//*****************************************
		JButton websiteButton = new JButton(uiMessageBundle.getString("ardublock.ui.website"));
		websiteButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
			    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			    URL url;
			    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			        try {
			        	url = new URL(uiMessageBundle.getString("ardublock.ui.website.domain"));
			            desktop.browse(url.toURI());
			        } catch (Exception e1) {
			            e1.printStackTrace();
			        }
			    }
			}
		});
		//*****************************************
		
		bottomPanel.add(lmssiteButton);
		//bottomPanel.add(blockreferenceButton);
		bottomPanel.add(Box.createRigidArea(new Dimension(30, 0)));
		bottomPanel.add(saveImageButton);
		bottomPanel.add(Box.createRigidArea(new Dimension(30, 0)));
		bottomPanel.add(zoomInButton);
		bottomPanel.add(zoomResetButton);
		bottomPanel.add(zoomOutButton);
		bottomPanel.add(Box.createRigidArea(new Dimension(30, 0)));
		bottomPanel.add(versionLabel);
		bottomPanel.add(Box.createRigidArea(new Dimension(30, 0)));
		bottomPanel.add(expertButton);
		bottomPanel.add(websiteButton);
		
		this.add(topPanel, BorderLayout.NORTH);
		this.add(bottomPanel, BorderLayout.SOUTH);
		this.add(workspace, BorderLayout.CENTER);
	}
	
	public void doOpenArduBlockFile()
	{
		if (context.isWorkspaceChanged())
		{
			int optionValue = JOptionPane.showOptionDialog(this, uiMessageBundle.getString("message.content.open_unsaved"), uiMessageBundle.getString("message.title.question"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION);
			if (optionValue == JOptionPane.YES_OPTION)
			{
				doSaveArduBlockFile();
				this.loadFile();
			}
			else
			{
				if (optionValue == JOptionPane.NO_OPTION)
				{
					this.loadFile();
				}
			}
		}
		else
		{
			this.loadFile();
		}
		this.setTitle(makeFrameTitle());
		
		resetExpertState();
	}
	
	private void loadFile()
	{
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			File savedFile = fileChooser.getSelectedFile();
			if (!savedFile.exists())
			{
				JOptionPane.showOptionDialog(this, uiMessageBundle.getString("message.file_not_found"), uiMessageBundle.getString("message.title.error"), JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, null, JOptionPane.OK_OPTION);
				return ;
			}
			
			try
			{
				this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				context.loadArduBlockFile(savedFile);
				context.setWorkspaceChanged(false);
			}
			catch (IOException e)
			{
				JOptionPane.showOptionDialog(this, uiMessageBundle.getString("message.file_not_found"), uiMessageBundle.getString("message.title.error"), JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, null, JOptionPane.OK_OPTION);
				e.printStackTrace();
			}
			finally
			{
				this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}
		
		resetExpertState(); //letsgoING
		
	}
	
	public void doSaveArduBlockFile()
	{
		if (!context.isWorkspaceChanged())
		{
			return ;
		}
		
		String saveString = getArduBlockString();
		
		if (context.getSaveFilePath() == null)
		{
			chooseFileAndSave(saveString);
		}
		else
		{
			File saveFile = new File(context.getSaveFilePath());
			writeFileAndUpdateFrame(saveString, saveFile);
		}
	}
	
	public void doSaveAsArduBlockFile()
	{
		if (context.isWorkspaceEmpty())
		{
			return ;
		}
		
		String saveString = getArduBlockString();
		
		chooseFileAndSave(saveString);
		
	}
	
	private void chooseFileAndSave(String ardublockString)
	{
		File saveFile = letUserChooseSaveFile();
		saveFile = checkFileSuffix(saveFile);
		if (saveFile == null)
		{
			return ;
		}
		
		if (saveFile.exists() && !askUserOverwriteExistedFile())
		{
			return ;
		}
		
		writeFileAndUpdateFrame(ardublockString, saveFile);
	}
	
	private String getArduBlockString()
	{
		WorkspaceController workspaceController = context.getWorkspaceController();
		return workspaceController.getSaveString();
	}
	
	private void writeFileAndUpdateFrame(String ardublockString, File saveFile) 
	{
		try
		{
			saveArduBlockToFile(ardublockString, saveFile);
			context.setWorkspaceChanged(false);
			this.setTitle(this.makeFrameTitle());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	private File letUserChooseSaveFile()
	{
		int chooseResult;
		chooseResult = fileChooser.showSaveDialog(this);
		if (chooseResult == JFileChooser.APPROVE_OPTION)
		{
			return fileChooser.getSelectedFile();
		}
		return null;
	}
	
	private boolean askUserOverwriteExistedFile()
	{
		int optionValue = JOptionPane.showOptionDialog(this, uiMessageBundle.getString("message.content.overwrite"), uiMessageBundle.getString("message.title.question"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION);
		return (optionValue == JOptionPane.YES_OPTION);
	}
	
	private void saveArduBlockToFile(String ardublockString, File saveFile) throws IOException
	{
		context.saveArduBlockFile(saveFile, ardublockString);
		context.setSaveFileName(saveFile.getName());
		context.setSaveFilePath(saveFile.getAbsolutePath());
	}
	
	public void doNewArduBlockFile()
	{
		if (context.isWorkspaceChanged())
		{
			int optionValue = JOptionPane.showOptionDialog(this, uiMessageBundle.getString("message.question.newfile_on_workspace_changed"), uiMessageBundle.getString("message.title.question"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION);
			if (optionValue != JOptionPane.YES_OPTION)
			{
				return ;
			}
		}
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		context.resetWorksapce();
		context.setWorkspaceChanged(false);
		this.setTitle(this.makeFrameTitle());
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		
	}
	
	private File checkFileSuffix(File saveFile)
	{
		String filePath = saveFile.getAbsolutePath();
		if (filePath.endsWith(".abp"))
		{
			return saveFile;
		}
		else
		{
			return new File(filePath + ".abp");
		}
	}

	private void resetExpertState(){ //letsgoING
		workspaceExpertState = false; 
	}

}
