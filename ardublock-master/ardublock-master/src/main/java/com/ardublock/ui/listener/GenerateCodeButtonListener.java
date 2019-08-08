package com.ardublock.ui.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.ardublock.core.Context;
import com.ardublock.translator.AutoFormat;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.exception.BlockException;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNameDuplicatedException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;
import com.ardublock.ui.OpenblocksFrame;

import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.renderable.RenderableBlock;
import edu.mit.blocks.workspace.Workspace;

public class GenerateCodeButtonListener implements ActionListener
{
	private JFrame parentFrame;
	private Context context;
	private Workspace workspace; 
	private ResourceBundle uiMessageBundle;
	private OpenblocksFrame openBlocksFrame;
	
	public GenerateCodeButtonListener(JFrame frame, OpenblocksFrame obFrame, Context context)
	{
		this.parentFrame = frame;
		this.openBlocksFrame=obFrame;
		this.context = context;
		workspace = context.getWorkspaceController().getWorkspace();
		uiMessageBundle = ResourceBundle.getBundle("com/ardublock/block/ardublock");
	}
	
	public void actionPerformed(ActionEvent e)
	{
		boolean success;
		success = true;
		Translator translator = new Translator(workspace);
		translator.reset();
		
		//AutoSave on Upload letsgoING
		openBlocksFrame.doSaveArduBlockFile();
		
		Iterable<RenderableBlock> renderableBlocks = workspace.getRenderableBlocks();
		
		Set<RenderableBlock> loopBlockSet = new HashSet<RenderableBlock>();
		Set<RenderableBlock> subroutineBlockSet = new HashSet<RenderableBlock>();
		Set<RenderableBlock> interruptBlockSet = new HashSet<RenderableBlock>();
		Set<RenderableBlock> commentBlockSet = new HashSet<RenderableBlock>();
		StringBuilder code = new StringBuilder();
		
		
		for (RenderableBlock renderableBlock:renderableBlocks)
		{
			Block block = renderableBlock.getBlock();
				
			if (!block.hasPlug() && (Block.NULL.equals(block.getBeforeBlockID())))
			{
				if(block.getGenusName().equals("loop"))
				{
					loopBlockSet.add(renderableBlock);
				}
				if(block.getGenusName().equals("loop1"))
				{
					loopBlockSet.add(renderableBlock);
				}
				if(block.getGenusName().equals("loop2"))
				{
					loopBlockSet.add(renderableBlock);
				}
				if(block.getGenusName().equals("loop3"))
				{
					loopBlockSet.add(renderableBlock);
				}
				if(block.getGenusName().equals("program"))
				{
					loopBlockSet.add(renderableBlock);
				}
				if(block.getGenusName().equals("sketch"))
				{
					loopBlockSet.add(renderableBlock);
				}
				if(block.getGenusName().equals("setup"))
				{
					loopBlockSet.add(renderableBlock);
				}
				if (block.getGenusName().equals("subroutine") || block.getGenusName().equals("subroutine_com"))
				{
					String functionName = block.getBlockLabel().trim();
					try
					{
						translator.addFunctionName(block.getBlockID(), functionName);
					}
					catch (SubroutineNameDuplicatedException e1)
					{
						context.highlightBlock(renderableBlock);
						//find the second subroutine whose name is defined, and make it highlight. though it cannot happen due to constraint of OpenBlocks -_-
						JOptionPane.showMessageDialog(parentFrame, uiMessageBundle.getString("ardublock.translator.exception.subroutineNameDuplicated"), "Error", JOptionPane.ERROR_MESSAGE);
						return ;
					}
					subroutineBlockSet.add(renderableBlock);
				}
				if (block.getGenusName().equals("subroutine_var") || block.getGenusName().equals("subroutine_var_com"))
				{
					String functionName = block.getBlockLabel().trim();
					try
					{
						translator.addFunctionName(block.getBlockID(), functionName);
					}
					catch (SubroutineNameDuplicatedException e1)
					{
						context.highlightBlock(renderableBlock);
						//find the second subroutine whose name is defined, and make it highlight. though it cannot happen due to constraint of OpenBlocks -_-
						JOptionPane.showMessageDialog(parentFrame, uiMessageBundle.getString("ardublock.translator.exception.subroutineNameDuplicated"), "Error", JOptionPane.ERROR_MESSAGE);
						return ;
					}
					subroutineBlockSet.add(renderableBlock);
				}
				if (block.getGenusName().equals("subroutine_varret") || block.getGenusName().equals("subroutine_varret_com"))
				{
					String functionName = block.getBlockLabel().trim();
					try
					{
						translator.addFunctionName(block.getBlockID(), functionName);
					}
					catch (SubroutineNameDuplicatedException e1)
					{
						context.highlightBlock(renderableBlock);
						//find the second subroutine whose name is defined, and make it highlight. though it cannot happen due to constraint of OpenBlocks -_-
						JOptionPane.showMessageDialog(parentFrame, uiMessageBundle.getString("ardublock.translator.exception.subroutineNameDuplicated"), "Error", JOptionPane.ERROR_MESSAGE);
						return ;
					}
					subroutineBlockSet.add(renderableBlock);
				}
				if (block.getGenusName().equals("interrupt"))
				{
					String functionName = block.getBlockLabel().trim();
					try
					{
						translator.addFunctionName(block.getBlockID(), functionName);
					}
					catch (SubroutineNameDuplicatedException e1)
					{
						context.highlightBlock(renderableBlock);
						//find the second subroutine whose name is defined, and make it highlight. though it cannot happen due to constraint of OpenBlocks -_-
						JOptionPane.showMessageDialog(parentFrame, uiMessageBundle.getString("ardublock.translator.exception.subroutineNameDuplicated"), "Error", JOptionPane.ERROR_MESSAGE);
						return ;
					}
					interruptBlockSet.add(renderableBlock);
				}
				if(block.getGenusName().equals("program_comment")) //letsgoING
				{
					commentBlockSet.add(renderableBlock);
				}		
			}
		}
		if (loopBlockSet.size() == 0) {
			JOptionPane.showMessageDialog(parentFrame, uiMessageBundle.getString("ardublock.translator.exception.noLoopFound"), "Error", JOptionPane.ERROR_MESSAGE);
			return ;
		}
		if (loopBlockSet.size() > 1) {
			for (RenderableBlock rb : loopBlockSet)
			{
				context.highlightBlock(rb);
			}
			JOptionPane.showMessageDialog(parentFrame, uiMessageBundle.getString("ardublock.translator.exception.multipleLoopFound"), "Error", JOptionPane.ERROR_MESSAGE);
			return ;
		}

		try
		{
			for (RenderableBlock renderableBlock : commentBlockSet)
			{
				translator.setRootBlockName("program_comment");
				Block commentBlock = renderableBlock.getBlock();
				code.append(translator.translate(commentBlock.getBlockID()));
			}
			
			for (RenderableBlock renderableBlock : loopBlockSet)
			{
				translator.setRootBlockName("loop");
				Block loopBlock = renderableBlock.getBlock();
				code.append(translator.translate(loopBlock.getBlockID()));
			}
			
			for (RenderableBlock renderableBlock : subroutineBlockSet)
			{
				translator.setRootBlockName("subroutine");
				Block subroutineBlock = renderableBlock.getBlock();
				code.append(translator.translate(subroutineBlock.getBlockID()));
			}
			
			for (RenderableBlock renderableBlock : interruptBlockSet)
			{
				translator.setRootBlockName("interrupt");
				Block interruptBlock = renderableBlock.getBlock();
				code.append(translator.translate(interruptBlock.getBlockID()));
			}

			
			translator.beforeGenerateHeader();
			code.insert(0, translator.genreateHeaderCommand());
		}
		catch (SocketNullException e1)
		{
			e1.printStackTrace();
			success = false;
			Long blockId = e1.getBlockId();
			Iterable<RenderableBlock> blocks = workspace.getRenderableBlocks();
			for (RenderableBlock renderableBlock2 : blocks)
			{
				Block block2 = renderableBlock2.getBlock();
				if (block2.getBlockID().equals(blockId))
				{
					context.highlightBlock(renderableBlock2);
					break;
				}
			}
			JOptionPane.showMessageDialog(parentFrame, uiMessageBundle.getString("ardublock.translator.exception.socketNull"), "Error", JOptionPane.ERROR_MESSAGE);
		}
		catch (BlockException e2)
		{
			e2.printStackTrace();
			success = false;
			Long blockId = e2.getBlockId();
			Iterable<RenderableBlock> blocks = workspace.getRenderableBlocks();
			for (RenderableBlock renderableBlock2 : blocks)
			{
				Block block2 = renderableBlock2.getBlock();
				if (block2.getBlockID().equals(blockId))
				{
					context.highlightBlock(renderableBlock2);
					break;
				}
			}
			JOptionPane.showMessageDialog(parentFrame, e2.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		catch (SubroutineNotDeclaredException e3)
		{
			e3.printStackTrace();
			success = false;
			Long blockId = e3.getBlockId();
			Iterable<RenderableBlock> blocks = workspace.getRenderableBlocks();
			for (RenderableBlock renderableBlock3 : blocks)
			{
				Block block2 = renderableBlock3.getBlock();
				if (block2.getBlockID().equals(blockId))
				{
					context.highlightBlock(renderableBlock3);
					break;
				}
			}
			JOptionPane.showMessageDialog(parentFrame, uiMessageBundle.getString("ardublock.translator.exception.subroutineNotDeclared"), "Error", JOptionPane.ERROR_MESSAGE);
			
		}
		
		if (success)
		{
			AutoFormat formatter = new AutoFormat();
			String codeOut = code.toString();
			
			if (context.isNeedAutoFormat)
			{
				codeOut = formatter.format(codeOut);
			}
			
			if (!context.isInArduino())
			{
				System.out.println(codeOut);
			}		
			context.didGenerate(codeOut);
		}
	}
}
