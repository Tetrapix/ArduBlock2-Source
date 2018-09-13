package com.ardublock.translator.block.control;

import java.util.LinkedList;
import java.util.List;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;


public class ProgramBlock extends TranslatorBlock
{
	private List<String> setupCommand;
	
	public ProgramBlock(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator);
		setupCommand = new LinkedList<String>();
	}
	
	

	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
	    String ret="";
	    
		TranslatorBlock translatorBlock = getTranslatorBlockAtSocket(0);
		while (translatorBlock != null)
		{
			ret = translatorBlock.toCode();
			translatorBlock = translatorBlock.nextTranslatorBlock();
			if(!ret.contentEquals("")){ //remove empty commands e.g. definition commands
				this.setupCommand.add(ret);
			}
		}
		
		translator.registerBodyTranslateFinishCallback(this);
//		return "";
		ret="";
		ret = "void loop() {\n";
		TranslatorBlock translatorBlock2 = getTranslatorBlockAtSocket(1);
		while (translatorBlock2 != null)
		{
			
			ret = ret + translatorBlock2.toCode();
			translatorBlock2 = translatorBlock2.nextTranslatorBlock();
		}
	
		ret = ret + "}\n\n";
		return ret;
	}
	
	@Override
	public void onTranslateBodyFinished()
	{
		for (String command : setupCommand)
		{
			translator.addSetupCommandForced(command);
		}
	}
	
}
